package database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class ClericSpellFetcher {

	private DatabaseController dbController;
	private final String WRITE_SPELL = "INSERT INTO ClericSpells (Name,School,Level,URL) VALUES (?,?,?,?)";
	private final String READ_SPELL = "SELECT Name FROM ClericSpells WHERE Name = ?";
	
	private final String dir = System.getProperty("user.dir");

	public ClericSpellFetcher(){
		dbController = new DatabaseController("jdbc:sqlite:" + dir + "\\"+ "Pathfinder.s3db");
	}

	public void fetchSpells(){
		String html;

		try {
			html = Jsoup.connect("http://www.d20pfsrd.com/magic/spell-lists-and-domains/spell-lists---cleric").timeout(0).get().html();
			System.out.println("Connected");
			Document doc = Jsoup.parse(html);

			Elements aTags = doc.select("a[href^=http://www.d20pfsrd.com/magic/all-spells/");
			String URL;
			String name;
			String school;
			int spellLevel;
			ArrayList<Object> spellData;
			ArrayList<Object> sqlData = new ArrayList<Object>();
			ResultSet rs;
			for(Element aTag:aTags){
				URL = aTag.attr("href");
				name = aTag.text();
				System.out.println(name + " " + URL);
				spellData = extractSpellData(name,URL);
				for(int i = 0; i < spellData.size(); i+=3){
					name = (String) spellData.get(i);
					school = (String) spellData.get(i+1);
					spellLevel = (Integer) spellData.get(i+2);
					if(name != null &&school != null && spellLevel != -1){
						sqlData.add(name);
						rs = dbController.read(READ_SPELL, sqlData); //checking to avoid storing the same spell multiple times
						if(!rs.next()){
							sqlData.add(school);
							sqlData.add(spellLevel);
							sqlData.add(URL);
							dbController.write(WRITE_SPELL, sqlData);
						}
						sqlData.clear();
					}else{
						System.out.println("failed to retrive data for spell: " + name);
					}


				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ArrayList<Object> extractSpellData(String name, String URL) throws IOException{
		String html = Jsoup.connect(URL).timeout(0).get().html();
		Document doc = Jsoup.parse(html);


		//Figure out the spell school
		Elements aTags = doc.getElementsByTag("a");
		String school = null;
		String temp;
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic#TOC-Necromancy")){
				school = aTag.text();
				break;
			}else if(temp.startsWith("http://www.d20pfsrd.com/magic#TOC-Enchantment")){
				school = aTag.text();
				break;
			}else if(temp.startsWith("http://www.d20pfsrd.com/magic#TOC-Abjuration")){
				school = aTag.text();
				break;
			}else if(temp.startsWith("http://www.d20pfsrd.com/magic#TOC-Evocation")){
				school = aTag.text();
				break;
			}else if(temp.startsWith("http://www.d20pfsrd.com/magic#TOC-Divination")){
				school = aTag.text();
				break;
			}else if(temp.startsWith("http://www.d20pfsrd.com/magic#TOC-Transmutation")){
				school = aTag.text();
				break;
			}else if(temp.startsWith("http://www.d20pfsrd.com/magic#TOC-Conjuration")){
				school = aTag.text();
				break;
			}else if(temp.startsWith("http://www.d20pfsrd.com/magic#TOC-Illusion")){
				school = aTag.text();
				break;
			}
		}


		//Figure out the spell level
		int spellLevel = -1;
		String levelString;
		Scanner in;
		int clericIndex = -1;
		Element tempElement = doc.select("p:has(a[href=http://www.d20pfsrd.com/classes/core-classes/cleric]").first();
		try{
			if(tempElement != null){
				levelString = tempElement.text();

				clericIndex = levelString.indexOf("cleric");
				if(clericIndex == -1){
					clericIndex = levelString.indexOf("oracle");
				}
				levelString= levelString.substring(clericIndex);

				in = new Scanner(levelString).useDelimiter("[^0-9]+");
				spellLevel = in.nextInt();
				//System.out.println(levelString);
			}else {
				tempElement = doc.select("p:has(a[href=http://www.d20pfsrd.com/classes/core-classes/oracle]").first();
				if(tempElement != null){
					levelString = tempElement.text();

					clericIndex = levelString.indexOf("cleric");
					if(clericIndex == -1){
						clericIndex = levelString.indexOf("oracle");
					}
					levelString= levelString.substring(clericIndex);

					in = new Scanner(levelString).useDelimiter("[^0-9]+");
					spellLevel = in.nextInt();
					//System.out.println(levelString);
				}else{
					tempElement = doc.select("p:has(a[href=http://www.d20pfsrd.com/classes/base-classes/oracle]").first();
					levelString = tempElement.text();

					clericIndex = levelString.indexOf("cleric");
					if(clericIndex == -1){
						clericIndex = levelString.indexOf("oracle");
					}
					levelString= levelString.substring(clericIndex);

					in = new Scanner(levelString).useDelimiter("[^0-9]+");
					spellLevel = in.nextInt();
					//System.out.println(levelString);
				}
			}
		}catch(NullPointerException e){
			System.out.println("this is not a cleric spell, someone may have inserted a bad link");
		}

		ArrayList<Object> spellData = new ArrayList<Object>();
		spellData.add(name);
		spellData.add(school);
		spellData.add(spellLevel);


		//Handles the case when there are multiple spells on a single page
		String variantDataString = "";
		String modifiedName = name; //differs from name is some special cases
		Elements siblings;
		Element p = null;
		int summonMonsterIndex = 0;

		if(name.contains("Summon Monster")){
			modifiedName = name.substring(0,15);
		}
		Elements h4Tags = doc.select("h4:has(a[name*="+modifiedName.replace(" ", "-") +"])");
		h4Tags.removeAll(doc.select("h4:has(a[name*=Table")); //remove any tables that got pulled along, happens for summoned monsters
		for(Element h4Tag: h4Tags){
			//found a spell variant on the same page
			System.out.println("Variant Spell Found: " +h4Tag.text() );
			spellData.add(h4Tag.text()); //spell name

			siblings = h4Tag.siblingElements();
			if(name.contains("Summon Monster")){
				Elements elements = siblings.select("*:has(a[href=http://www.d20pfsrd.com/classes/core-classes/cleric])");
				for(Element e: elements){
					System.out.println(e.text());
				}
				if(!h4Tag.text().equals("Summon Monster I") && !h4Tag.text().equals("Summon Monster II")){
					summonMonsterIndex++;
				}
				System.out.println("monster index for this spell is: " + summonMonsterIndex + "\n");
				p = siblings.select("*:has(a[href=http://www.d20pfsrd.com/classes/core-classes/cleric])").get(summonMonsterIndex);
				variantDataString = p.text();
				//want to trim away any classes that come before cleric so we grab the correct spell level
				clericIndex = variantDataString.indexOf("cleric");
				if(clericIndex == -1){
					clericIndex = variantDataString.indexOf("oracle");
				}
				variantDataString = variantDataString.substring(clericIndex);
				System.out.println("variantData: "+ variantDataString);

				spellData.add(school); //add school
				in = new Scanner(variantDataString).useDelimiter("[^0-9]+");
				spellData.add(in.nextInt());
				in.close();

			}else{
				try{
					p = siblings.select("*:has(a[href=http://www.d20pfsrd.com/classes/core-classes/cleric])").first();
					variantDataString = p.text();
					//want to trim away any classes that come before cleric so we grab the correct spell level
					clericIndex = variantDataString.indexOf("cleric");
					if(clericIndex == -1){
						clericIndex = variantDataString.indexOf("oracle");
					}
					variantDataString = variantDataString.substring(clericIndex);
					System.out.println("variantData: "+ variantDataString);

					spellData.add(school); //add school
					in = new Scanner(variantDataString).useDelimiter("[^0-9]+");
					spellData.add(in.nextInt());
					in.close();

				} catch(NullPointerException e){
					System.out.println("Could not find the word cleric in the level string for the spell: " + name);
					spellData.add(""); //add blank school
					spellData.add(-1);
				}
			}

		}

		return spellData;
	}
}

