package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.progressit.backupzui.api.FlavorSettings;

public class RealBackupService  {
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
		private final boolean start;
		private final Path fromFile;
		private final Path toFile;
		private final Path relFile;
		private final boolean realCopy;
	}
	@Data
	public static class EventException{
		private final Throwable exception;
		private final boolean fromSwing;
	}
	
	private RealFlavorService flavorService;
	private UserHomeJsonFlavorRegistry flavorRegistry;
	private CopyService copyService;
	private EventBus bus;
	@Inject
	public RealBackupService(RealFlavorService flavorService, UserHomeJsonFlavorRegistry flavorRegistry, CopyService copyService, EventBus bus) {
		this.flavorService = flavorService;
		this.flavorRegistry = flavorRegistry;
		this.copyService = copyService;
		this.bus = bus;
	}
	
	
	private long backupId = 0L;
	private volatile boolean stopped = false; //Gets read from Backup thread. Gets set from UI events thread.
	public boolean isStopped() {
		return stopped;
	}
	public void stop() {
		stopped = true;
	}
	
	public void startNewBackup(Path source, Path destination, String flavorOpt, boolean isResync) throws IOException {
		stopped = false;
		startNewBackupInner(source, source, destination, flavorOpt, isResync);
	}
	public void startNewBackupInner(Path originalRoot, Path source, Path destination, String flavorOpt, boolean isResync) throws IOException {
		System.out.print("\nstartNewBackup(source, destination, flavorOpt, isResync)");
		System.out.print("\n" + Arrays.asList(source, destination, flavorOpt, isResync));
		
		backupId = System.nanoTime();
		
		FlavorSettings settings = null;
		if(flavorOpt==null) {
			flavorOpt = flavorService.detectFlavor(source, null);
			if(flavorOpt==null) throw new RuntimeException("Flavor is null for " + source);
		}
		settings = flavorRegistry.getSettings(flavorOpt);
		if(settings==null) throw new RuntimeException("Settings is null for " + source);
		
		bus.post(new EventBackupStarted(backupId, isResync, source, destination));
		
		BackupRunSettings bkpSettings = new BackupRunSettings(originalRoot, source, destination, settings, isResync);
		//Even the sub flavors crawling will be done Sync in same thread, and will be done inside these calls.
		FileVisitor<Path> myVisitor = new FlavorAwareVisitor(RealBackupService.this, flavorService, bkpSettings);
		try {
			Files.walkFileTree(source, myVisitor);
		} catch (IOException e) {
			bus.post(new EventException(e, false));
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		bus.post(new EventBackupCompleted(backupId));
		

	}

	public void post(Object event) {
		bus.post(event);
	}
	
	public EventBus getBus() {
		return bus;
	}
	
	public void copyFile(Path file, Path matchingDestFile, Path relFile) {
		try {
			copyService.copyFile(file, matchingDestFile, relFile);
		} catch (IOException e) {
			bus.post(new EventException(e, false));
			System.err.println(e);
		}
	}
}
