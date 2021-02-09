package net.progressit.backupzui.userdata;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class BackupHistory implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Data
	public static class BackupEntry{
		private Date backupDate;
		private Path sourceRoot;
		private Path destinationRoot;
		private String sourceSerial;
		private String destinationSerial;
		private String sourceLabel;
		private String destinationLabel;
	}
	
	private List<BackupEntry> entries;
	/**
	 * Conservative set. Will try not to replace the list object, instead just clearing and taking the contents if possible.
	 * @param entries
	 */
	public void setEntries(List<BackupEntry> entries) {
		if(this.entries==null) {
			this.entries = entries;
		} else {
			this.entries.clear();
			this.entries.addAll(entries);
		}
	}
	/**
	 * Safe get. Will not return null.
	 * @return
	 */
	public List<BackupEntry> getEntries(){
		if(entries==null) {
			entries = new ArrayList<>();
		}
		return entries;
	}
}
