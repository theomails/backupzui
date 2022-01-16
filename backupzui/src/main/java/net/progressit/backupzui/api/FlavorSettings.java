package net.progressit.backupzui.api;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlavorSettings {
	public String flavorName;
	public String flavorDescription;
	
	//Identity
	public List<String> identifyBySelfFolderPatterns;
	
	public List<String> identifyByParentFolderPatterns;
	public List<String> identifyByChildFolderPatterns;
	public List<String> identifyBySiblingFolderPatterns;
	
	public List<String> identifyByChildFilePatterns;
	public List<String> identifyBySiblingFilePatterns;
	
	//Contents to Copy or Skip
	public List<String> blacklistFolderPatterns;
	public List<String> whitelistFolderPatterns;
	public List<String> blacklistFilePatterns;
	public List<String> whitelistFilePatterns;

	//Look for Flavors inside
	public boolean lookForFlavorsInside;
	public List<String> blacklistInnerFlavors;
	public List<String> whitelistInnerFlavors;
}
