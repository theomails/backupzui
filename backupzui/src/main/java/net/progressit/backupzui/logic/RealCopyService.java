package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.google.inject.Inject;

public class RealCopyService implements CopyService {
	@Inject
	public RealCopyService() {}
	
	@Override
	public void copyFile(Path file, Path matchingDestFile, Path relFile) throws IOException {
		if(! matchingDestFile.getParent().toFile().exists() ) {
			matchingDestFile.toFile().mkdirs();
		}
		Files.copy(file, matchingDestFile, StandardCopyOption.REPLACE_EXISTING);
	}
}
