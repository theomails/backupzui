package net.progressit.backupzui.logic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

import net.progressit.backupzui.userdata.BackupHistory;

public class UserJsonBackupHistoryService implements BackupHistoryService {
	
	@Inject
	public UserJsonBackupHistoryService() {}
	
	@Override
	public BackupHistory getHistory() {
		String userHomeStr = System.getProperty("user.home");
		File userHome = new File(userHomeStr);
		File backupzuiLog = new File(userHome, "backupzui.log.json");
		Gson g = new Gson();
		try(FileReader fr = new FileReader(backupzuiLog)){
			BackupHistory wrapper = g.fromJson(fr, BackupHistory.class);
			return wrapper;
		} catch (IOException e) {
			BackupHistory blank = new BackupHistory();
			blank.setEntries(new ArrayList<>());
			return blank;
		}
	}
	@Override
	public void writeHistory(BackupHistory backupLog) {
		String userHomeStr = System.getProperty("user.home");
		File userHome = new File(userHomeStr);
		File backupzuiLog = new File(userHome, "backupzui.log.json");
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		try(FileWriter fw = new FileWriter(backupzuiLog)){
			g.toJson(backupLog, fw);
		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
	}
}
