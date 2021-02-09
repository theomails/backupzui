package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import net.progressit.backupzui.logic.BackupService.BackupRunSettings;

public class FlavorAwareVisitor implements FileVisitor<Path> {
	private BackupService backupService;
	private FlavorService flavorService;
	private BackupRunSettings settings;
	public FlavorAwareVisitor(BackupService backupService, FlavorService flavorService, BackupRunSettings settings) {
		this.backupService = backupService;
		this.flavorService = flavorService;
		this.settings = settings;
		if(this.settings.getFlavor()==null) {
			throw new RuntimeException("Flavor mandatory");
		}
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if(backupService.isStopped()) return FileVisitResult.SKIP_SUBTREE;
		
		if(dir.getParent()!=null && dir.getParent().equals(settings.getSourceBase())) {
			System.out.println("preVisitDirectory " + dir);
		}
		
		//System.out.println(flavor);
		boolean allowed = flavorService.isFolderAllowed(settings.getFlavor(), dir);
		if(!allowed) {
			System.out.println(settings.getFlavor().flavorName + " has blocked " + dir);
			return FileVisitResult.SKIP_SUBTREE;
		}
		
		Path relPath = settings.getSourceBase().relativize(dir);
		Path matchingDest = settings.getDestinationBase().resolve(relPath);
		
		if(!dir.equals(settings.getSourceBase())) { //Don't check flavour again for root folder during the walk.
			String newFlavor = flavorService.detectFlavor(dir, settings.getFlavor());
			if(newFlavor!=null && !"generic".equals(newFlavor)) {
				System.out.println("Found inner flavor " + newFlavor + ". Proceeding to a new backup...");
				backupService.startNewBackupInner(settings.getOriginalRoot(), dir, matchingDest, newFlavor, settings.isResync());
				return FileVisitResult.SKIP_SUBTREE;
			}
		}
		
		backupService.post( new BackupService.EventFolderProcessed(settings.getOriginalRoot(), settings.getSourceBase(), settings.getFlavor().flavorName, false, true, dir, matchingDest, relPath) );
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if(backupService.isStopped()) return FileVisitResult.SKIP_SUBTREE;
		//System.out.println("visitFile " + file);
		
		//Optimize.. If blacklist *, skip siblings
		boolean allowed = flavorService.isFileAllowed(settings.getFlavor(), file);
		if(allowed) {
			Path relFile = settings.getSourceBase().relativize(file);
			Path matchingDestFile = settings.getDestinationBase().resolve(relFile);
			
			backupService.copyFile(file, matchingDestFile, relFile);
			backupService.post( new BackupService.EventFileProcessed(false,file, matchingDestFile, relFile) );
			//try { Thread.sleep(100); } catch(Exception e) {}
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		System.out.println("visitFileFailed " + file);
		
		if (exc instanceof FileSystemLoopException) {
	        System.err.println("Cycle detected: " + file);
	        return FileVisitResult.CONTINUE;
	    } else {
	        System.err.format("Unable to copy: %s: %s%n . ", file, exc);
	        return FileVisitResult.CONTINUE;
	    }
	    
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		//System.out.println("postVisitDirectory " + dir);
		
		backupService.post( new BackupService.EventFolderProcessed(settings.getOriginalRoot(), settings.getSourceBase(), settings.getFlavor().flavorName, false, false, dir, null, null) );
		return FileVisitResult.CONTINUE;
	}
};