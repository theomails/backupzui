package net.progressit.backupzui.ui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.miginfocom.swing.MigLayout;
import net.progressit.backupzui.logic.RealBackupService;
import net.progressit.backupzui.logic.RealBackupService.EventException;
import net.progressit.backupzui.logic.RealBackupService.EventFileProcessed;
import net.progressit.backupzui.logic.RealBackupService.EventFolderProcessed;
import net.progressit.backupzui.ui.helpers.LimitLinesDocumentListener;

public class RunBackupPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private class Comps{
		private JPanel pnlStart = new JPanel(new MigLayout("insets 5","[][grow, fill][]","[][]20[]"));
		private JPanel pnlProgress = new JPanel(new MigLayout("insets 5","[][grow, fill][]","[][][]20[]"));
		
		private JTextField txtFolderToBackup = new JTextField();
		private JButton btnFolderToBackup = new JButton("Browse...");
		private JTextField txtBackupDestination = new JTextField();	
		private JButton btnBackupDestination = new JButton("Browse...");
		
		private final JPanel pnlButtons = new JPanel(new MigLayout("insets 0","[grow][]","[]"));
		private final JButton btnStartStopBackup = new JButton("Start Backup");
		
		private final JLabel lblProgressFolder = new JLabel();
		private final JLabel lblProgressFile = new JLabel();
		private final JLabel lblProgressSummary = new JLabel();
		private final JProgressBar pbProgress = new JProgressBar();
		
		private final JPanel pnlLog = new JPanel(new MigLayout("insets 0","[grow, fill]","[][grow, fill]"));
		private final JCheckBox cbLogExceptions = new JCheckBox("Log Errors");
		private final JTextArea taLog = new JTextArea();
		private final JScrollPane spLog = new JScrollPane(taLog);
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
			
			pnlLog.add(cbLogExceptions, "wrap");
			pnlLog.add(spLog, "");
			
			//setEnabledForPanel(pnlStart, false);
			//pbProgress.setIndeterminate(true);			
		}
	}
	private Comps comps = new Comps();
	
	private File fromFolder = null;
	private File toFolder = null;
	private boolean running = false;
	
	private RealBackupService backupService;
	private EventBus bus;
	public RunBackupPanel(RealBackupService backupService) {
		super(new MigLayout("insets 0","[grow, fill]","[][][grow, fill]"));
		
		bus = backupService.getBus();
		bus.register(this);
		this.backupService = backupService;
		this.bus = backupService.getBus();
		add(comps.pnlStart, "wrap");
		add(comps.pnlProgress, "hidemode 2, wrap");
		add(comps.pnlLog, "");
		
		comps.init();
		
		comps.pnlStart.setBorder(BorderFactory.createTitledBorder("Start Backup"));
		comps.pnlProgress.setBorder(BorderFactory.createTitledBorder("Backup Progress"));
		comps.pnlLog.setBorder(BorderFactory.createTitledBorder("Backup Log"));
		
		comps.pnlProgress.setVisible(false);
		comps.spLog.setVisible(false);
		
		comps.txtFolderToBackup.setText( getDrivePathFirstOrLast(true) );
		comps.txtBackupDestination.setText( getDrivePathFirstOrLast(false) );
		
		comps.taLog.getDocument().addDocumentListener(
			    new LimitLinesDocumentListener(499) );
		
		addHandlers();
	}
	public void addHandlers() {
		comps.btnFolderToBackup.addActionListener( (e)->{
			fromFolder = browseAndGetFolder(comps.txtFolderToBackup.getText());
			if(fromFolder==null) return;
			try {
				comps.txtFolderToBackup.setText( fromFolder.getCanonicalPath() );
			} catch (IOException e1) {
				bus.post(new EventException(e1, true));
				System.err.println(e1.toString());
				e1.printStackTrace();
			}
		} );
		comps.btnBackupDestination.addActionListener( (e)->{
			toFolder = browseAndGetFolder(comps.txtBackupDestination.getText());
			if(toFolder==null) return;
			try {
				comps.txtBackupDestination.setText( toFolder.getCanonicalPath() );
			} catch (IOException e1) {
				bus.post(new EventException(e1, true));
				System.err.println(e1.toString());
				e1.printStackTrace();
			}
		} );
		
		comps.btnStartStopBackup.addActionListener( (e)->{
			if(!running) {
				Path source = Paths.get(comps.txtFolderToBackup.getText());
				Path destination = Paths.get(comps.txtBackupDestination.getText());
				comps.pnlProgress.setVisible(true);
				comps.spLog.setVisible(true);
				running = true;
				comps.btnStartStopBackup.setText("Stop Backup");
				
				startNewBackup(source, destination);
			}else {
				backupService.stop();
				comps.taLog.append("\nRequesting stop backup...\n\n"); //Space for next run
				//Dont clear in case of stop in middle, so that user can know progress.
				//comps.lblProgressFolder.setText( "" );
				//comps.lblProgressFile.setText( "" );
			}
		} );
	}
	
	//KINDA PRIVATE
	
	private String sizeText(double bytes, int decimals) {
		if(bytes<1000d) {
			decimals = 0;
			return String.format("%."+decimals+"f bytes", bytes);
		}else if(bytes<1e6d) {
			decimals = 0;
			return String.format("%."+decimals+"f KB", bytes/1e3d);
		}else if(bytes<1e9d) {
			decimals = Math.min(decimals, 1);
			return String.format("%."+decimals+"f MB", bytes/1e6d);
		}else  {
			decimals = Math.min(decimals, 2);
			return String.format("%."+decimals+"f GB", bytes/1e9d);
		}
	}
	private String getDrivePathFirstOrLast(boolean first) {
		File[] paths;
		FileSystemView fsv = FileSystemView.getFileSystemView();

		// returns pathnames for files and directory
		paths = File.listRoots();

		// for each pathname in pathname array
		File file = null;
		for(File path:paths)
		{
			file = path;
		    // prints file and directory paths
			System.out.print("\nDrive Name: "+path);
			System.out.print("\nDescription: "+fsv.getSystemTypeDescription(path));
		    
		    if(first) break;
		}


		return file.toPath().toString();
	}
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
						comps.pnlProgress.setVisible(false);
						String result = "Scanned " + sizeText( bytesScanned, 2 ) + " and Copied " + sizeText( bytesCopied, 1 ) + ".";
						comps.taLog.append("\n" + result + "\n\n\n"); //Space for next run
						bytesScanned = 0d;
						bytesCopied = 0d;
					} );
				} catch (IOException e1) {
					bus.post(new EventException(e1, false));
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
	
	private double bytesScanned=0d;
	private double bytesCopied=0d;
	@Subscribe
	private void handle(EventFileProcessed event) {
		SwingUtilities.invokeLater( ()->{
			if(event.isRealCopy()) {
				if(!event.isStart()) {
					//Log only actual copying of files.
					long fileLen = event.getToFile().toFile().length();
					comps.taLog.append( "Copied " + fileMsgRel(event) + " (" + sizeText( fileLen, 1 ) + ")" );
					comps.taLog.append("\n");
					comps.taLog.getCaret().setDot( Integer.MAX_VALUE );
					long scanBytes = event.getFromFile().toFile().length();
					bytesCopied += scanBytes;
				}
			}else {
				if(event.isStart()) {
					//Update label for checking of files also.
					long scanBytes = event.getFromFile().toFile().length();
					bytesScanned += scanBytes;
					comps.lblProgressFile.setText( html(fileMsg(event)) );
				}else {
					String result = "Scanned " + sizeText( bytesScanned,2 ) + " and Copied " + sizeText( bytesCopied,2 ) + "...";
					comps.lblProgressSummary.setText( result );
				}
			}
		} );
	}
	@Subscribe
	private void handle(EventException event) {
		if(!comps.cbLogExceptions.isSelected()) return;
		
		if(event.isFromSwing()) {
			comps.taLog.append(event.getException().toString());
			//comps.taLog.append("\n");
		}else {
			SwingUtilities.invokeLater( ()->{
				comps.taLog.append(event.getException().toString());
				//comps.taLog.append("\n");
			} );
		}
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
	private String fileMsgRel(EventFileProcessed event) {
		return event.getRelFile().toString();
	}
	
	private String html(String string) {
		return "<html>"+string+"</html>";
	}
	
	
	@SuppressWarnings("unused")
	private void setEnabledForPanel(JPanel panel, boolean enabled) {
		for(Component c:panel.getComponents()) {
			if(c instanceof JPanel) {
				setEnabledForPanel( (JPanel) c, enabled);
			}else {
				c.setEnabled(enabled);
			}
		}
	}
	
	private File browseAndGetFolder(String txtCurFolder) {
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory( new File(txtCurFolder) );
        int option = fileChooser.showOpenDialog(null);
        if(option == JFileChooser.APPROVE_OPTION){
           File file = fileChooser.getSelectedFile();
           return file;
        }
        return  null;
	}
}
