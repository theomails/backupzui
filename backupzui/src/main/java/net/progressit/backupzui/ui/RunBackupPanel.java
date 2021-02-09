package net.progressit.backupzui.ui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.miginfocom.swing.MigLayout;
import net.progressit.backupzui.logic.BackupService;
import net.progressit.backupzui.logic.BackupService.EventFileProcessed;
import net.progressit.backupzui.logic.BackupService.EventFolderProcessed;

public class RunBackupPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private class Comps{
		private JPanel pnlStart = new JPanel(new MigLayout("insets 5","[][grow, fill][]","[][]20[]"));
		private JPanel pnlProgress = new JPanel(new MigLayout("insets 5","[][grow, fill][]","[][][]20[]"));
		
		private JTextField txtFolderToBackup = new JTextField("C:\\FAKEPC\\C");
		private JButton btnFolderToBackup = new JButton("Browse...");
		private JTextField txtBackupDestination = new JTextField("C:\\FAKEPC\\E\\BackupC");	
		private JButton btnBackupDestination = new JButton("Browse...");
		
		private final JPanel pnlButtons = new JPanel(new MigLayout("insets 0","[grow][]","[]"));
		private final JButton btnStartStopBackup = new JButton("Start Backup");
		
		private final JLabel lblProgressFolder = new JLabel();
		private final JLabel lblProgressFile = new JLabel();
		private final JLabel lblProgressSummary = new JLabel();
		private final JProgressBar pbProgress = new JProgressBar();
		
		
		private void init() {
			pnlStart.add(new JLabel("Folder To Backup"), "");
			pnlStart.add(txtFolderToBackup, "");
			pnlStart.add(btnFolderToBackup, "wrap");
			pnlStart.add(new JLabel("Backup Destination"), "");
			pnlStart.add(txtBackupDestination, "");
			pnlStart.add(btnBackupDestination, "wrap");
			pnlStart.add(pnlButtons, "spanx 3, grow");
			
			pnlProgress.add(new JLabel("Copying Folder:"), "");
			pnlProgress.add(lblProgressFolder, "spanx 2, grow, wrap");
			pnlProgress.add(new JLabel("Copying File:"), "");
			pnlProgress.add(lblProgressFile, "spanx 2, grow, wrap");
			pnlProgress.add(new JLabel("Progress:"), "");
			pnlProgress.add(lblProgressSummary, "spanx 2, grow, wrap");
			pnlProgress.add(pbProgress, "spanx 3, grow");
			
			pnlButtons.add(btnStartStopBackup, "skip 1");
			
			//setEnabledForPanel(pnlStart, false);
			//pbProgress.setIndeterminate(true);			
		}
	}
	private Comps comps = new Comps();
	
	private File fromFolder = null;
	private File toFolder = null;
	private boolean running = false;
	
	private BackupService backupService;
	public RunBackupPanel(BackupService backupService, EventBus bus) {
		super(new MigLayout("insets 0","[grow, fill]","[][]"));
		
		bus.register(this);
		this.backupService = backupService;
		add(comps.pnlStart, "wrap");
		add(comps.pnlProgress, "");
		
		comps.init();
		
		setBorder(BorderFactory.createTitledBorder("Start a Backup"));
		addHandlers();
	}
	public void addHandlers() {
		comps.btnFolderToBackup.addActionListener( (e)->{
			fromFolder = browseAndGetFolder();
			try {
				comps.txtFolderToBackup.setText( fromFolder.getCanonicalPath() );
			} catch (IOException e1) {
				System.err.println(e1.toString());
				e1.printStackTrace();
			}
		} );
		comps.btnBackupDestination.addActionListener( (e)->{
			toFolder = browseAndGetFolder();
			try {
				comps.txtBackupDestination.setText( toFolder.getCanonicalPath() );
			} catch (IOException e1) {
				System.err.println(e1.toString());
				e1.printStackTrace();
			}
		} );
		
		comps.btnStartStopBackup.addActionListener( (e)->{
			if(!running) {
				Path source = Paths.get(comps.txtFolderToBackup.getText());
				Path destination = Paths.get(comps.txtBackupDestination.getText());
				startNewBackup(source, destination);
				running = true;
				comps.btnStartStopBackup.setText("Stop Backup");
			}else {
				backupService.stop();
				comps.pbProgress.setIndeterminate(false);
				running = false;
				comps.btnStartStopBackup.setText("Start Backup");
				//Dont clear in case of stop in middle, so that user can know progress.
				//comps.lblProgressFolder.setText( "" );
				//comps.lblProgressFile.setText( "" );
			}
		} );
	}
	
	//KINDA PRIVATE
	private void startNewBackup(Path source, Path destination) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					SwingUtilities.invokeLater( ()->{ comps.pbProgress.setIndeterminate(true); } );
					backupService.startNewBackup(source, destination, null, false);
					running = false;
					SwingUtilities.invokeLater( ()->{ 
						comps.lblProgressFolder.setText( "" );
						comps.lblProgressFile.setText( "" );
						comps.pbProgress.setIndeterminate(false); 
						comps.btnStartStopBackup.setText("Start Backup");
					} );
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
			}
		};
		
		new Thread(r).start();
	}
	@Subscribe
	private void handle(EventFolderProcessed event) {
		SwingUtilities.invokeLater( ()->{
			if(event.isStart()) {
				comps.lblProgressFolder.setText( html(folderMsg(event)) );
			}
		} );
	}
	@Subscribe
	private void handle(EventFileProcessed event) {
		SwingUtilities.invokeLater( ()->{
			comps.lblProgressFile.setText( html(fileMsg(event)) );
		} );
	}
	
	//PRIVATE
	private String folderMsg(EventFolderProcessed event) {
		String res = "";
		
		res += " [" + event.getFlavor() + " ~ ";
		res +=  event.getOriginalRoot().relativize( event.getFlavorRoot() ) + "] :: "; //Flavor path from root
		res += event.getRelFolder().toString(); //Rel path from flavor path
		
		return res;
	}
	private String fileMsg(EventFileProcessed event) {
		return event.getRelFile().getFileName().toString();
	}
	
	private String html(String string) {
		return "<html>"+string+"</html>";
	}
	
	
	private void setEnabledForPanel(JPanel panel, boolean enabled) {
		for(Component c:panel.getComponents()) {
			if(c instanceof JPanel) {
				setEnabledForPanel( (JPanel) c, enabled);
			}else {
				c.setEnabled(enabled);
			}
		}
	}
	
	private File browseAndGetFolder() {
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(null);
        if(option == JFileChooser.APPROVE_OPTION){
           File file = fileChooser.getSelectedFile();
           return file;
        }
        return  null;
	}
}
