import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Football_Flex_Lineups {

	public static void main(String[] args) {
		
//		try {
//			
//			final long start_time = System.nanoTime();
//			Create_Teams("/Users/dillonlouden/Desktop/FanDuel-NFL-2017-12-17-22413-players-list.csv");
//			final long program_duration = System.nanoTime() - start_time;
//			System.out.print("Program run time: ");
//			Run_Time(program_duration);
//			
//		}
//		catch (FileNotFoundException e) {
//			
//			e.printStackTrace();
//		}

	}
	
	public static int Read_Data(String file, final int FPPG_min, final int money_cap) throws IOException {
		Scanner scan = new Scanner(new FileReader(file));
		
		PrintWriter data = new PrintWriter(new FileWriter("/Volumes/TOSHIBA/Fantasy_Football_Flex_Lineups/data.csv", false));
		String line = null;
		String QB = "", RB = "", WR = "", TE = "";
		
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
			total_games += Integer.parseInt(x[5]);
			data_counter++;
		}
		scan.close();
		data.write(QB);
		data.write(RB);
		data.flush();
		data.write(WR);
		data.write(TE);
		data.close();
		
		final int games_played_ave = total_games / data_counter;
		return games_played_ave;
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
