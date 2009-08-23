/**
 * 
 */
package com.modelesis.hotwater.view;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import com.modelesis.hotwater.control.ToggleScheduleController;
import com.modelesis.hotwater.control.ViewScheduleController;
import com.modelesis.hotwater.model.ScheduleManager;

/**
 * @author ramon
 *
 */
public class ScheduleTableMock {
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame frame = new JFrame("HotWater");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		ScheduleManager mgr = new ScheduleManager();
		mgr.toggle(1, 45);
		mgr.toggle(1, 46);
		mgr.toggle(1, 47);
		//
		JTable schedule = new JTable();
		ScheduleTableCellRenderer renderer = new ScheduleTableCellRenderer();
		ScheduleTableSelectionListener listener =
			new ScheduleTableSelectionListener(
				new ToggleScheduleController(mgr), schedule);
		schedule.setModel(new ScheduleTableModel(new ViewScheduleController(mgr)));
		schedule.getSelectionModel().addListSelectionListener(listener);
		schedule.setDefaultRenderer(String.class, renderer);
		schedule.setDefaultRenderer(Boolean.class, renderer);
		schedule.setFillsViewportHeight(true);
		schedule.setCellSelectionEnabled(true);
		JScrollPane jsp = new JScrollPane(schedule);
		frame.add(jsp);
		frame.pack();
		frame.setVisible(true);
	}
}
