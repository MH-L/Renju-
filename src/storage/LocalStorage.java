package storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import model.AbstractGame.Difficulty;

public class LocalStorage {
	public static void initializeLocalStorage() throws IOException {
		String homeDirStr = System.getProperty("user.home");
		File userHomeDir = new File(homeDirStr);
		String gameBaseDir = userHomeDir.getAbsolutePath() + File.separator + "Renju";
		File gameBase = new File(gameBaseDir);
		gameBase.mkdir();
		String recordInB64 = Base64.encode("gameStats".getBytes());
		File statsFile = new File(gameBaseDir + File.separator + recordInB64 + ".renj");
		if (statsFile.createNewFile()) {
			PrintWriter pr = new PrintWriter(statsFile);
			pr.println("0|0|0_0|0|0_0|0|0_0|0|0");
			pr.println("0|0|0_0|0|0_0|0|0_0|0|0");
			pr.close();
		}
	}
	
	/**
	 * Update game stats. 
	 * @param diff
	 * @param result 0 for win, 1 for lose, 2 for tie
	 * @throws IOException
	 */
	public static void updateGameStats(Difficulty diff, int result, boolean isFirst)
			throws IOException {
		FileReader fr = new FileReader(getStatsFile());
		BufferedReader br = new BufferedReader(fr);
		String firstLine = br.readLine();
		String secondLine = br.readLine();
		
		br.close();
		FileWriter frr = new FileWriter(getStatsFile());
		BufferedWriter brr = new BufferedWriter(frr);
		if (isFirst) {
			firstLine = updateStatsHelper(firstLine, diff, result);
		} else {
			secondLine = updateStatsHelper(secondLine, diff, result);
		}
		
		brr.write(firstLine);
		brr.newLine();
		brr.write(secondLine);
		brr.newLine();
		brr.close();
	}
	
	private static String updateStatsHelper(String record, Difficulty diff, int result) {
		String[] levels = record.split("_");
		
		switch (diff) {
		case novice:
			String[] recs = levels[0].split("\\|");
			recs[result] = (Integer.parseInt(recs[result]) + 1) + "";
			String newRec = (recs[0] + '|' + recs[1] + '|' + recs[2] + '|')
					+ '_' + levels[1] + '_' + levels[2] + '_' + levels[3];
			return newRec;
		case intermediate:
			recs = levels[1].split("\\|");
			recs[result] = (Integer.parseInt(recs[result]) + 1) + "";
			newRec = levels[0] + '_' + (recs[0] + '|' + recs[1] + '|' + recs[2] + '|')
					+ '_' + levels[2] + '_' + levels[3];
			return newRec;
		case advanced:
			recs = levels[2].split("\\|");
			recs[result] = (Integer.parseInt(recs[result]) + 1) + "";
			newRec = levels[0] + '_' + levels[1] + '_' +
					(recs[0] + '|' + recs[1] + '|' + recs[2] + '|')
					+'_' + levels[3];
			return newRec;
		case ultimate:
			recs = levels[3].split("\\|");
			recs[result] = (Integer.parseInt(recs[result]) + 1) + "";
			newRec = levels[0] + '_' + levels[1] + '_' + levels[2] + '_' + 
					(recs[0] + '|' + recs[1] + '|' + recs[2] + '|');
			return newRec;
		default:
			return null;
		}
	}
	
	private static File getStatsFile() {
		String homeDirStr = System.getProperty("user.home");
		File userHomeDir = new File(homeDirStr);
		String gameBaseDir = userHomeDir.getAbsolutePath() + File.separator + "Renju";
		String recordInB64 = Base64.encode("gameStats".getBytes());
		File statsFile = new File(gameBaseDir + File.separator + recordInB64 + ".renj");
		return statsFile;
	}
	
	public static List<Integer> readStatsFile() throws IOException {
		FileReader fr = new FileReader(getStatsFile());
		BufferedReader br = new BufferedReader(fr);
		String firstLine = br.readLine();
		String secondLine = br.readLine();
		List<Integer> ret = new ArrayList<>();
		String[] asBlack = firstLine.split("_");
		String[] asWhite = secondLine.split("_");
		for (String blackLevel : asBlack) {
			String[] recs = blackLevel.split("\\|");
			for (String rec : recs)
				ret.add(Integer.parseInt(rec));
		}
		
		for (String whiteLevel : asWhite) {
			String[] recs = whiteLevel.split("\\|");
			for (String rec : recs)
				ret.add(Integer.parseInt(rec));
		}
		
		br.close();
		return ret;
	}
}
