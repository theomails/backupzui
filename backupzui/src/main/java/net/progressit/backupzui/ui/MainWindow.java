package net.progressit.backupzui.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import net.miginfocom.swing.MigLayout;
import net.progressit.backupzui.logic.BackupHistoryService;
import net.progressit.backupzui.logic.BackupService;

public class MainWindow extends JFrame{
	private static final long serialVersionUID = 1L;

	private BackupHistoryService dataLogService;
	private BackupService backupService;
	private EventBus bus;
	@Inject
	public MainWindow(BackupHistoryService dataLogService, BackupService backupService, EventBus bus) {
		this.dataLogService = dataLogService;
		this.backupService = backupService;
		this.bus = bus;
		this.logPanel = new LogDisplayPanel(dataLogService, backupService, bus);
	}
	
	private JPanel mainPanel = new JPanel(new MigLayout("insets 10","[200::,fill][600::,grow 3,fill]","[500::,grow,fill][]"));
	private JPanel closePanel = new JPanel(new MigLayout("insets 0","[grow,fill][]","[]"));
	
	private NavPanel navPanel = new NavPanel();
	private LogDisplayPanel logPanel;
	private JButton btnClose = new JButton("Close");
	public void init() {
		bus.register(this);
		
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.add(navPanel, "");
		mainPanel.add(logPanel, "wrap");
		mainPanel.add(closePanel, "spanx 2");
		
		closePanel.add(btnClose, "skip 1");
		
		addHandlers();
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setTitle("Backupz");
	}
	
	private void addHandlers() {
		btnClose.addActionListener( (e)->{ MainWindow.this.dispose(); });
	}
}
