package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.google.inject.Inject;

public class RealCopyIfNeededService implements CopyService {
	@Inject
	public RealCopyIfNeededService() {}
	
	@Override
	public void copyFile(Path file, Path matchingDestFile, Path relFile) {
		boolean copy = true;
		if(! matchingDestFile.getParent().toFile().exists() ) {
			matchingDestFile.toFile().mkdirs();
		}else {
			long destSize = matchingDestFile.toFile().exists()? matchingDestFile.toFile().length():0;
			long srcSize = destSize!=0? file.toFile().length() : 1; //If dest is 0, we dont need the actual size to determine that it needs copying.
			copy = (srcSize!=destSize);
		}
		
		if(copy) {
			try {
				Files.copy(file, matchingDestFile, StandardCopyOption.REPLACE_EXISTING);
				System.out.print(".");
			} catch (IOException e) {
				System.out.print("!");
				System.err.println(e.toString());
			}
		}else {
			System.out.print("x");
		}
	}
}
