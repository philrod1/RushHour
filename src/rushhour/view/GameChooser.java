package rushhour.view;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import games.Games;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import rushhour.controller.RushHour;
import rushhour.model.game.GameAnalysis;
import rushhour.tools.FileTools;

public class GameChooser extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = -980561393532392085L;
	private JTextPane htmlPane;
	private JTree tree;
	// private URL helpURL;
	// private static boolean DEBUG = false;
	private RushHour controller;

	// Optionally play with line styles. Possible values are
	// "Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";

	public GameChooser(RushHour controller) {
		super(new GridLayout(1, 0));
		this.controller = controller;
		// Create the nodes.
		Games games = new Games();
		URL url = games.getClass().getResource("");
		System.out.println(url.getFile());
		File dir = null;
		dir = new File(url.getPath());

		DefaultMutableTreeNode top = getNode(dir);
		setPreferredSize(new Dimension(100, 600));
		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			System.out.println("line style = " + lineStyle);
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		// Create the HTML viewing pane.
		htmlPane = new JTextPane();
		htmlPane.setContentType("text/html");
		htmlPane.setEditable(false);
		JScrollPane htmlView = new JScrollPane(htmlPane);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JSplitPane bottomPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(bottomPane);
		SetupPanel setupPanel = new SetupPanel(controller);
		// setupPanel.setPreferredSize(new Dimension(100,180));
		// setupPanel.setBounds(0, 0, 100, 180);
		bottomPane.setTopComponent(setupPanel);
		bottomPane.setBottomComponent(htmlView);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		htmlView.setMaximumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		// setPreferredSize(new Dimension(220, 500));
		splitPane.setDividerLocation(260);
		// splitPane.setPreferredSize(new Dimension(200, 500));

		// Add the split pane to this panel.
		add(splitPane);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {
			System.out.println(nodeInfo.toString());
			FileInfo fi = (FileInfo) nodeInfo;
			displayFile(fi.getFile(), fi.getName());
		}
	}

	private class FileInfo {
		public String filename;
		public String file;

		public FileInfo(String file, String filename) {
			this.file = file;
			this.filename = filename;
		}
		
		public String getName() {
			return (filename);
		}

		public FileInfo(File file, String filename) {

			this.file = FileTools.readFileContents(file) + "\n";
			this.filename = filename;
		}

		public String toString() {
			return filename;
		}

		public String getFile() {
			return file;
		}
	}

	private DefaultMutableTreeNode getNode(File file) {

		Class<?> type = new Games().getClass();
		String me = type.getName().replace(".", "/") + ".class";
		URL dirURL = type.getClassLoader().getResource(me);
		JarFile jar = null;
		try {
			jar = new JarFile(URLDecoder.decode(
					dirURL.getPath()
							.substring(5, dirURL.getPath().indexOf("!")),
					"UTF-8"));
		} catch (Exception e) {

		}

		if (jar != null) {
			System.out.println("JAR!");
			Enumeration<JarEntry> entries = jar.entries();
			List<String> files = new LinkedList<String>();
			while (entries.hasMoreElements()) {
				JarEntry next = entries.nextElement();
				String name = next.getName();
				if(name.endsWith(".rh")) {
					files.add(name);
				}
			}
			return getNodeString(files);
		} else {
			return getNodeFile(file);
		}
	}
	
	private DefaultMutableTreeNode getNodeString(List<String> files) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode();
		Games games = new Games();
		
		for (String file : files) {
			System.out.println("File: " + file.substring(6));
			InputStream in = games.getClass().getResourceAsStream(file.substring(6));
			String content = FileTools.slurp(in);
			node.add(new DefaultMutableTreeNode(
					new FileInfo(content , file)));
		}
		return node;
	}

	private DefaultMutableTreeNode getNodeFile(File file) {
		DefaultMutableTreeNode node = null;
		if (file.isDirectory()) {
			node = new DefaultMutableTreeNode(file.getName());
			File[] files = file.listFiles();
			for (File newfile : files) {
				DefaultMutableTreeNode newNode = getNodeFile(newfile);
				if (newNode != null) {
					node.add(newNode);
				}
			}
		} else if (file.getName().endsWith(".rh")) {
			node = new DefaultMutableTreeNode(
					
					new FileInfo(file, file.getName()));
		}
		return node;
	}

	private void displayFile(String file, String name) {
		if (file != null) {
			GameAnalysis ga = new GameAnalysis(file, name);
			controller.setGameAnalysis(ga);
			htmlPane.setEditable(false);
			htmlPane.setText(ga.toHTML());
		} else { // null url
			controller.setMessage("File Not Found");
		}
	}

	public void setHTML(String html) {
		htmlPane.setText(html);
	}

}
