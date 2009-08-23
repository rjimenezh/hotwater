/**
 * 
 */
package com.modelesis.hotwater.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

/**
 * The application UI's main window.
 * 
 * @author ramon
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	/**
	 * Builds the application UI's main window.
	 * 
	 * @param scheduleTable Schedule table to display
	 * @param buttonPanel Application button panel
	 */
	public MainWindow(JTable scheduleTable, JPanel buttonPanel) {
		super("HotWater");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JTabbedPane tabs = new JTabbedPane();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		mainPanel.add(new JScrollPane(scheduleTable));
		tabs.addTab("Programación", mainPanel);
		add(tabs);
		pack();
	}
}