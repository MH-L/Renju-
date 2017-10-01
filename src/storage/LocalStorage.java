package storage;

import java.io.*;
import java.util.*;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import model.AbstractBoard;
import model.AbstractGame.Difficulty;

public class LocalStorage {
	private static final String SEED_FILE_NAME = "seed.renj";
	private static final String RECORD_SUBDIR = "cachedRecords" + File.separator;
	private static final String CRITICAL_LOCATIONS_DIR = "criticalLocs" + File.separator;
	private static final int MAX_BUFFER_SIZE = 30;
	private static Set<Long> buffer = new HashSet<>();

	private static String getGameStorageBaseDir() {
		String homeDir = System.getProperty("user.home");
		return homeDir + File.separator + "Documents" + File.separator + "programming" + File.separator +
				"persistentStorage" + File.separator + "Renju" + File.separator;
	}

	public static void writeInitialSeed() {
		try {
			writeSeedWithOption(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void forceWriteInitialSeed() {
		try {
			writeSeedWithOption(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addCritical(long hash) {
	    buffer.add(hash);
	    if (buffer.size() > MAX_BUFFER_SIZE) {
	        writeAllCriticalLocations();
        }
    }

	private static void writeSeedWithOption(boolean force) throws IOException {
		String seedFileName = getGameStorageBaseDir() + SEED_FILE_NAME;
		File seedFile = new File(seedFileName);
		if (seedFile.exists() && !force) {
			return;
		}
		PrintWriter pr = new PrintWriter(seedFile);
		for (int i = 0; i < AbstractBoard.width * AbstractBoard.height * 2; i++) {
			long randomSeed = new Random().nextLong();
			pr.println(randomSeed);
		}

		pr.close();
	}

	public static List<Long> readInitialSeed() {
		List<Long> seedsList = new ArrayList<>();
		File seedFile = new File(getGameStorageBaseDir() + SEED_FILE_NAME);
		if (!seedFile.exists()) {
			writeInitialSeed();
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(seedFile));
			String line = "";
			while ((line = br.readLine()) != null) {
				seedsList.add(Long.parseLong(line.trim()));
			}

			return seedsList;
		} catch (IOException e) {
			return seedsList;
		}
	}

	public static void writeAllCriticalLocations() {
		String locationDir = getGameStorageBaseDir() + CRITICAL_LOCATIONS_DIR;
		File critLocs = new File(locationDir + "criticals001.txt");
		try {
			PrintWriter pr = new PrintWriter(new FileWriter(critLocs, true));
			for (Long loc : buffer) {
				pr.println(loc);
			}
			pr.close();
			buffer.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateGameRecord(List<Integer> gameMoves, int result) throws IOException{
		if (gameMoves.isEmpty()) {
			return;
		}
		String recordDirPath = getGameStorageBaseDir() + RECORD_SUBDIR;
		File recordDirFile = new File(recordDirPath);
		// TODO Find a file that is not too big; if no file found, create one
		File[] recordList = recordDirFile.listFiles();
		PrintWriter pr = new PrintWriter(new FileWriter(recordDirPath + "record013_sver.txt", true));
		pr.print(gameMoves.get(0));
		for (int i = 1; i < gameMoves.size(); i++) {
			pr.print("|" + gameMoves.get(i));
		}

		pr.print(":" + result);
		pr.println();
		pr.close();
	}

	public static Set<Long> readCritLocs() {
	    Set<Long> returnVal = new HashSet<>();
	    File locsDir = new File(getGameStorageBaseDir() + CRITICAL_LOCATIONS_DIR);
        File[] fileList = locsDir.listFiles();
        for (File f : fileList) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = "";
                while ((line = br.readLine()) != null) {
                    returnVal.add(Long.parseLong(line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnVal;
    }

	public static void initializeLocalStorage() throws IOException {
		String homeDirStr = System.getProperty("user.home");
		File userHomeDir = new File(homeDirStr);
		String gameBaseDir = getGameStorageBaseDir();
		File gameBase = new File(gameBaseDir);
		gameBase.mkdirs();
		String recordInB64 = Base64.encode("gameStats".getBytes());
		File statsFile = new File(gameBaseDir + recordInB64 + ".renj");
		if (statsFile.createNewFile()) {
			PrintWriter pr = new PrintWriter(statsFile);
			pr.println("0|0|0_0|0|0_0|0|0_0|0|0_0|0|0");
			pr.println("0|0|0_0|0|0_0|0|0_0|0|0_0|0|0");
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

	public static List<List<Integer>> getAllPreviousGames(List<Integer> resultList) {
	    List<List<Integer>> returnVal = new ArrayList<>();
        String recordDirPath = getGameStorageBaseDir() + RECORD_SUBDIR;
        File recordDirFile = new File(recordDirPath);
        // Find a file that is not too big; if no file found, create one
        File[] recordList = recordDirFile.listFiles();
        for (File recFile : recordList) {
            try (BufferedReader br = new BufferedReader(new FileReader(recFile))) {
                String line = "";
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        int[] resultArr = new int[]{0};
                        returnVal.add(lineToGame(line, resultArr));
                        resultList.add(resultArr[0]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnVal;
    }

	private static List<Integer> lineToGame(String line, int[] result) {
	    // TODO potential inefficient method
	    String[] parts = line.split(":");
	    result[0] = Integer.parseInt(parts[1].trim());
	    String[] moves = parts[0].split("\\|");
	    List<Integer> returnVal = new ArrayList<>();
	    for (int i = 0; i < moves.length; i++) {
	        returnVal.add(Integer.parseInt(moves[i]));
        }

        return returnVal;
    }
}
