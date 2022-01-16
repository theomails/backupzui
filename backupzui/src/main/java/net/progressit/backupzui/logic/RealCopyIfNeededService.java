package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import net.progressit.backupzui.logic.RealBackupService.EventException;

public class RealCopyIfNeededService implements CopyService {
	private EventBus bus;
	@Inject
	public RealCopyIfNeededService(EventBus bus) {
		this.bus = bus;
	}
	
	@Override
	public void copyFile(Path file, Path matchingDestFile, Path relFile) {
		boolean copy = true;
		if(! matchingDestFile.getParent().toFile().exists() ) {
			matchingDestFile.toFile().mkdirs();
		}else {
			long destSize = matchingDestFile.toFile().exists()? matchingDestFile.toFile().length():-1;
			long srcSize = destSize!=-1? file.toFile().length() : 1; //If dest is 0, we dont need the actual size to determine that it needs copying.
			copy = (srcSize!=destSize);
		}
		
		if(copy) {
			try {
				bus.post( new RealBackupService.EventFileProcessed(false, true, file, matchingDestFile, relFile, true) );
				Files.copy(file, matchingDestFile, StandardCopyOption.REPLACE_EXISTING);
				bus.post( new RealBackupService.EventFileProcessed(false, false, file, matchingDestFile, relFile, true) );
				System.out.print(".");
			} catch (IOException e) {
				bus.post(new EventException(e, false));
				System.out.print("!");
				System.err.println(e.toString());
			}
		}else {
			System.out.print("x");
		}
	}
}
