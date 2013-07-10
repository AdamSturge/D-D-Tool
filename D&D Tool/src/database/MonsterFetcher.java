package database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;

//JSoup library www.http://jsoup.org

/**fetches monster names by type and URLs to their data entries from the pathfinder srd
**writes this data into a database*/

/**This was the first thing I wrote when I was learning JSoup, I should rewrite it using CSS selectors now that I know
 * how to use those
*/
public class MonsterFetcher {

	private final String pathfinderURL = "http://www.d20pfsrd.com";
	private final String Aberration = "toc-aberrations";
	private final String Animal = "toc-animals";
	private final String Construct = "toc-constructs";
	private final String Dragon = "toc-dragons";
	private final String Fey = "toc-fey";
	private final String Humanoid = "toc-humanoids";
	private final String Magical_Beast = "toc-magical-beasts";
	private final String Monsterous_Humanoid = "toc-monstrous-humanoids";
	private final String Ooze = "toc-oozes";
	private final String Outsider = "toc-outsiders";
	private final String Plant = "toc-plants";
	private final String Undead = "toc-undead";
	private final String Vermin = "toc-vermin";
	private final String Template = "toc-templates";
	
	private final String dir = System.getProperty("user.dir");
	
	private final String WRITE_MONSTER = "INSERT INTO Monsters (Name,Type,URL) Values (?,?,?)";
	
	private DatabaseController dbController;
	
	public MonsterFetcher(){
		dbController = new DatabaseController("jdbc:sqlite:" + dir + "\\"+ "Pathfinder.s3db");
	}
	
	public void fetchMonsters(){
		
		/*fetchMonstersByType(Aberration,"Aberration");
		fetchMonstersByType(Animal, "Animal");
		fetchMonstersByType(Construct,"Construct");
		fetchMonstersByType(Dragon,"Dragon");
		fetchMonstersByType(Fey,"Fey");
		fetchMonstersByType(Humanoid,"Humanoid");
		fetchMonstersByType(Magical_Beast,"Magical Beast");*/
		fetchMonstersByType(Monsterous_Humanoid,"Monsterous Humanoid");
		/*fetchMonstersByType(Ooze,"Ooze");
		fetchMonstersByType(Plant,"Plant");
		fetchMonstersByType(Undead,"Undead");
		fetchMonstersByType(Vermin,"Vermin");
		fetchMonstersByType(Template,"Template");
		fetchMonstersByType(Outsider,"Outsider");*/
		
	
	}
	
	public void fetchMonstersByType(String type,String databaseType){
		String html = null;
		int start = 0;
		int end = 0;
		String URL;
		String monsterName;
		int stop = 1;
		try {
			html = Jsoup.connect("http://www.d20pfsrd.com/bestiary/monster-listings").timeout(0).get().html();
			html = html.toLowerCase();
			System.out.println(databaseType +" Connected");

			//Trimming excess html
			start = html.indexOf(type);
			//System.out.println(start);
			html = html.substring(start + type.length()); //throw away useless data
			FileWriter out = new FileWriter("html.txt");
			PrintWriter htmlOut = new PrintWriter(out,false);
			htmlOut.print(html);
			htmlOut.close();
			start = html.indexOf(type);
			html = html.substring(start); //throw away more useless data

			start = html.indexOf("<a href="); //this begins the url
			html = html.substring(start+8); //throw away more useless data
			//Trimming complete

			start = 0; //reset start before looping
			stop = html.indexOf("<a href=\"#top\">back to top" ); 
			
			ArrayList<Object> sqlData = new ArrayList<Object>();
			
			 while(true){
				start = html.indexOf("<a href="); //this begins the url
				if(start >= stop){
					break;
				}
				end = html.indexOf("jotid", start); //the ends the url
				URL = pathfinderURL + html.substring(start+9, end-2);
				
				html = html.substring(end); //remove url
				stop -= end;

				start = html.indexOf(">");
				end = html.indexOf("</a>");

				monsterName = html.substring(start+1, end);
				
				html = html.substring(end+4); //remove Name
				stop -= end+4;
				
				sqlData.add(monsterName);
				sqlData.add(databaseType); 
				sqlData.add(URL);
				try {
					dbController.write(WRITE_MONSTER, sqlData);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sqlData.remove(monsterName);
				sqlData.remove(URL);
				sqlData.remove(sqlData.size()-1);
				
				
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	//THIS IS NOT GOING TO WORK, NOT ENOUGH REGULARITY IN HTML CODE
	public void fetchMonsterData(String URL){
		int start;
		int end;
		String XP,Size,HP,AC,Initiative,Fort,Ref,Will,Speed,Touch,FlatFooted;
		String Melee =null;
		String Ranged = null;
		String temp;
		
		try {
			String html = Jsoup.connect(URL).timeout(0).get().html();
			
			start = html.indexOf("XP");
			end = html.indexOf("</b>", start+7);
			start = html.indexOf("<b>", start+7);
			XP = html.substring(start+3, end);
			
			start = html.indexOf("Init");
			end = html.indexOf("<b>",start);
			Initiative = html.substring(start+9, end);
			
			start = html.indexOf("AC");
			end = html.indexOf(" ", start+7);
			AC = html.substring(start+7, end);
			
			//html = html.substring(end);
			start = html.indexOf("hp");
			end = html.indexOf(" ", start+8);
			HP = html.substring(start+7, end);
			
			//html = html.substring(end);
			start = html.indexOf("Fort");
			end = html.indexOf("<b>", start+9);
			Fort = html.substring(start+9, end-1);
			
			html = html.substring(end);
			start = html.indexOf("Ref");
			end = html.indexOf("<b>", start+9);
			Ref = html.substring(start+9, end-1);
			
			html = html.substring(end);
			start = html.indexOf("Will");
			end = html.indexOf("</font>", start+9);
			Will = html.substring(start+9, end);
			
			html = html.substring(end);
			start = html.indexOf("Speed");
			end = html.indexOf("</font>", start+10);
			Speed = html.substring(start+10, end);
			
			html = html.substring(end);
			start = html.indexOf("Melee");
			if(start >-1){
				end = html.indexOf("</font>", start+11);
				Melee = html.substring(start+11, end);
			}
			
			
			html = html.substring(end);
			start = html.indexOf("Ranged");
			if(start > -1){
				end = html.indexOf("</font>", start+12);
				Ranged = html.substring(start+12, end);
			}
			
			
			System.out.println("xp:" + XP);
			System.out.println("hp: "+ HP);
			System.out.println("ac: " +AC);
			System.out.println("init: "+Initiative);
			System.out.println("fort: "+Fort);
			System.out.println("ref: "+Ref);
			System.out.println("will: "+Will);
			System.out.println("speed: "+Speed);
			System.out.println("melee: " +Melee);
			System.out.println("ranged: "+ Ranged);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
