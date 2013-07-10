package database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WizardSpellFetcher {

	private final String abjuration = "toc-abjuration";
	private final String evocation = "toc-evocation";
	private final String conjuration = "toc-conjuration";
	private final String transmutation = "toc-transmutation";
	private final String divination = "toc-divination";
	private final String enchantment = "toc-enchantment";
	private final String necromancy = "toc-necromancy";
	private final String illusion = "toc-illusion";

	private final String level0 = "0-level sorcerer/wizard spells";
	private final String level1 = "1st-level sorcerer/wizard spells";
	private final String level2 = "2nd-level sorcerer/wizard spells";
	private final String level3 = "3rd-level sorcerer/wizard spells";
	private final String level4 = "4th-level sorcerer/wizard spells";
	private final String level5 = "5th-level sorcerer/wizard spells";
	private final String level6 = "6th-level sorcerer/wizard spells";
	private final String level7 = "7th-level sorcerer/wizard spells";
	private final String level8 = "8th-level sorcerer/wizard spells";
	private final String level9 = "9th-level sorcerer/wizard spells";
	
	private final String dir = System.getProperty("user.dir");

	private DatabaseController dbController;
	private final String WRITE_SPELL = "INSERT INTO WizardSpells (Name,School,Level,URL) VALUES (?,?,?,?)";

	public WizardSpellFetcher(){
		dbController = new DatabaseController("jdbc:sqlite:" + dir + "\\"+ "Pathfinder.s3db");
	}


	public void fetchWizardSpells(){
		String html;
		int spellLevel;


		try {
			html = Jsoup.connect("http://www.d20pfsrd.com/magic/spell-lists-and-domains/spell-lists---sorcerer-and-wizard").timeout(0).get().html();
			System.out.println("Connected");
			Document doc = Jsoup.parse(html);



			Elements spellLevels = doc.getElementsByTag("h4");

			String levelString = "";
			for(Element tag:spellLevels){
				levelString = tag.getElementsByTag("a").attr("name");
				if(levelString.startsWith("TOC-0")){
					writeSpellsByLevel(doc,0);
				}
				else if(levelString.startsWith("TOC-1")){
					writeSpellsByLevel(doc,1);
				}
				else if(levelString.startsWith("TOC-2")){
					writeSpellsByLevel(doc,2);
				}
				else if(levelString.startsWith("TOC-3")){
					writeSpellsByLevel(doc,3);
				}
				else if(levelString.startsWith("TOC-4")){
					writeSpellsByLevel(doc,4);
				}
				else if(levelString.startsWith("TOC-5")){
					writeSpellsByLevel(doc,5);
				}
				else if(levelString.startsWith("TOC-6")){
					writeSpellsByLevel(doc,6);
				}
				else if(levelString.startsWith("TOC-7")){
					writeSpellsByLevel(doc,7);
				}
				else if(levelString.startsWith("TOC-8")){
					writeSpellsByLevel(doc,8);
				}
				else if(levelString.startsWith("TOC-9")){
					writeSpellsByLevel(doc,9);
				}
			}





		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeSpellsByLevel(Document doc, int spellLevel) throws SQLException{
		String URL;
		String name;
		String temp;

		Element abjurationTable = null;
		Element conjurationTable = null;
		Element evocationTable = null;
		Element divinationTable = null;
		Element necromancyTable = null;
		Element transmutationTable = null;
		Element enchantmentTable = null;
		Element illusionTable = null;

		Elements tables = doc.getElementsByTag("table");
		Elements tableHeaders;
		Elements aTags;
		if(spellLevel == 0){
			//for all spell levels but 0 the level is appended to the table of contents string
			for(Element table:tables){
				tableHeaders = table.getElementsByTag("th");
				for(Element header: tableHeaders){
					aTags = header.getElementsByTag("a");
					//System.out.println(header.toString());
					if(aTags.size() != 0){
						for(Element aTag: aTags){
							temp = aTag.attr("name");
							if(temp.equals("TOC-Abjuration")){
								abjurationTable = table;
							}else if(temp.equals("TOC-Conjuration")){
								conjurationTable = table;
							}else if(temp.equals("TOC-Evocation")){
								evocationTable = table;
							}else if(temp.equals("TOC-Necromancy")){
								necromancyTable = table;
							}else if(temp.equals("TOC-Enchantment")){
								enchantmentTable = table;
							}else if(temp.equals("TOC-Transmutation")){
								transmutationTable = table;
							}else if(temp.equals("TOC-Divination")){
								divinationTable = table;
							}else if(temp.equals("TOC-Illusion")){
								illusionTable = table;
							}
						}
					}
				}
			}
		}else{
			for(Element table:tables){
				tableHeaders = table.getElementsByTag("th");
				for(Element header: tableHeaders){
					aTags = header.getElementsByTag("a");
					//System.out.println(header.toString());
					if(aTags.size() != 0){
						for(Element aTag: aTags){
							temp = aTag.attr("name");
							if(temp.equals("TOC-Abjuration"+ spellLevel)){
								abjurationTable = table;
							}else if(temp.equals("TOC-Conjuration"+ spellLevel)){
								conjurationTable = table;
							}else if(temp.equals("TOC-Evocation"+ spellLevel)){
								evocationTable = table;
							}else if(temp.equals("TOC-Necromancy"+ spellLevel)){
								necromancyTable = table;
							}else if(temp.equals("TOC-Enchantment"+ spellLevel)){
								enchantmentTable = table;
							}else if(temp.equals("TOC-Transmutation"+ spellLevel)){
								transmutationTable = table;
							}else if(temp.equals("TOC-Divination"+ spellLevel)){
								divinationTable = table;
							}else if(temp.equals("TOC-Illusion")){
								illusionTable = table;
							}
						}
					}
				}
			}
		}

		ArrayList<Object> sqlData = new ArrayList<Object>();

		aTags = abjurationTable.getElementsByTag("a");
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic/all-spells")){
				URL = temp;
				name = aTag.text();

				sqlData.add(name);
				sqlData.add("Abjuration");
				sqlData.add(spellLevel);
				sqlData.add(URL);

				dbController.write(WRITE_SPELL, sqlData);

				sqlData.clear();
			}
		}

		aTags = conjurationTable.getElementsByTag("a");
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic/all-spells")){
				URL = temp;
				name = aTag.text();

				sqlData.add(name);
				sqlData.add("Conjuration");
				sqlData.add(spellLevel);
				sqlData.add(URL);

				dbController.write(WRITE_SPELL, sqlData);

				sqlData.clear();
			}
		}

		aTags = transmutationTable.getElementsByTag("a");
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic/all-spells")){
				URL = temp;
				name = aTag.text();

				sqlData.add(name);
				sqlData.add("Transmutation");
				sqlData.add(spellLevel);
				sqlData.add(URL);

				dbController.write(WRITE_SPELL, sqlData);

				sqlData.clear();
			}
		}

		aTags = divinationTable.getElementsByTag("a");
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic/all-spells")){
				URL = temp;
				name = aTag.text();

				sqlData.add(name);
				sqlData.add("Divination");
				sqlData.add(spellLevel);
				sqlData.add(URL);

				dbController.write(WRITE_SPELL, sqlData);

				sqlData.clear();
			}
		}

		aTags = necromancyTable.getElementsByTag("a");
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic/all-spells")){
				URL = temp;
				name = aTag.text();

				sqlData.add(name);
				sqlData.add("Necromancy");
				sqlData.add(spellLevel);
				sqlData.add(URL);

				dbController.write(WRITE_SPELL, sqlData);

				sqlData.clear();
			}
		}

		aTags = enchantmentTable.getElementsByTag("a");
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic/all-spells")){
				URL = temp;
				name = aTag.text();

				sqlData.add(name);
				sqlData.add("Enchantment");
				sqlData.add(spellLevel);
				sqlData.add(URL);

				dbController.write(WRITE_SPELL, sqlData);

				sqlData.clear();
			}
		}

		aTags = evocationTable.getElementsByTag("a");
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic/all-spells")){
				URL = temp;
				name = aTag.text();

				sqlData.add(name);
				sqlData.add("Evcoation");
				sqlData.add(spellLevel);
				sqlData.add(URL);

				dbController.write(WRITE_SPELL, sqlData);

				sqlData.clear();
			}
		}
		
		aTags = illusionTable.getElementsByTag("a");
		for(Element aTag: aTags){
			temp = aTag.attr("href");
			if(temp.startsWith("http://www.d20pfsrd.com/magic/all-spells")){
				URL = temp;
				name = aTag.text();

				sqlData.add(name);
				sqlData.add("Illusion");
				sqlData.add(spellLevel);
				sqlData.add(URL);

				dbController.write(WRITE_SPELL, sqlData);

				sqlData.clear();
			}
		}
	}
}
