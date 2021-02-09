package net.progressit.backupzui.logic;

import net.progressit.backupzui.userdata.BackupHistory;

/**
 * Just logging the history of the Backups done in the past.
 * <br>Just the date, from and to details. Not the detailed log of the backup itself.
 * @author theom
 *
 */
public interface BackupHistoryService {

	/**
	 * Get the history of Backups performed on this system. Usually in production from a Json file in User home.
	 * @return
	 */
	BackupHistory getHistory();

	/**
	 * Write the history of Backups performed on this system. Usually in production to a Json file in User home.
	 * <br>First get the existing list, append, and then call write, so that old data is not lost.
	 * @return
	 */
	void writeHistory(BackupHistory backupLog);

}