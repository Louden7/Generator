import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class web_scrap {

	public static void main(String[] args) throws IOException{
		
		Scanner user_input = new Scanner(System.in);
		
		int info[] = Get_Data();
		System.out.println("QBs: " + info[0] + ", RBs: " + info[1] + ", WRs: " + info[2] + ", TEs: " + info[3] + ", K: " + info[4] + ", Ds: " + info[5]);
		System.out.println("Please select how many players at each position you would like to generate lineups for.");
		int user_QBs, user_RBs, user_WRs, user_TEs, user_Ks, user_Ds;
		do {
			System.out.print("QB's: >> " );
			user_QBs = user_input.nextInt();
			if(user_QBs > info[0] || user_QBs <= 0) {
				System.out.println("Must be more than 1 and less than or equal to the number of QBs > " + info[0] + " <");
			}
		}while(user_QBs > info[0] || user_QBs <= 0);
		
		do {
			System.out.print("RB's: >> " );
			user_RBs = user_input.nextInt();
			if(user_RBs > info[1] || user_RBs <= 0) {
				System.out.println("Must be more than 1 and less than or equal to the number of RBs > " + info[1] + " <");
			}
		}while(user_RBs > info[1] || user_RBs <= 0);
		
		do {
			System.out.print("WR's: >> " );
			user_WRs = user_input.nextInt();
			if(user_WRs > info[2] || user_WRs <= 0) {
				System.out.println("Must be more than 1 and less than or equal to the number of WRs > " + info[2] + " <");
			}
		}while(user_WRs > info[2] || user_WRs <= 0);
		
		do {
			System.out.print("TE's: >> " );
			user_TEs = user_input.nextInt();
			if(user_TEs > info[3] || user_TEs <= 0) {
				System.out.println("Must be more than 1 and less than or equal to the number of TEs > " + info[0] + " <");
			}
		}while(user_TEs > info[3] || user_TEs <= 0);
		
		do {
			System.out.print(" K's: >> " );
			user_Ks = user_input.nextInt();
			if(user_Ks > info[4] || user_Ks <= 0) {
				System.out.println("Must be more than 1 and less than or equal to the number of Ks > " + info[0] + " <");
			}
		}while(user_Ks > info[4] || user_Ks <= 0);
		
		do {
			System.out.print(" D's: >> " );
			user_Ds = user_input.nextInt();
			if(user_Ds > info[5] || user_Ds <= 0) {
				System.out.println("Must be more than 1 and less than or equal to the number of Ds > " + info[0] + " <");
			}
		}while(user_Ds > info[5] || user_Ds <= 0);
		user_input.close();
			
		final long start_time = System.nanoTime();
		Get_Team(user_QBs, user_RBs, user_WRs, user_TEs, user_Ks, user_Ds);
		final long program_duration = System.nanoTime() - start_time;
		Run_Time(program_duration);
	}
	
	public static int[] Get_Data(){
		String QB = "", RB = "", WR = "", TE = "", K = "", D = "";
		int QB_rank = 1, RB_rank = 1, WR_rank = 1, TE_rank = 1, K_rank = 1, D_rank = 1;
		int scrapted[] = new int[6];
		System.out.print("Data Scraping: ");
		try{
			Document WP = Jsoup.connect("https://www.rotowire.com/daily/nfl/optimizer.php?site=FanDuel&sport=NFL").get(); //website for teams and players
			File Directory = new File("Fantasy_Lineups");
			
			if(!Directory.exists()) {
				Directory.mkdir();
			}
			
			PrintWriter PW = new PrintWriter(new FileWriter("Fantasy_Lineups/Fantasy_Data.csv", false));
			
			PW.write("Rank, Name, POS, Team, OPP, Spread, O/U, TM/P, ML, OWN%, Value, Salary, FPTS" + System.getProperty("line.separator"));
			
			for (Element row : WP.select("tbody#players tr")){
				String data = row.select("td").text();
				data += row.getElementsByClass("salaryInput").attr("value") + " ";
				data += row.getElementsByClass("ptsInput").attr("value");
				data = data.replaceFirst(" ", "-");
				data = data.replaceAll(",", "");
				data = data.replaceAll("   ", ",");
				data = data.replaceAll(" ", ",");
				data = data.replaceFirst("-", " ");
				data = data.replace(",Q,", " Q,");
				data = data.replace(",D,", " D,");
				
				if(data.contains("QB,") && (data.contains("PHI,") || data.contains("PIT,"))){
					QB += QB_rank + "," + data + System.getProperty("line.separator");
					QB_rank++;
				}
				else if(data.contains("RB,") && (data.contains("PHI,") || data.contains("PIT,"))){
					RB += RB_rank + "," + data + System.getProperty("line.separator"); 
					RB_rank++;
				}
				else if(data.contains("WR,") && (data.contains("PHI,") || data.contains("PIT,"))){
					WR += WR_rank + "," + data + System.getProperty("line.separator");
					WR_rank++;
				}
				else if(data.contains("TE,") && (data.contains("PHI,") || data.contains("PIT,"))){
					TE += TE_rank + "," + data + System.getProperty("line.separator");
					TE_rank++;
				}
				else if(data.contains("K,") && (data.contains("PHI,") || data.contains("PIT,"))){
					K += K_rank + "," + data + System.getProperty("line.separator");
					K_rank++;
				}
				else if(data.contains("D,") && (data.contains("PHI,") || data.contains("PIT,"))){
					D += D_rank + "," + data + System.getProperty("line.separator");
					if(D.contains("Los Angeles,Rams")){
						D = D.replace("Los Angeles,Rams", "Los Angeles Rams");
					}
					else if(D.contains("New England,Patriots")){
						D = D.replace("New England,Patriots", "New England Patriots");
					}
					else if(D.contains("Tampa Bay,Buccaneers")){
						D = D.replace("Tampa Bay,Buccaneers", "Tampa Bay Buccaneers");
					}
					else if(D.contains("Green Bay,Packers")){
						D = D.replace("Green Bay,Packers", "Green Bay Packers");
					}
					else if(D.contains("New York,Giants")){
						D = D.replace("New York,Giants", "New York Giants");
					}
					else if(D.contains("San Francisco,49ers")){
						D = D.replace("San Francisco,49ers", "San Francisco 49ers");
					}
					else if(D.contains("New Orleans,Saints")){
						D = D.replace("New Orleans,Saints", "New Orleans Saints");
					}
					else if(D.contains("New York,Jets")){
						D = D.replace("New York,Jets", "New York Jets");
					}
					else if(D.contains("Los Angeles,Chargers")){
						D = D.replace("Los Angeles,Chargers", "Los Angeles Chargers");
					}
					else if(D.contains("Kansas City,Chiefs")) {
						D = D.replace("Kansas City,Chiefs", "Kansas City Chiefs");
					}
					D = D.replace(" D,", ",D,");
					D_rank++;
				}
			}
			
			PW.write(QB + System.getProperty("line.separator"));
			PW.flush();
			PW.write(RB + System.getProperty("line.separator"));
			PW.flush();
			PW.write(WR + System.getProperty("line.separator"));
			PW.flush();
			PW.write(TE + System.getProperty("line.separator"));
			PW.flush();
			PW.write(K + System.getProperty("line.separator"));
			PW.flush();
			PW.write(D + System.getProperty("line.separator"));
			PW.flush();
			PW.close();
			System.out.println("Complete");
			scrapted[0] = QB_rank-1;
			scrapted[1] = RB_rank-2;
			scrapted[2] = WR_rank-3;
			scrapted[3] = TE_rank-1;
			scrapted[4] = K_rank-1;
			scrapted[5] = D_rank-1;
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return scrapted;
	}
	
	public static void Get_Team(int num_qb, int num_rb, int num_wr, int num_te, int num_k, int num_d) throws IOException{
		final int money_cap = 60000;
		final int FPTS_lower_cap = 100;
		int lineup_number = 1;
		double Highest_FPTS = 0;
		List<String> QB_List = new ArrayList<String>();
		List<String> RB_List = new ArrayList<String>();
		ArrayList<String> WR_List = new ArrayList<String>();
		ArrayList<String> TE_List = new ArrayList<String>();
		ArrayList<String> K_List = new ArrayList<String>();
		ArrayList<String> D_List = new ArrayList<String>();
		int file = 1;
		
		Scanner Scan = new Scanner(new FileReader("Fantasy_Lineups/Fantasy_Data.csv"));
		PrintWriter PW = new PrintWriter(new FileWriter("Fantasy_Lineups/Lineup_" + file + ".csv", false));
		PrintWriter PW_Best_FPTS = new PrintWriter(new FileWriter("Fantasy_Lineups/Best_FPTS_Lineups.csv", false));
		String line = null;
		
		System.out.print("Reading File Data: ");
		while(Scan.hasNextLine()){
			line = Scan.nextLine();
			if(line.contains(",QB,")){
				String a = line.substring(line.indexOf(",", 0), line.indexOf(",QB,")).replace(",", "");
				String b = line.substring(line.indexOf("$")).replace("$", " $").replace(",", " ");
				QB_List.add(a + b);
			}
			else if(line.contains(",RB,")){
				String a = line.substring(line.indexOf(",", 0), line.indexOf(",RB,")).replace(",", "");
				String b = line.substring(line.indexOf("$")).replace("$", " $").replace(",", " ");
				RB_List.add(a + b);
			}
			else if(line.contains(",WR,")){
				String a = line.substring(line.indexOf(",", 0), line.indexOf(",WR,")).replace(",", "");
				String b = line.substring(line.indexOf("$")).replace("$", " $").replace(",", " ");
				WR_List.add(a + b);
			}
			else if(line.contains(",TE,")){
				String a = line.substring(line.indexOf(",", 0), line.indexOf(",TE,")).replace(",", "");
				String b = line.substring(line.indexOf("$")).replace("$", " $").replace(",", " ");
				TE_List.add(a + b);
			}
			else if(line.contains(",K,")){
				String a = line.substring(line.indexOf(",", 0), line.indexOf(",K,")).replace(",", "");
				String b = line.substring(line.indexOf("$")).replace("$", " $").replace(",", " ");
				K_List.add(a + b);
			}
			else if(line.contains(",D,")){
				String a = line.substring(line.indexOf(",", 0), line.indexOf(",D,")).replace(",", "");
				String b = line.substring(line.indexOf("$")).replace("$", " $").replace(",", " ");
				D_List.add(a + b);
			}
		}
		Scan.close();
		System.out.println("Complete");
		
		System.out.print("Generating Line-ups: -> 0%");
		String line_up_header = "Lineup Number, QB, RB, RB2, WR, WR2, WR3, TE, K, D, Total Price, Total FPTS" + System.getProperty("line.separator");
		PW.write(line_up_header);
		PW_Best_FPTS.write(line_up_header);
		
		for(int a = 0; a < num_qb; a++) {
			String QB = QB_List.get(a);
			
			for(int b = 0; b < num_rb; b++) {
				String RB = RB_List.get(b);
				
				for(int c = 0; c < num_rb; c++) {
					String RB2 = RB_List.get(b + 1);
					
					if(RB.equals(RB2) == false) {
						
						for(int d = 0; d < num_wr; d++) {
							String WR = WR_List.get(d);
							
							for(int e = 0; e < num_wr; e++) {
								String WR2 = WR_List.get(d + 1);
								
								if(WR.equals(WR2) == false) {
									
									for(int f = 0; f < num_wr; f++) {
										String WR3 = WR_List.get(e + 1);
										
										if(WR.equals(WR3) == false && WR2.equals(WR3) == false) {
											
											for(int g = 0; g < num_te; g++) {
												String TE = TE_List.get(g);
												
												for(int h = 0; h < num_k; h++) {
													String K = K_List.get(h);
													
													for(int i = 0; i < num_d; i++) {
														String D = D_List.get(i);
														
														List<String> x = new ArrayList<String>();
														
														int money_total = Integer.parseInt(QB.substring(QB.indexOf("$") + 1, QB.lastIndexOf(" "))) 
																+ Integer.parseInt(RB.substring(RB.indexOf("$") + 1, RB.lastIndexOf(" ")))
																+ Integer.parseInt(RB2.substring(RB2.indexOf("$") + 1, RB2.lastIndexOf(" ")))
																+ Integer.parseInt(WR.substring(WR.indexOf("$") + 1, WR.lastIndexOf(" ")))
																+ Integer.parseInt(WR2.substring(WR2.indexOf("$") + 1, WR2.lastIndexOf(" ")))
																+ Integer.parseInt(WR3.substring(WR3.indexOf("$") + 1, WR3.lastIndexOf(" ")))
																+ Integer.parseInt(TE.substring(TE.indexOf("$") + 1, TE.lastIndexOf(" ")))
																+ Integer.parseInt(K.substring(K.indexOf("$") + 1, K.lastIndexOf(" ")))
																+ Integer.parseInt(D.substring(D.indexOf("$") + 1, D.lastIndexOf(" ")));
														
														Double Total_FPTS = Double.parseDouble(QB.substring(QB.lastIndexOf(" ") + 1)) 
																+ Double.parseDouble(RB.substring(RB.lastIndexOf(" ") + 1)) 
																+ Double.parseDouble(RB2.substring(RB2.lastIndexOf(" ") + 1)) 
																+ Double.parseDouble(WR.substring(WR.lastIndexOf(" ") + 1)) 
																+ Double.parseDouble(WR2.substring(WR2.lastIndexOf(" ") + 1)) 
																+ Double.parseDouble(WR3.substring(WR3.lastIndexOf(" ") + 1)) 
																+ Double.parseDouble(TE.substring(TE.lastIndexOf(" ") + 1)) 
																+ Double.parseDouble(K.substring(K.lastIndexOf(" ") + 1)) 
																+ Double.parseDouble(D.substring(D.lastIndexOf(" ") + 1));
														
														if(money_total <= money_cap && Total_FPTS >= FPTS_lower_cap){
															x.add(Long.toString(lineup_number));
															x.add(QB);
															x.add(RB);
															x.add(RB2);
															x.add(WR);
															x.add(WR2);
															x.add(WR3);
															x.add(TE);
															x.add(K);
															x.add(D);
															x.add("$" + Integer.toString(money_total));
															x.add(Double.toString(Total_FPTS));
															String writer = x.toString().replace("[", "").replace("]", "") + "\n";
															PW.write(writer); 
															
															if(Total_FPTS > Highest_FPTS) {
																Highest_FPTS = Total_FPTS;
																PW_Best_FPTS.write(writer);
																PW_Best_FPTS.flush();
															}
															
															PW.flush();
														}
														lineup_number++;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			float percentage = 100 / num_qb;
			System.out.print(" -> " + (a+1)*percentage + "%");
			
			Highest_FPTS = 0;
			if(file == a+1) {
				if(file != num_qb) {
					file++;
					PW = new PrintWriter(new FileWriter("Fantasy_Lineups/Lineup_" + file + ".csv", false));
					PW.write(line_up_header);
				}
			}
		}
		PW.close();
		PW_Best_FPTS.close();
		System.out.print(" -> 100% -> Complete");
		System.out.println();
	}
	
	public static void Run_Time(long nanoseconds) {
		int seconds = (int)(nanoseconds / Math.pow(10, 9));
		int end_seconds = (int)(nanoseconds / Math.pow(10, 9)) % 60;
		int total_minutes = seconds / 60;
		int minutes = total_minutes % 60;
		int hours = total_minutes / 60;
		System.out.println("Hours: " + hours + " Minutes: " + minutes + " Seconds: " + end_seconds);
	}
}
