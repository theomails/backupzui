package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.Path;

public interface CopyService {

	void copyFile(Path file, Path matchingDestFile, Path relFile) throws IOException;

}