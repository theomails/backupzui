package net.progressit.backupzui;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import net.progressit.backupzui.logic.CopyService;
import net.progressit.backupzui.logic.FlavorService;
import net.progressit.backupzui.logic.RealBackupService;
import net.progressit.backupzui.logic.RealCopyIfNeededService;
import net.progressit.backupzui.logic.RealFlavorService;
import net.progressit.backupzui.ui.MainWindow;

public class LiveModule extends AbstractModule{

	@Override
	protected void configure() {
		//Just for debug. Comment as needed.
		binder().requireExplicitBindings();
		binder().requireAtInjectOnConstructors();
		
		bind(EventBus.class).toInstance(new EventBus()); //requireExplicitBindings. Shows that has no dependencies.
		bind(FlavorService.class).to(RealFlavorService.class).in(Scopes.SINGLETON);
		bind(CopyService.class).to(RealCopyIfNeededService.class).in(Scopes.SINGLETON);
		
		bind(RealBackupService.class).in(Scopes.SINGLETON);
		bind(UserHomeJsonFlavorRegistry.class).in(Scopes.SINGLETON);;
		bind(MainWindow.class);
	}

}
