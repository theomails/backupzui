package net.progressit.backupzui.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.miginfocom.swing.MigLayout;

public class NavPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final JTree tree;
	private final DefaultMutableTreeNode root;

	public NavPanel() {
		super(new MigLayout("insets 0", "[grow, fill]", "[grow, fill]"));

		root = new DefaultMutableTreeNode("The Java Series");
		createNodes();
		tree = new JTree(root);
		tree.setRootVisible(false);

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		tree.setCellRenderer(renderer);

		JScrollPane treeView = new JScrollPane(tree);
		treeView.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createEmptyBorder(5,5,5,5)));
		treeView.setBackground(tree.getBackground());
		add(treeView);
	}

	private void createNodes() {
		DefaultMutableTreeNode tmpNode = null;
		tmpNode = new DefaultMutableTreeNode("Backup Log");

		root.add(tmpNode);
	}
}
