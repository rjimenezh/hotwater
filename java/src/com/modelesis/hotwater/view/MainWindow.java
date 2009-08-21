/**
 * 
 */
package com.modelesis.hotwater.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.modelesis.hotwater.control.ScheduleController;
import com.modelesis.hotwater.model.ScheduleChangeListener;

/**
 * The application UI's main window.
 * 
 * @author ramon
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	protected ScheduleController controller;
	
	public MainWindow(ScheduleController ctrl, JTable scheduleTable) {
		super("HotWater");
		controller = ctrl;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Programación", createMainPanel(scheduleTable));
		add(tabs);
		pack();
	}
	
	private JPanel createMainPanel(JTable scheduleTable) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(createButtonPanel(), BorderLayout.NORTH);
		panel.add(new JScrollPane(scheduleTable));
		return panel;
	}
	
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		buttonPanel.setLayout(new FlowLayout());
		final JButton btnUndo = new JButton("Undo");
		btnUndo.setEnabled(controller.canUndo());
		btnUndo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				controller.undo();
			}
			
		});
		controller.addScheduleChangeListener(new ScheduleChangeListener() {
			
			@Override
			public void scheduleChanged(int day, int segment) {
				btnUndo.setEnabled(controller.canUndo());
			}
		});

		buttonPanel.add(btnUndo);
		return buttonPanel;
	}
}