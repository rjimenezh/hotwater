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
	
	/** Schedule manager. */
	protected ScheduleManager scheduleManager;
	
	/** High-level schedule controller. */
	protected ScheduleController scheduleController;
	
	/** Application UI main window. */
	protected MainWindow mainWindow;
	
	/**
	 * Builds the application by
	 * instantiating and connecting
	 * objects among them.
	 */
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
	
	/**
	 * Builds the application UI's schedule table,
	 * which relies on some model objects.
	 * 
	 * @return Instantiated schedule table
	 */
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
	
	/**
	 * Starts the application from a user perspective.
	 */
	public void startApp() {
		mainWindow.setVisible(true);
	}
}