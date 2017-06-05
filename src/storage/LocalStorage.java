package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
			pr.close();
		}
	}
	
	/**
	 * Update game stats. 
	 * @param diff
	 * @param result 0 for win, 1 for lose, 2 for tie
	 * @throws IOException
	 */
	public static void updateGameStats(Difficulty diff, int result) throws IOException {
		FileReader fr = new FileReader(getStatsFile());
		BufferedReader br = new BufferedReader(fr);
		String record = br.readLine();
		br.close();
		FileWriter frr = new FileWriter(getStatsFile());
		String[] levels = record.split("_");
		
		switch (diff) {
		case novice:
			String[] recs = levels[0].split("\\|");
			recs[result] = (Integer.parseInt(recs[result]) + 1) + "";
			String newRec = (recs[0] + '|' + recs[1] + '|' + recs[2] + '|')
					+ '_' + levels[1] + '_' + levels[2] + '_' + levels[3];
			frr.write(newRec);
			frr.close();
			break;
		case intermediate:
			recs = levels[1].split("\\|");
			recs[result] = (Integer.parseInt(recs[result]) + 1) + "";
			newRec = levels[0] + '_' + (recs[0] + '|' + recs[1] + '|' + recs[2] + '|')
					+ '_' + levels[2] + '_' + levels[3];
			frr.write(newRec);
			frr.close();
			break;
		case advanced:
			recs = levels[2].split("\\|");
			recs[result] = (Integer.parseInt(recs[result]) + 1) + "";
			newRec = levels[0] + '_' + levels[1] + '_' +
					(recs[0] + '|' + recs[1] + '|' + recs[2] + '|')
					+'_' + levels[3];
			frr.write(newRec);
			frr.close();
			break;
		case ultimate:
			recs = levels[3].split("\\|");
			recs[result] = (Integer.parseInt(recs[result]) + 1) + "";
			newRec = levels[0] + '_' + levels[1] + '_' + levels[2] + '_' + 
					(recs[0] + '|' + recs[1] + '|' + recs[2] + '|');
			frr.write(newRec);
			frr.close();
			break;
		default:
			break;
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
}
