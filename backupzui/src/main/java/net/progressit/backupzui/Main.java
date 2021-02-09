package net.progressit.backupzui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.progressit.backupzui.ui.MainWindow;

public class Main {
	public static void main(String[] args) {
		try {
			//System.setProperty("sun.java2d.opengl", "true"); //Caused some problem with UI not updating.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		Injector injector = Guice.createInjector(new LiveModule());
		MainWindow mw = injector.getInstance(MainWindow.class);
		mw.init();
		mw.setVisible(true);	
	}
}
