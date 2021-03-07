package net.progressit.backupzui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import lombok.Getter;
import net.progressit.backupzui.api.FlavorSettings;
import net.progressit.backupzui.util.StringList;

public class HardcodedFlavorRegistry implements FlavorRegistry {
	
	private final List<FlavorSettings> flavorExecs = new ArrayList<>();
	private final Map<String, FlavorSettings> flavorExecMap = new LinkedHashMap<>();
	
	@Getter private final float version = 1.0f;
	@Getter private final String versionDate = "01-03-2021";
	
	@Inject
	public HardcodedFlavorRegistry() {
		load();
	}
	
	private void load() {
		
		//Misses hidden files .. need to fix.
		
		//Maven
		//Intellij
		//Android
		//Eclipse ws
		//Tomcat
		//pagefile.sys, hiberfile.sys
		
		FlavorSettings fakePc = FlavorSettings.builder() //
				.flavorName("fake-pc")
				.identifyBySelfFolderPatterns( StringList.strings("FAKEPC") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings ssdClone = FlavorSettings.builder() //
				.flavorName("ssd-clone")
				.identifyBySelfFolderPatterns( StringList.strings("SSD-CLONE") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings ssdToDellFolder = FlavorSettings.builder() //
				.flavorName("ssd-to-dell")
				.identifyBySelfFolderPatterns( StringList.strings("Zoho Dell Ubuntu 2021") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings git = FlavorSettings.builder() //
				.flavorName("git")
				.identifyBySelfFolderPatterns( StringList.strings("\\.git") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings m2 = FlavorSettings.builder() //
				.flavorName("maven-m2")
				.identifyBySelfFolderPatterns( StringList.strings("\\.m2") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings hg = FlavorSettings.builder() //
				.flavorName("hg")
				.identifyBySelfFolderPatterns( StringList.strings("\\.hg") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings unixHidden = FlavorSettings.builder() //
				.flavorName("unix-hidden")
				.identifyBySelfFolderPatterns( StringList.strings("\\.(.*)") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.build();
		FlavorSettings unixSystem = FlavorSettings.builder() //
				.flavorName("unix-system")
				.identifyByParentFolderPatterns(StringList.strings("/"))
				.identifyBySelfFolderPatterns( StringList.strings("bin","boot","dev","etc","lib","lib32","lib64","libx32","media","mnt",
						"opt","proc","root","run","sbin","snap","srv","sys","usr","tmp") ) //Retained /home, /lost+found, /usr, /var
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.blacklistFilePatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings unixTrash = FlavorSettings.builder() //
				.flavorName("unix-trash")
				.identifyBySelfFolderPatterns( StringList.strings("\\.Trash(.*)") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings sysVolInfo = FlavorSettings.builder() //
				.flavorName("sys-vol-info")
				.identifyBySelfFolderPatterns( StringList.strings("System Volume Information") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) // 
				.build();
		
		FlavorSettings winRecycle = FlavorSettings.builder() //
				.flavorName("win-recyclebin")
				.identifyBySelfFolderPatterns( StringList.strings("$RECYCLE.BIN") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) // 
				.build();
		
		FlavorSettings appData = FlavorSettings.builder() //
				.flavorName("app-data")
				.identifyBySelfFolderPatterns( StringList.strings("AppData") ) //
				.blacklistFolderPatterns( StringList.strings("Local","LocalLow","Roaming") ) // 
				.build();
		
		FlavorSettings programData = FlavorSettings.builder() //
				.flavorName("program-data")
				.identifyBySelfFolderPatterns( StringList.strings("ProgramData") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) // 
				.blacklistFilePatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings recoveryAndBoot = FlavorSettings.builder() //
				.flavorName("recovery-and-boot")
				.identifyBySelfFolderPatterns( StringList.strings("Recovery", "boot", "inetpub", "SYSTEM.SAV") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) // 
				.blacklistFilePatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings vueFlavor = FlavorSettings.builder() //
				.flavorName("vue-folder")
				.lookForFlavorsInside(true)
				.identifyByParentFolderPatterns( StringList.strings("VUE", "vue") ) //
				.blacklistFolderPatterns( StringList.strings("node_modules","\\.git","dist") ) //
				.build();
		
//		FlavorSettings eclipseWs = FlavorSettings.builder() //
//				.flavorName("eclipse-ws")
//				.lookForFlavorsInside(true)
//				.identifyByChildFolderPatterns( StringList.strings("\\.metadata") ) //
//				.blacklistFolderPatterns( StringList.strings("\\.metadata") ) //
//				.build();
		
		FlavorSettings eclipseProject = FlavorSettings.builder() //
				.flavorName("eclipse-project")
				.lookForFlavorsInside(true)
				.identifyBySiblingFolderPatterns( StringList.strings("\\.metadata") ) //weak
				.blacklistFolderPatterns( StringList.strings("\\.metadata", "target") ) //
				.build();
		
		FlavorSettings zideProject = FlavorSettings.builder() //
				.flavorName("zide-project")
				.lookForFlavorsInside(true)
				.identifyByChildFolderPatterns( StringList.strings("\\.zide_resources") ) //
				.blacklistFolderPatterns( StringList.strings("\\.zide_resources", "build") ) //
				.build();
		
		FlavorSettings tomcat = FlavorSettings.builder() //
				.flavorName("tomcat-dir")
				.lookForFlavorsInside(true)
				.identifyBySelfFolderPatterns( StringList.strings("apache-tomcat.*") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.blacklistFilePatterns( StringList.strings(".*") ) //
				.build();
		FlavorSettings knownDrivePrograms = FlavorSettings.builder() //
				.flavorName("known-drive-programs")
				.identifyBySelfFolderPatterns( StringList.strings("Autodesk.*", "g", "go", "Google SketchUp.*", "HP", 
						"Intel", "kingsoft", "oracledata", "oraclexe", "WINAVR") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.blacklistFilePatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings userDir = FlavorSettings.builder() //
				.flavorName("user-dir")
				.lookForFlavorsInside(true)
				.identifyByParentFolderPatterns( StringList.strings("Users") ) //
				.blacklistFolderPatterns( StringList.strings("(\\.).*") ) //
				.build();
		
		FlavorSettings progFilesFlavor = FlavorSettings.builder() //
				.flavorName("program-files")
				.identifyBySelfFolderPatterns( StringList.strings("Program Files","Program Files \\(x86\\)") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.blacklistFilePatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings windowsFlavor = FlavorSettings.builder() //
				.flavorName("windows")
				.identifyBySelfFolderPatterns( StringList.strings("Windows", "Windows.old") ) //
				.blacklistFolderPatterns( StringList.strings(".*") ) //
				.blacklistFilePatterns( StringList.strings(".*") ) //
				.build();
		
		FlavorSettings genericFlavor = FlavorSettings.builder() //
				.flavorName("generic")
				.lookForFlavorsInside(true)
				.identifyBySelfFolderPatterns( StringList.strings(".*") ) //
				.blacklistFolderPatterns( StringList.strings("\\$.*") ) //
				.build();
		
		init( Arrays.asList( new FlavorSettings[] {
				fakePc, ssdClone, ssdToDellFolder, 
				m2, git, hg, 
				unixHidden, unixSystem, unixTrash,
				sysVolInfo, winRecycle,
				appData, programData, recoveryAndBoot, 
				zideProject, eclipseProject, vueFlavor, tomcat, knownDrivePrograms,
				userDir, progFilesFlavor, windowsFlavor, 
				genericFlavor} ) );
	}
	
	private void init(List<FlavorSettings> settings) {
		flavorExecs.clear();
		flavorExecs.addAll(settings);
		for(FlavorSettings f:flavorExecs) {
			flavorExecMap.put(f.getFlavorName(), f);
		}
	}
	
	@Override
	public List<FlavorSettings> getOrderedFlavorExecs() {
		return flavorExecs;
	}
	@Override
	public FlavorSettings getSettings(String flavorName) {
		FlavorSettings settings =  flavorExecMap.get(flavorName);
		return settings;
	}
	@Override
	public List<String> getFlavorNames(){
		List<String> names = new ArrayList<>();
		names.addAll( flavorExecMap.keySet() );
		return names;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void saveSettings(List<FlavorSettings> settings) {
		//NOOP
	}

}
