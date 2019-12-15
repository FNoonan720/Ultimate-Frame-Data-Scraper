package ultimateFrameDataScrape;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {

	public static void main(String[] args) throws InterruptedException, IOException {
		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\fnoon\\Documents\\Programming\\Java\\Jars\\ModelScrapeNBA\\chromedriver.exe");
		
		// Creates a Chrome driver instance
		WebDriver driver = new ChromeDriver();

		// Sets implicit wait times for 10 seconds. Then, maximizes driver window
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		ArrayList<String> charList = new ArrayList<String>(Arrays.asList("banjo_and_kazooie", "bayonetta", "bowser", "bowser_jr", "captain_falcon", "chrom", "cloud", "corrin", "daisy", "dark_pit", "dark_samus", "diddy_kong", "donkey_kong", "dr_mario", "duck_hunt", "falco", "fox", "ganondorf", "greninja", "hero", "ice_climbers", "ike", "incineroar", "inkling", "isabelle", "jigglypuff", "joker", "ken", "king_dedede", "king_k_rool", "kirby", "link", "little_mac", "lucario", "lucas", "lucina", "luigi", "mario", "marth", "mega_man", "meta_knight", "mewtwo", "mii_brawler", "mii_gunner", "mii_swordfighter", "mr_game_and_watch", "ness", "olimar", "pac-man", "palutena", "peach", "pichu", "pikachu", "piranha_plant", "pit", "pt_squirtle", "pt_ivysaur", "pt_charizard", "rob", "richter", "ridley", "robin", "rosalina_and_luma", "roy", "ryu", "samus", "sheik", "shulk", "simon", "snake", "sonic", "terry", "toon_link", "villager", "wario", "wii_fit_trainer", "wolf", "yoshi", "young_link", "zelda", "zero_suit_samus"));

		ArrayList<Document> docList = new ArrayList<Document>();
		
		for(int i = 0; i < charList.size(); i++) {
			driver.get("https://ultimateframedata.com/" + charList.get(i) + ".php");
			Thread.sleep(250);
			
			docList.add(Jsoup.parse(driver.getPageSource()));
		}
		
		driver.close();
		
		ArrayList<String> charNames = new ArrayList<String>();
		ArrayList<ArrayList<String>> moveNameList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> moveStartupList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> moveAdvList = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < docList.size(); i++) {
			String charName = docList.get(i).getElementsByClass("charactername").text();
			charNames.add(charName);
			Elements moveNameEls = docList.get(i).getElementsByClass("movename");
			Elements moveStartupEls = docList.get(i).getElementsByClass("startup");
			Elements moveAdvEls = docList.get(i).getElementsByClass("advantage");
			ArrayList<String> moveNameTemp = new ArrayList<String>();
			ArrayList<String> moveStartupTemp = new ArrayList<String>();
			ArrayList<String> moveAdvTemp = new ArrayList<String>();
			for(int j = 0; j < moveNameEls.size(); j++) {
				moveNameTemp.add(moveNameEls.get(j).text());
			}
			for(int j = 0; j < moveStartupEls.size(); j++) {
				moveStartupTemp.add(moveStartupEls.get(j).text());
			}
			for(int j = 0; j < moveAdvEls.size(); j++) {
				moveAdvTemp.add(moveAdvEls.get(j).text());
			}
			
			// Accounts for Donkey Kong having a different # of throws than every other character
			int numThrows;
			if(charName.equals("donkey kong")) { numThrows = 7; }
			else { numThrows = 4; }
			
			// Accounts for Bowser Jr. having a different # of air dodges than every other character
			int numDodges;
			if(charName.equals("Bowser Jr")) { numDodges = 10; }
			else { numDodges = 9; }
			
			// Remove dodges
			for(int k = 0; k < numDodges; k++) {
				moveNameTemp.remove(moveNameTemp.size()-1);
			}
			// Remove throws
			for(int k = 0; k < numThrows; k++) {
				moveNameTemp.remove(moveNameTemp.size()-1);
				moveStartupTemp.remove(moveStartupTemp.size()-1);
			}
			// Add "--" for Adv. on grabs/pummel
			for(int k = 0; k < 4; k++) {
				moveAdvTemp.add("--");
			}
			
			moveNameList.add(moveNameTemp);
			moveStartupList.add(moveStartupTemp);
			moveAdvList.add(moveAdvTemp);
		}
		
		String basePath = new File("").getAbsolutePath();
		File tsvFile = new File(basePath + "\\frame-data.tsv");
		FileWriter fileWriter = new FileWriter(tsvFile);
		
		for(int i = 0; i < charNames.size(); i++) {
			fileWriter.append(charNames.get(i)+"\n");
			System.out.println("printing i = " + i);
			for(int j = 0; j < moveNameList.get(i).size(); j++) {
				fileWriter.append(moveNameList.get(i).get(j)+"\t");
				fileWriter.append(moveStartupList.get(i).get(j)+"\t");
				fileWriter.append(moveAdvList.get(i).get(j)+"\n");
			}
			fileWriter.append("\n");
		}
		
		fileWriter.flush();
		fileWriter.close();
		
		System.out.println("Data can be found in 'frame-data.tsv'.");
		
	}

}
