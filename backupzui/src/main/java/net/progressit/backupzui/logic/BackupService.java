package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.progressit.backupzui.api.FlavorSettings;

public interface BackupService {
	@Data
	@AllArgsConstructor
	public static class BackupRunSettings{
		private final Path originalRoot;
		private final Path sourceBase;
		private final Path destinationBase;
		private FlavorSettings flavor;
		private final boolean resync;
	}
	
	@Data
	public static class EventBackupStarted{
		private final long backupId; //nanos at start
		private final boolean newBackup;
		private final Path fromFolder;
		private final Path toFolder;
	}	
	@Data
	public static class EventBackupCompleted{
		private final long backupId;
	}
	@Data
	public static class EventFolderProcessed{
		private final Path originalRoot;
		private final Path flavorRoot;
		private final String flavor;
		private final boolean skipped;
		private final boolean start;
		private final Path fromFolder;
		private final Path toFolder;
		private final Path relFolder;
	}
	@Data
	public static class EventFileProcessed{
		private final boolean skipped;
		private final Path fromFile;
		private final Path toFile;
		private final Path relFile;
	}
	
	void startNewBackup(Path source, Path destination, String flavorOpt, boolean isResync) throws IOException;
	
	void startNewBackupInner(Path originalRoot, Path dir, Path matchingDest, String newFlavor, boolean resync) throws IOException;

	void post(Object event);

	void copyFile(Path file, Path matchingDestFile, Path relFile);

	boolean isStopped();
	
	void stop();

}