
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class BBscrape {

	public static void main(String[] args) throws IOException {
		
		String Website = "";
		PrintWriter PW = new PrintWriter(new FileWriter ("/Users/dillonlouden/Desktop/NBA Shit/NBA Roster.csv", false));
		Scanner scan = new Scanner(new FileReader("/Users/dillonlouden/Desktop/NBA Shit/NBA_Teams.txt"));
		String[] a;
		String b;
		PW.write("Player,Games Played,Minutes Per Game,Rebounds Per Game,Assists Per Game,Steals Per Game,Blocks Per Game,Points Per Game\n");
		while(scan.hasNextLine()) {
			Website = scan.nextLine();
			Document WP = Jsoup.connect(Website).get();
			PW.write(",,,," + Website.substring(40, Website.indexOf("/", 40)).replaceAll("-", " ") + ",,,\n");
			for(Element row : WP.select("tbody tr")) {
				String data = row.select("td").text();
				data = data.replaceFirst(" ", "#").replaceFirst(" ", "-").replace("#", " ").replaceAll(" ", ",").replaceFirst("-", " ");
				a = data.split(",");
				if(a.length == 23) {
					b = a[1] + "," + a[3] + "," + a[4] + "," + a[18] + "," + a[19]+ "," + a[20] + "," + a[21] + "," + a[22];
					PW.write(b + "\n");
				}
			}
		}
		PW.close();
		scan.close();
		System.out.println("Done");
	}
}
