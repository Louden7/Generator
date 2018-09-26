import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Basketball_Lineups {

	public static void main(String[] args) throws IOException {
		
		String Fanduel_File = "FanDuel-NBA-2018-01-31-23338-players-list.csv"; // Download a .csv file from the fanduel website.
		
		try {
			
			final long start_time = System.nanoTime();
			Create_Teams("/Users/dillonlouden/Desktop/" + Fanduel_File); // path for output file
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
		final int FPPG_min = 270;
		double highest_FPTS = 0;
		
	/* Creates a directory if one does not exist */
		Create_Directory("/Volumes/TOSHIBA/Fantasy_Basketball_Lineups"); // path to create a directory
		
	/* Creates the first file and writes the files heading */
		File output_file = new File("/Volumes/TOSHIBA/Fantasy_Basketball_Lineups/Lineup" + file_number + ".csv"); // path for normal .csv file
		File best_output_file = new File("/Volumes/TOSHIBA/Fantasy_Basketball_Lineups/Best_Lineup.csv"); // path for the "best" line up.
		PrintWriter PW = new PrintWriter(new FileWriter(output_file, false));
		PrintWriter best_PW = new PrintWriter(new FileWriter(best_output_file, false));
		String lineup_header = "PG 1 / FPPG / Salary, PG 2 / FPPG / Salary, SG 1 / FPPG / Salary, SG 2 / FPPG / Salary, SF 1 / FPPG / Salary, SF 2 / FPPG / Salary, PF 1 / FPPG / Salary, PF 2 / FPPG / Salary, C / FPPG / Salary, Total Fantasy Points, Total Salary \n";
		PW.write(lineup_header);
		best_PW.write(lineup_header);
		
	/* Reads the .CSV file down loaded from Fanduel.com */
		int games_played_ave = Read_Data(file, FPPG_min, money_cap) - 10;
	
	/* Overwrites the initial data file created to a neat and easy to read format */
		System.out.print("Formatting Data File: ");
		Object position_lists[] = Over_Write(games_played_ave, "/Volumes/TOSHIBA/Fantasy_Basketball_Lineups/data.csv", "/Volumes/TOSHIBA/Fantasy_Basketball_Lineups/data.csv");
		List<String> PG_List = (List<String>) position_lists[0];
		List<String> SG_List = (List<String>) position_lists[1];
		List<String> SF_List = (List<String>) position_lists[2];
		List<String> PF_List = (List<String>) position_lists[3];
		List<String> C_List = (List<String>) position_lists[4];
	/* Tells the user that the gathering of data is complete and the line up generations have started */
		System.out.println("Complete");
		
	/* Starts the generation of all non-repeating line up combinations */
		System.out.println("Generating Lineups: ");
		for(int a = 0; a < PG_List.size(); a++) {
			String PG1 = PG_List.get(a);
			System.out.print("	Creating Lineup File " + file_number + ": ");
			for(int b = a+1; b < PG_List.size(); b++) {
				String PG2 = PG_List.get(b);
				for(int c = 0; c < SG_List.size(); c++) {
					String SG1 = SG_List.get(c);
					for(int d = c+1; d < SG_List.size(); d++) {
						String SG2 = SG_List.get(d);
						for(int e = 0; e < SF_List.size(); e++) {
							String SF1 = SF_List.get(e);
							for(int f = e+1; f < SF_List.size(); f++) {
								String SF2 = SF_List.get(f);
								for(int g = 0; g < PF_List.size(); g++) {
									String PF1 = PF_List.get(g);
									for(int h = g+1; h < PF_List.size(); h++) {
										String PF2 = PF_List.get(h);
										for(int i = 0; i < C_List.size(); i++) {
											String C1 = C_List.get(i);
											
											/* Creates the list for the line up */
											List<String> Lineup = new ArrayList<String>();
											
											/* Checks that both the total line up salary is at or below the cap and that the total fantasy points is equal to or above the minimum value */
											Object salary_check[] = Salary_Check(money_cap, PG1, PG2, SG1, SG2, SF1, SF2, PF1, PF2, C1);
											Object fppg_check[] = FPPG_Check(FPPG_min, PG1, PG2, SG1, SG2, SF1, SF2, PF1, PF2, C1);
											
											if((boolean)salary_check[0] && (boolean)fppg_check[0]) {
												Lineup.add(PG1);
												Lineup.add(PG2);
												Lineup.add(SG1);
												Lineup.add(SG2);
												Lineup.add(SF1);
												Lineup.add(SF2);
												Lineup.add(PF1);
												Lineup.add(PF2);
												Lineup.add(C1);
												Lineup.add(fppg_check[1].toString());
												Lineup.add(salary_check[1].toString());
												PW.write(Lineup.toString() + "\n");
												
												// Added this to test
												if((Double)fppg_check[1] > highest_FPTS) {
													highest_FPTS = (Double)fppg_check[1];
													best_PW.write(Lineup.toString() + "\n");
												}
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
			if(file_number <= PG_List.size()) {
				output_file = new File("/Volumes/TOSHIBA/Fantasy_Basketball_Lineups/Lineup" + file_number + ".csv");
				PW = new PrintWriter(new FileWriter(output_file, false));
				PW.write(lineup_header);
			}
		}
		PW.close();
		best_PW.close();
		System.out.println("All Lineups Generated!");
	}
	
	public static int Read_Data(String file, final int FPPG_min, final int money_cap) throws IOException {
		Scanner scan = new Scanner(new FileReader(file));
		
		PrintWriter data = new PrintWriter(new FileWriter("/Volumes/TOSHIBA/Fantasy_Basketball_Lineups/data.csv", false));
		String line = null;
		String PG = "", SG = "", SF = "", PF = "", C = "";
		
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
			if(line.contains("PG,")) {
				PG += line + "\n";
			}
			else if(line.contains("SG,")) {
				SG += line + "\n";
			}
			else if(line.contains("SF,")) {
				SF += line + "\n";
			}
			else if(line.contains("PF,")) {
				PF += line + "\n";
			}
			else if(line.contains("C,")) {
				C += line + "\n";
			}
			total_games += Integer.parseInt(x[5]);
			data_counter++;
		}
		scan.close();
		data.write(PG);
		data.write(SG);
		data.flush();
		data.write(PF);
		data.write(SF);
		data.write(C);
		data.close();
		
		final int games_played_ave = total_games / data_counter;
		return games_played_ave;
	}
	
	public static Object[] Over_Write(int played_min, String old_file, String new_file) throws IOException {
		Object Lists[] = new Object[5];
		List<String> PG_List = new ArrayList<String>();
		List<String> SG_List = new ArrayList<String>();
		List<String> SF_List = new ArrayList<String>();
		List<String> PF_List = new ArrayList<String>();
		List<String> C_List = new ArrayList<String>();
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
			if(line.contains("PG,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				PG_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("SG,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				SG_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("SF,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				SF_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("PF,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				PF_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
			else if(line.contains("C,")) {
				if(x[3].length() < 5 && !x[3].contains(".")) {
					fp = Float.parseFloat(x[3].replace("-", ".000").substring(1, 6));
				}
				else if(x[3].length() < 5) {
					fp = Float.parseFloat(x[3].replace("-", "000").substring(1, 6));
				}
				else {
					fp = Float.parseFloat(x[3].replace("-", "0").substring(1, 6));
				}
				C_List.add(x[1] + " / " + fp + " / " + x[4]);
			}
		}
		scan.close();
		Lists[0] = PG_List;
		Lists[1] = SG_List;
		Lists[2] = SF_List;
		Lists[3] = PF_List;
		Lists[4] = C_List;
		return Lists;
	}
	
	public static Object[] Salary_Check(int money_cap, String PG1, String PG2, String SG1, String SG2, String SF1, String SF2, String PF1, String PF2, String C1) {
		Object info[] = new Object[2];
		info[0] = false;
		
		int money_total = Integer.parseInt(PG1.substring(PG1.indexOf("$")+1, PG1.lastIndexOf(" "))) + 
				Integer.parseInt(PG2.substring(PG2.indexOf("$")+1, PG2.lastIndexOf(" "))) +
				Integer.parseInt(SG1.substring(SG1.indexOf("$")+1, SG1.lastIndexOf(" "))) +
				Integer.parseInt(SG2.substring(SG2.indexOf("$")+1, SG2.lastIndexOf(" "))) +
				Integer.parseInt(SF1.substring(SF1.indexOf("$")+1, SF1.lastIndexOf(" "))) +
				Integer.parseInt(SF2.substring(SF2.indexOf("$")+1, SF2.lastIndexOf(" "))) +
				Integer.parseInt(PF1.substring(PF1.indexOf("$")+1, PF1.lastIndexOf(" "))) +
				Integer.parseInt(PF2.substring(PF2.indexOf("$")+1, PF2.lastIndexOf(" "))) +
				Integer.parseInt(C1.substring(C1.indexOf("$")+1, C1.lastIndexOf(" ")));
		
		if(money_total <= money_cap) {
			info[0] = true;
			info[1] = money_total;
		}
		
		return info;
	}
	
	public static Object[] FPPG_Check(double FPPG_min, String PG1, String PG2, String SG1, String SG2, String SF1, String SF2, String PF1, String PF2, String C1) {
		Object info[] = new Object[2];
		info[0] = false;
		
		double total_FPPG = Double.parseDouble(PG1.substring(PG1.indexOf("/")+1, PG1.lastIndexOf("/"))) + 
				Double.parseDouble(PG2.substring(PG2.indexOf("/")+1, PG2.lastIndexOf("/"))) +
				Double.parseDouble(SG1.substring(SG1.indexOf("/")+1, SG1.lastIndexOf("/"))) +
				Double.parseDouble(SG2.substring(SG2.indexOf("/")+1, SG2.lastIndexOf("/"))) +
				Double.parseDouble(SF1.substring(SF1.indexOf("/")+1, SF1.lastIndexOf("/"))) +
				Double.parseDouble(SF2.substring(SF2.indexOf("/")+1, SF2.lastIndexOf("/"))) +
				Double.parseDouble(PF1.substring(PF1.indexOf("/")+1, PF1.lastIndexOf("/"))) +
				Double.parseDouble(PF2.substring(PF2.indexOf("/")+1, PF2.lastIndexOf("/"))) +
				Double.parseDouble(C1.substring(C1.indexOf("/")+1, C1.lastIndexOf("/")));
		
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