/**
 * 
 */
package com.modelesis.hotwater.app;

import javax.swing.JTable;
import javax.swing.UIManager;

import com.modelesis.hotwater.control.ScheduleController;
import com.modelesis.hotwater.model.ScheduleManager;
import com.modelesis.hotwater.view.MainWindow;
import com.modelesis.hotwater.view.ScheduleTableCellRenderer;
import com.modelesis.hotwater.view.ScheduleTableModel;
import com.modelesis.hotwater.view.ScheduleTableSelectionListener;

/**
 * The AppFactory class builds and 'wires'
 * application objects.
 * 
 * @author ramon
 */
public class ApplicationFactory {
	
	protected ScheduleManager scheduleManager;
	
	protected ScheduleController scheduleController;
	
	protected MainWindow mainWindow;
	
	public void buildApp() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {e.printStackTrace();}

		scheduleManager = new ScheduleManager();
		scheduleController = new ScheduleController(scheduleManager);
		JTable scheduleTable = buildScheduleTable();
		mainWindow = new MainWindow(scheduleController, scheduleTable);
	}
	
	private JTable buildScheduleTable() {
		JTable scheduleTable = new JTable();
		ScheduleTableCellRenderer renderer = new ScheduleTableCellRenderer();
		ScheduleTableSelectionListener listener =
			new ScheduleTableSelectionListener(scheduleManager, scheduleTable);
		scheduleTable.setModel(new ScheduleTableModel(scheduleManager));
		scheduleTable.getSelectionModel().addListSelectionListener(listener);
		scheduleTable.setDefaultRenderer(String.class, renderer);
		scheduleTable.setDefaultRenderer(Boolean.class, renderer);
		scheduleTable.setFillsViewportHeight(true);
		scheduleTable.setCellSelectionEnabled(true);
		return scheduleTable; 
	}
	
	public void startApp() {
		mainWindow.setVisible(true);
	}
}