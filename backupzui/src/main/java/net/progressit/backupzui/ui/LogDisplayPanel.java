package net.progressit.backupzui.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.google.common.eventbus.EventBus;

import net.miginfocom.swing.MigLayout;
import net.progressit.backupzui.logic.BackupHistoryService;
import net.progressit.backupzui.logic.BackupService;
import net.progressit.backupzui.userdata.BackupHistory;
import net.progressit.backupzui.userdata.BackupHistory.BackupEntry;

public class LogDisplayPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Object[] columns = new Object[] {"Backup Date", "Source Root", "Source Serial", "Source Label", "Destination Root", "Destination Serial", "Destination Label"};
	
	private final JTable table;
	
	private final JPanel pnlButtons = new JPanel(new MigLayout("","[grow][]10[]","[]"));
	private final JButton btnNewBackup = new JButton("Start Backup...");
	private final JButton btnResyncBackup = new JButton("Resync Backup...");
	private final JPanel pnlContext = new JPanel(new BorderLayout());

	private BackupHistoryService dataLogService;
	@SuppressWarnings("unused")
	private BackupService backupService;
	public LogDisplayPanel(BackupHistoryService dataLogService, BackupService backupService, EventBus bus) {
		super(new MigLayout("insets 0","[grow, fill]","[300::, grow, fill][][300::, grow, fill]"));
		
		bus.register(this);
		this.dataLogService = dataLogService;
		this.backupService = backupService;
		
		table = new JTable( new Object[0][], columns );
		JScrollPane spTable = new JScrollPane(table);
		
		add(spTable, "wrap");
		add(pnlButtons, "wrap");
		add(pnlContext, "");
		
		pnlButtons.add(btnResyncBackup, "skip 1");
		pnlButtons.add(btnNewBackup, "");
		
		pnlContext.add(new RunBackupPanel(backupService, bus), BorderLayout.CENTER);
		
		initData();
	}

	/**
	 * Idempotent. Call as often as u like.
	 */
	public void initData() { //Idempotent
		BackupHistory wrapper = dataLogService.getHistory();
		if(wrapper.getEntries()==null) {
			table.setModel( new DefaultTableModel(new Object[0][], columns) );
		}else {
			int rows = wrapper.getEntries().size();
			Object[][] data = new Object[rows][];
			int i=0;
			for(BackupEntry entry:wrapper.getEntries()) {
				Object[] row = new Object[] { entry.getBackupDate(), entry.getSourceRoot(), entry.getSourceSerial(), entry.getSourceLabel(), 
						entry.getDestinationRoot(), entry.getDestinationSerial(), entry.getDestinationLabel() };
				data[i] = row;
				i++;
			}
			table.setModel( new DefaultTableModel(data, columns) );
		}
	}
	

}
