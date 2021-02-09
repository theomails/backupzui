package net.progressit.backupzui;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import net.progressit.backupzui.logic.BackupHistoryService;
import net.progressit.backupzui.logic.BackupService;
import net.progressit.backupzui.logic.CopyService;
import net.progressit.backupzui.logic.FlavorService;
import net.progressit.backupzui.logic.RealBackupService;
import net.progressit.backupzui.logic.RealCopyService;
import net.progressit.backupzui.logic.RealFlavorService;
import net.progressit.backupzui.logic.UserJsonBackupHistoryService;
import net.progressit.backupzui.ui.MainWindow;

public class LiveModule extends AbstractModule{

	@Override
	protected void configure() {
		//Just for debug. Comment as needed.
		binder().requireExplicitBindings();
		binder().requireAtInjectOnConstructors();
		
		bind(BackupHistoryService.class).to(UserJsonBackupHistoryService.class).in(Scopes.SINGLETON);
		bind(FlavorRegistry.class).to(HardcodedFlavorRegistry.class).in(Scopes.SINGLETON);
		bind(EventBus.class).toInstance(new EventBus()); //Shows that has no dependencies.
		bind(FlavorService.class).to(RealFlavorService.class).in(Scopes.SINGLETON);
		bind(BackupService.class).to(RealBackupService.class).in(Scopes.SINGLETON);
		bind(CopyService.class).to(RealCopyService.class).in(Scopes.SINGLETON);
		bind(MainWindow.class);
	}

}
