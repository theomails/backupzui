package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import net.progressit.backupzui.FlavorRegistry;
import net.progressit.backupzui.api.FlavorSettings;

public class RealBackupService implements BackupService {
	private FlavorService flavorService;
	private FlavorRegistry flavorRegistry;
	private CopyService copyService;
	private EventBus bus;
	@Inject
	public RealBackupService(FlavorService flavorService, FlavorRegistry flavorRegistry, CopyService copyService, EventBus bus) {
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
	
	@Override
	public void startNewBackup(Path source, Path destination, String flavorOpt, boolean isResync) throws IOException {
		startNewBackupInner(source, source, destination, flavorOpt, isResync);
	}
	public void startNewBackupInner(Path originalRoot, Path source, Path destination, String flavorOpt, boolean isResync) throws IOException {
		System.out.println("startNewBackup(source, destination, flavorOpt, isResync)");
		System.out.println(Arrays.asList(source, destination, flavorOpt, isResync));
		
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
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		bus.post(new EventBackupCompleted(backupId));
		

	}

	@Override
	public void post(Object event) {
		bus.post(event);
	}
	
	@Override
	public void copyFile(Path file, Path matchingDestFile, Path relFile) {
		try {
			copyService.copyFile(file, matchingDestFile, relFile);
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
