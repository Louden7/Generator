import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Football_Lineups {

	public static void main(String[] args) throws IOException {
		
		try {
			
			final long start_time = System.nanoTime();
			Create_Teams("/Users/dillonlouden/Desktop/FanDuel-NFL-2017-12-24-22515-players-list.csv");
			final long program_duration = System.nanoTime() - start_time;
			System.out.print("Program run time: ");
			Run_Time(program_duration);
			
		}
		catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void Create_Teams(String file) throws IOException {
		int file_number = 1;
		final int money_cap = 60000;
		final int FPPG_min = 95;
		
	/* Creates a directory if one does not exist */
		Create_Directory("/Volumes/TOSHIBA/Fantasy_Football_Lineups");
		
	/* Creates the first file and writes the files heading */
		File output_file = new File("/Volumes/TOSHIBA/Fantasy_Football_Lineups/Lineup" + file_number + ".csv");
		PrintWriter PW = new PrintWriter(new FileWriter(output_file, false));
		String lineup_header = "QB / FPPG / Salary, RB 1 / FPPG / Salary, RB 2 / FPPG / Salary, WR 1 / FPPG / Salary, WR 2 / FPPG / Salary, WR 3 / FPPG / Salary, TE / FPPG / Salary, K / FPPG / Salary, D / FPPG / Salary, Total Fantasy Points, Total Salary \n";
		PW.write(lineup_header);
		
	/* Reads the .CSV file down loaded from Fanduel.com */
		int games_played_ave = Read_Data(file, FPPG_min, money_cap);
	
	/* Overwrites the initial data file created to a neat and easy to read format */
		System.out.print("Formatting Data File: ");
		Object position_lists[] = Over_Write(games_played_ave, "/Volumes/TOSHIBA/Fantasy_Football_Lineups/data.csv", "/Volumes/TOSHIBA/Fantasy_Football_Lineups/data.csv");
		List<String> QB_List = (List<String>) position_lists[0];
		List<String> RB_List = (List<String>) position_lists[1];
		List<String> WR_List = (List<String>) position_lists[2];
		List<String> TE_List = (List<String>) position_lists[3];
		List<String> K_List = (List<String>) position_lists[4];
		List<String> D_List = (List<String>) position_lists[5];
	/* Tells the user that the gathering of data is complete and the line up generations have started */
		System.out.println("Complete");
		
	/* Starts the generation of all non-repeating line up combinations */
		System.out.println("Generating Lineups: ");
		for(int a = 0; a < QB_List.size(); a++) {
			String QB = QB_List.get(a);
			System.out.print("	Creating Lineup File " + file_number + ": ");
			for(int b = 0; b < RB_List.size(); b++) {
				String RB1 = RB_List.get(b);
				for(int c = b+1; c < RB_List.size(); c++) {
					String RB2 = RB_List.get(c);
					for(int d = 0; d < WR_List.size(); d++) {
						String WR1 = WR_List.get(d);
						for(int e = d+1; e < WR_List.size(); e++) {
							String WR2 = WR_List.get(e);
							for(int f = e+1; f < WR_List.size(); f++) {
								String WR3 = WR_List.get(f);
								for(int g = 0; g < TE_List.size(); g++) {
									String TE = TE_List.get(g);
									for(int h = 0; h < K_List.size(); h++) {
										String K = K_List.get(h);
										for(int i = 0; i < D_List.size(); i++) {
											String D = D_List.get(i);
											
											//System.out.println(QB + RB1 + RB2 + WR1 + WR2 + WR3 + TE + K + D);
											
											/* Creates the list for the line up */
											List<String> Lineup = new ArrayList<String>();
											
											/* Checks that both the total line up salary is at or below the cap and that the total fantasy points is equal to or above the minimum value */
											Object salary_check[] = Salary_Check(money_cap, QB, RB1, RB2, WR1, WR2, WR3, TE, K, D);
											Object fppg_check[] = FPPG_Check(FPPG_min, QB, RB1, RB2, WR1, WR2, WR3, TE, K, D);
											
											if((boolean)salary_check[0] && (boolean)fppg_check[0]) {
												Lineup.add(QB);
												Lineup.add(RB1);
												Lineup.add(RB2);
												Lineup.add(WR1);
												Lineup.add(WR2);
												Lineup.add(WR3);
												Lineup.add(TE);
												Lineup.add(K);
												Lineup.add(D);
												Lineup.add(fppg_check[1].toString());
												Lineup.add(salary_check[1].toString());
												//System.out.println(Lineup.toString());
												PW.write(Lineup.toString() + "\n");
											}
										}
									}
								}
								PW.flush();
							}
						}
					}
				}
			}
			System.out.println("Complete");
			if(output_file.length() <= 250) {
				output_file.delete();
			}
			file_number++;
			if(file_number <= QB_List.size()) {
				output_file = new File("/Volumes/TOSHIBA/Fantasy_Football_Lineups/Lineup" + file_number + ".csv");
				PW = new PrintWriter(new FileWriter(output_file, false));
				PW.write(lineup_header);
			}
		}
		PW.close();
		System.out.println("All Lineups Generated!");
	}
	
	public static int Read_Data(String file, final int FPPG_min, final int money_cap) throws IOException {
		Scanner scan = new Scanner(new FileReader(file));
		
		PrintWriter data = new PrintWriter(new FileWriter("/Volumes/TOSHIBA/Fantasy_Football_Lineups/data.csv", false));
		String line = null;
		String QB = "", RB = "", WR = "", TE = "", K = "", D = "";
		
		int total_games = 0;
		int data_counter = 0;
		
		scan.nextLine();
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			line = line.replaceAll("\"", "");
			line = line.substring(line.indexOf(",") + 1);
			String x[] = line.split(",");
			if(x.length >= 11 && x[10].equals("O") && (scan.hasNextLine())) {
				line = scan.nextLine().replaceAll("\"", "");
				line = line.substring(line.indexOf(",") + 1);
				x = line.split(",");
			}
			double dollar_per_points = Double.parseDouble(x[6]) / Double.parseDouble(x[4]);
			double what_to_spend = Double.parseDouble(x[4]) / FPPG_min * money_cap;
			line = x[0] + "," + x[2] + "," + x[5] + ",#" + x[4] + "-,$" + x[6] + " ," + x[7] + "," + x[8] + "," + Double.toString(dollar_per_points) + "," + Double.toString(what_to_spend);
			if(line.contains("QB,")) {
				QB += line + "\n";
			}
			else if(line.contains("RB,")) {
				RB += line + "\n";
			}
			else if(line.contains("WR,")) {
				WR += line + "\n";
			}
			else if(line.contains("TE,")) {
				TE += line + "\n";
			}
			else if(line.contains("K,")) {
				K += line + "\n";
			}
			else if(line.contains("D,")) {
				D += line + "\n";
			}
			total_games += Integer.parseInt(x[5]);
			data_counter++;
		}
		scan.close();
		data.write(QB);
		data.write(RB);
		data.flush();
		data.write(WR);
		data.write(TE);
		data.write(K);
		data.write(D);
		data.close();
		
		final int games_played_ave = total_games / data_counter;
		return games_played_ave;
	}
	
	public static Object[] Over_Write(int played_min, String old_file, String new_file) throws IOException {
		Object Lists[] = new Object[6];
		List<String> QB_List = new ArrayList<String>();
		List<String> RB_List = new ArrayList<String>();
		List<String> WR_List = new ArrayList<String>();
		List<String> TE_List = new ArrayList<String>();
		List<String> K_List = new ArrayList<String>();
		List<String> D_List = new ArrayList<String>();
		
 		Scanner scan = new Scanner(new FileReader(old_file));
		String line = "";
		String file = "Position,Name,Games Played,FPPG,Salary,Opponent,Team,Salary/FPPG,What to spend" + "\n";
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			String x[] = line.split(",");
			if(Integer.parseInt(x[2]) >= played_min) {
				file += line + "\n";
			}
			
		}
		PrintWriter data = new PrintWriter(new FileWriter(new_file, false));
		data.write(file);
		data.close();
		scan.close();
		scan = new Scanner(new FileReader(new_file));
		scan.nextLine();
		float fp;
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			String x[] = line.split(",");
			if(line.contains("QB,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				QB_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("RB,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				RB_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("WR,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				WR_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("TE,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				TE_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("K,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				K_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("D,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				D_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
		}
		scan.close();
		Lists[0] = QB_List;
		Lists[1] = RB_List;
		Lists[2] = WR_List;
		Lists[3] = TE_List;
		Lists[4] = K_List;
		Lists[5] = D_List;
		return Lists;
	}
	
	public static Object[] Salary_Check(int money_cap, String QB, String RB1, String RB2, String WR1, String WR2, String WR3, String TE, String K, String D) {
		Object info[] = new Object[2];
		info[0] = false;
		
		int money_total = Integer.parseInt(QB.substring(QB.indexOf("$")+1, QB.lastIndexOf(" "))) + 
				Integer.parseInt(RB1.substring(RB1.indexOf("$")+1, RB1.lastIndexOf(" "))) +
				Integer.parseInt(RB2.substring(RB2.indexOf("$")+1, RB2.lastIndexOf(" "))) +
				Integer.parseInt(WR1.substring(WR1.indexOf("$")+1, WR1.lastIndexOf(" "))) +
				Integer.parseInt(WR2.substring(WR2.indexOf("$")+1, WR2.lastIndexOf(" "))) +
				Integer.parseInt(WR3.substring(WR3.indexOf("$")+1, WR3.lastIndexOf(" "))) +
				Integer.parseInt(TE.substring(TE.indexOf("$")+1, TE.lastIndexOf(" "))) +
				Integer.parseInt(K.substring(K.indexOf("$")+1, K.lastIndexOf(" "))) +
				Integer.parseInt(D.substring(D.indexOf("$")+1, D.lastIndexOf(" ")));
		
		if(money_total <= money_cap) {
			info[0] = true;
			info[1] = money_total;
		}
		
		return info;
	}
	
	public static Object[] FPPG_Check(double FPPG_min, String QB, String RB1, String RB2, String WR1, String WR2, String WR3, String TE, String K, String D) {
		Object info[] = new Object[2];
		info[0] = false;
		
		double total_FPPG = Double.parseDouble(QB.substring(QB.indexOf("/")+1, QB.lastIndexOf("/"))) + 
				Double.parseDouble(RB1.substring(RB1.indexOf("/")+1, RB1.lastIndexOf("/"))) +
				Double.parseDouble(RB2.substring(RB2.indexOf("/")+1, RB2.lastIndexOf("/"))) +
				Double.parseDouble(WR1.substring(WR1.indexOf("/")+1, WR1.lastIndexOf("/"))) +
				Double.parseDouble(WR2.substring(WR2.indexOf("/")+1, WR2.lastIndexOf("/"))) +
				Double.parseDouble(WR3.substring(WR3.indexOf("/")+1, WR3.lastIndexOf("/"))) +
				Double.parseDouble(TE.substring(TE.indexOf("/")+1, TE.lastIndexOf("/"))) +
				Double.parseDouble(K.substring(K.indexOf("/")+1, K.lastIndexOf("/"))) +
				Double.parseDouble(D.substring(D.indexOf("/")+1, D.lastIndexOf("/")));
		
		if(total_FPPG >= FPPG_min) {
			info[0] = true;
			info[1] = total_FPPG;
		}
		
		return info;
	}
	
	public static void Create_Directory(String Directory_Path_Name) {
		System.out.print("Creating Directory: ");
		File Directory = new File(Directory_Path_Name);	
		if(!Directory.exists()) {
			Directory.mkdir();
		}
		System.out.println("Complete");
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
