package net.progressit.backupzui.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;

import net.progressit.backupzui.UserHomeJsonFlavorRegistry;
import net.progressit.backupzui.api.FlavorSettings;

public class RealFlavorService implements FlavorService {
	private static final List<String> EMPTY_LIST = new ArrayList<>();
	
	private UserHomeJsonFlavorRegistry flavorRegistry;
	@Inject
	public RealFlavorService(UserHomeJsonFlavorRegistry flavorRegistry) {
		this.flavorRegistry = flavorRegistry;
	}
	
	@Override
	public String detectFlavor(Path folder, FlavorSettings currentFlavorSettingsOpt) {
		if(currentFlavorSettingsOpt!=null && !currentFlavorSettingsOpt.lookForFlavorsInside) {
			//Not allowed to look for flavors inside.
			return null;
		}
		
		List<String> names = flavorRegistry.getFlavorNames();
		for(String name:names) {
			boolean allowed = allowsFlavor(name, currentFlavorSettingsOpt);
			//System.out.println("#");
			if(allowed) {
				try {
					FlavorSettings newFlavorSettings = flavorRegistry.getSettings(name);
					boolean flavorMatched = checkFlavorMatches(folder, newFlavorSettings);
					if(flavorMatched) {
						System.out.print("\nFound flavor " + name);
						return name;
					}
				}catch(IOException e) {
					System.err.println(e.toString());
					e.printStackTrace();
				}
			}
		}
		return null; //Should not get here, as there should be a generic flavor which matches any folder.
	}
	
	/**
	 * Every criteria, if given, gets added to the AND logic. So, all of them must be true if provided.
	 * This makes it easy to pin point folders' flavors.
	 * 
	 * @param folder
	 * @param newFlavorSettings
	 * @return
	 * @throws IOException
	 */
	private boolean checkFlavorMatches(Path folder, FlavorSettings newFlavorSettings) throws IOException {
		boolean identifyBySelfFolderPatterns=false;
		boolean identifyByParentFolderPatterns=false;
		boolean identifyByChildFolderPatterns=false;
		boolean identifyBySiblingFolderPatterns=false;		
		boolean identifyByChildFilePatterns=false;
		boolean identifyBySiblingFilePatterns=false;
		
		boolean identifiedBySelfFolderPatterns=false;
		boolean identifiedByParentFolderPatterns=false;
		boolean identifiedByChildFolderPatterns=false;
		boolean identifiedBySiblingFolderPatterns=false;		
		boolean identifiedByChildFilePatterns=false;
		boolean identifiedBySiblingFilePatterns=false;
		
		//By self pattern
		if(newFlavorSettings.identifyBySelfFolderPatterns!=null && newFlavorSettings.identifyBySelfFolderPatterns.size()>0) {
			identifyBySelfFolderPatterns = true;
			for(String selfFolderPattern:newFlavorSettings.identifyBySelfFolderPatterns) {
				Pattern pattern = getPattern(selfFolderPattern);
				boolean matches = matches(selfFolderPattern, pattern, folder);
				if(matches) {
					identifiedBySelfFolderPatterns = true;
					break; //even one matches
				}
			}
		}
		
		//By parent pattern
		if(newFlavorSettings.identifyByParentFolderPatterns!=null && newFlavorSettings.identifyByParentFolderPatterns.size()>0) {
			identifyByParentFolderPatterns = true;
			Path parent = folder.getParent();
			if(parent!=null) { //Parent will be null for Root/drives
				for(String parentFolderPattern:newFlavorSettings.identifyByParentFolderPatterns) {
					Pattern pattern = getPattern(parentFolderPattern);
					boolean matches = matches(parentFolderPattern, pattern, parent);
					if(matches) {
						identifiedByParentFolderPatterns = true;
						break; //even one matches
					}
				}
			}else {
				//Even root folder will be considered if "all" is configured 
				boolean starAllowed = newFlavorSettings.identifyByParentFolderPatterns.contains("glob:*") || newFlavorSettings.identifyByParentFolderPatterns.contains(".*");
				if(starAllowed) return true;
			}
		}
		//By child patterns
		if(newFlavorSettings.identifyByChildFolderPatterns!=null && newFlavorSettings.identifyByChildFolderPatterns.size()>0) {
			identifyByChildFolderPatterns = true;
			List<Path> childFolders = getImmediateFilesOrFolders(folder, false);
			outer:
			for(String childFolderPattern:newFlavorSettings.identifyByChildFolderPatterns) {
				Pattern pattern = getPattern(childFolderPattern);
				
				for(Path childFolder:childFolders) {
					boolean matches = matches(childFolderPattern, pattern, childFolder);
					if(matches) {
						identifiedByChildFolderPatterns = true;
						break outer; //even one matches
					}
				}
			}
		}
		//By sibling patterns
		if(newFlavorSettings.identifyBySiblingFolderPatterns!=null && newFlavorSettings.identifyBySiblingFolderPatterns.size()>0) {
			identifyBySiblingFolderPatterns = true;
			if(folder.getParent()!=null) {
				List<Path> siblingFolders = getImmediateFilesOrFolders(folder.getParent(), false);
				outer:
				for(String siblingFolderPattern:newFlavorSettings.identifyBySiblingFolderPatterns) {
					Pattern pattern = getPattern(siblingFolderPattern);
					
					for(Path siblingFolder:siblingFolders) {
						boolean matches = matches(siblingFolderPattern, pattern, siblingFolder);
						if(matches) {
							identifiedBySiblingFolderPatterns = true;
							break outer; //even one matches
						}
					}
				}
			} //Else? Not important to identify root folder by the sibling folders.
		}		
		//By child FILE patterns
		if(newFlavorSettings.identifyByChildFilePatterns!=null && newFlavorSettings.identifyByChildFilePatterns.size()>0) {
			identifyByChildFilePatterns = true;
			List<Path> childFiles = getImmediateFilesOrFolders(folder, true);
			outer:
			for(String childFilePattern:newFlavorSettings.identifyByChildFilePatterns) {
				Pattern pattern = getPattern(childFilePattern);
				
				for(Path childFile:childFiles) {
					boolean matches = matches(childFilePattern, pattern, childFile);
					if(matches) {
						identifiedByChildFilePatterns = true;
						break outer; //even one matches
					}
				}
			}
		}
		//By sibling FILE patterns
		if(newFlavorSettings.identifyBySiblingFilePatterns!=null && newFlavorSettings.identifyBySiblingFilePatterns.size()>0) {
			identifyBySiblingFilePatterns = true;
			if(folder.getParent()!=null) {
				List<Path> siblingFiles = getImmediateFilesOrFolders(folder.getParent(), true);
				outer:
				for(String siblingFilePattern:newFlavorSettings.identifyBySiblingFilePatterns) {
					Pattern pattern = getPattern(siblingFilePattern);
					
					for(Path siblingFile:siblingFiles) {
						boolean matches = matches(siblingFilePattern, pattern, siblingFile);
						if(matches) {
							identifiedBySiblingFilePatterns = true;
							break outer; //even one matches
						}
					}
				}
			}//Else?
		}		
		
		if(identifyBySelfFolderPatterns && !identifiedBySelfFolderPatterns) return false;
		if(identifyByParentFolderPatterns && !identifiedByParentFolderPatterns) return false;
		if(identifyByChildFolderPatterns && !identifiedByChildFolderPatterns) return false;
		if(identifyBySiblingFolderPatterns && !identifiedBySiblingFolderPatterns) return false;
		if(identifyByChildFilePatterns && !identifiedByChildFilePatterns) return false;
		if(identifyBySiblingFilePatterns && !identifiedBySiblingFilePatterns) return false;

		//Whatever needs to be checked has been matched, or Nothing to be checked
		return true;
	}
	
	private boolean allowsFlavor(String flavor, FlavorSettings currentFlavorSettingsOpt) {
		if(currentFlavorSettingsOpt==null) {
			//Don't have any current flavor settiongs, So, allowed to search for flavors.
			return true;
		}
		
		if(currentFlavorSettingsOpt.lookForFlavorsInside==true) {
			//At least some are allowed
			//Check blacklist first
			if(currentFlavorSettingsOpt.blacklistInnerFlavors==null || currentFlavorSettingsOpt.blacklistInnerFlavors.size()==0) {
				//All allowed
			}else {
				boolean blocked = currentFlavorSettingsOpt.blacklistInnerFlavors.contains(flavor.trim().toLowerCase());
				if(blocked) {
					return false;
				}
			}
			//Fall through non-blacklisted
			
			if(currentFlavorSettingsOpt.whitelistInnerFlavors==null || currentFlavorSettingsOpt.whitelistInnerFlavors.size()==0) {
				//Even the lack of white-list will be considered as all allowed. This is to avoid inconvenience of having * for the common case.
			}else {
				boolean allowed = currentFlavorSettingsOpt.whitelistInnerFlavors.contains(flavor.trim().toLowerCase());
				if(!allowed) {
					return false;
				}
			}
			//Fall through exists in white-list, or white-list is empty.
		}else {
			return false;
		}
		
		return true;
	}
	
	private List<Path> getImmediateFilesOrFolders(Path startAt, boolean files) throws IOException{
	    try (Stream<Path> stream = Files.walk(startAt, 1)) {
	    	
	        return stream
	          .filter(file -> files ? !Files.isDirectory(file) : Files.isDirectory(file) )
	          .collect(Collectors.toList());
	    }
	}

	@Override
	public boolean isFolderAllowed(FlavorSettings flavorSettings, Path dir) {
		//System.out.println("isFolderAllowed " + dir);
		if(flavorSettings.getBlacklistFolderPatterns()!=null && flavorSettings.getBlacklistFolderPatterns().size()>0) {
			List<String> folderBlacklists = flavorSettings.getBlacklistFolderPatterns();
			folderBlacklists = folderBlacklists==null?EMPTY_LIST:folderBlacklists;
			for(String folderBlacklist:folderBlacklists) {
				Pattern pattern = getPattern(folderBlacklist);
				if(matches(folderBlacklist, pattern, dir)) {
					return false;
				}
			}
		}
		//Fall through not blacklisted
		
		if(flavorSettings.getWhitelistFolderPatterns()==null || flavorSettings.getWhitelistFolderPatterns().size()==0) {
			return true;
		}else {
			List<String> folderWhitelists = flavorSettings.getWhitelistFolderPatterns();
			folderWhitelists = folderWhitelists==null?EMPTY_LIST:folderWhitelists;
			for(String folderWhitelist:folderWhitelists) {
				Pattern pattern = getPattern(folderWhitelist);
				if(matches(folderWhitelist, pattern, dir)) {
					return true;
				}
			}
			
			//Fall through not allowed.
			return false;
		}
	}
	@Override
	public boolean isFileAllowed(FlavorSettings flavorSettings, Path file) {
		//System.out.println("isFileAllowed " + file);
		if(flavorSettings.getBlacklistFilePatterns()!=null && flavorSettings.getBlacklistFilePatterns().size()>0) {
			List<String> fileBlacklists = flavorSettings.getBlacklistFilePatterns();
			fileBlacklists = fileBlacklists==null?EMPTY_LIST:fileBlacklists;
			for(String fileBlacklist:fileBlacklists) {
				Pattern pattern = getPattern(fileBlacklist);
				if(matches(fileBlacklist, pattern, file)) {
					return false;
				}
			}
		}
		//Fall through not blacklisted
		
		if(flavorSettings.getWhitelistFilePatterns()==null || flavorSettings.getWhitelistFilePatterns().size()==0) {
			return true;
		}else {
			List<String> fileWhitelists = flavorSettings.getWhitelistFilePatterns();
			fileWhitelists = fileWhitelists==null?EMPTY_LIST:fileWhitelists;
			for(String fileWhitelist:fileWhitelists) {
				Pattern pattern = getPattern(fileWhitelist);
				if(matches(fileWhitelist, pattern, file)) {
					return true;
				}
			}
			
			//Fall through not allowed.
			return false;
		}
	}
	
	private boolean matches(String patternStr, Pattern pattern, Path path) {
		
		Path finalPath = path.getFileName(); //Only consider current one, and not the whole path.
		finalPath = finalPath==null?path:finalPath;
		//System.out.println("matches " + finalPath);
		boolean match = pattern.matcher(finalPath.toString()).matches();
		if(match) System.out.println( patternStr + "(" + finalPath + "): Matched" );
		else {
			if( finalPath.toString().endsWith(patternStr) ) System.out.println("OOPS");
		}
		return match;
	}
	private Pattern getPattern(String patternStr) {
		return Pattern.compile(patternStr);
	}
}
