/**
 * 
 */
package com.modelesis.hotwater.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
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
		panel.add(createButtonPanel(scheduleTable), BorderLayout.NORTH);
		panel.add(new JScrollPane(scheduleTable));
		return panel;
	}
	
	private JPanel createButtonPanel(final JTable scheduleTable) {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		buttonPanel.setLayout(new FlowLayout());

		final JButton btnMorning = new JButton("Mañana");
		btnMorning.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				vScrollComponent(scheduleTable, 1, 4);
			}
		});
		
		buttonPanel.add(btnMorning);
		
		final JButton btnAfternoon = new JButton("Tarde");
		btnAfternoon.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				vScrollComponent(scheduleTable, 2, 4);
			}
		});
		
		buttonPanel.add(btnAfternoon);

		final JButton btnEvening = new JButton("Noche");
		btnEvening.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				vScrollComponent(scheduleTable, 3, 4);
			}
		});
		
		buttonPanel.add(btnEvening);

		final JButton btnUndo = new JButton("Deshacer");
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
	
	/**
	 * Logically split a component vertically into n fragments,
	 * then scroll vertically to the 'position'-th (1-based) fragment.
	 * 
	 * @param comp Component to scroll
	 * @param position Position or fragment to display
	 * @param nFragments Number of fragments
	 */
	private void vScrollComponent(JComponent comp, int position, int nFragments) {
		Rectangle currentVR = comp.getVisibleRect();
		int width = currentVR.width;
		int height = currentVR.height;
		int y = position * comp.getHeight() / nFragments;
		comp.scrollRectToVisible(new Rectangle(0, y, width, height));
	}
}