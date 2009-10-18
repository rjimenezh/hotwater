/**
 * 
 */
package com.modelesis.hotwater.app;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;

import com.modelesis.hotwater.control.NormalizationController;
import com.modelesis.hotwater.control.SchedulePersistenceController;
import com.modelesis.hotwater.control.ScrollScheduleController;
import com.modelesis.hotwater.control.ToggleScheduleController;
import com.modelesis.hotwater.control.TransferScheduleController;
import com.modelesis.hotwater.control.UndoController;
import com.modelesis.hotwater.control.ViewScheduleController;
import com.modelesis.hotwater.model.ScheduleChangeListener;
import com.modelesis.hotwater.model.ScheduleManager;
import com.modelesis.hotwater.model.seriface.SerialInterface;
import com.modelesis.hotwater.view.MainWindow;
import com.modelesis.hotwater.view.ScheduleTableCellRenderer;
import com.modelesis.hotwater.view.ScheduleTableModel;
import com.modelesis.hotwater.view.ScheduleTableSelectionListener;
import com.modelesis.hotwater.view.TransferDialog;

/**
 * The ApplicationBuilder class builds and 'wires'
 * application objects.
 * 
 * @author ramon
 */
public class ApplicationBuilder {
	
	/** Schedule manager. */
	protected ScheduleManager scheduleManager;
	
	/** Application UI main window. */
	protected MainWindow mainWindow;
	
	/** Scheduling data transfer controller. */
	protected TransferScheduleController transferController;
	
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
		// The following line automatically loads the
		// last saved schedule
		SchedulePersistenceController persistenceController = 
			new SchedulePersistenceController(scheduleManager);		
		//
		JTable scheduleTable = buildScheduleTable();
		JPanel buttonPanel = buildButtonPanel(persistenceController, scheduleTable);
		mainWindow = new MainWindow(scheduleTable, buttonPanel);
		WindowListener mainWindowListener =
			buildMainWindowListener(persistenceController, mainWindow);
		mainWindow.addWindowListener(mainWindowListener);
		//
		SerialInterface serialInterface = new SerialInterface();
		transferController =
			new TransferScheduleController(scheduleManager, serialInterface);
		// The controller will, indirectly, reference the dialog
		new TransferDialog(mainWindow, transferController);
	}
	
	/**
	 * Starts the application from a user perspective.
	 */
	public void startApp() {
		mainWindow.setVisible(true);
	}

	/**
	 * Builds the application UI's schedule table.
	 * 
	 * @return Instantiated schedule table
	 */
	private JTable buildScheduleTable() {
		JTable scheduleTable = new JTable();
		scheduleTable.setFillsViewportHeight(true);
		scheduleTable.setCellSelectionEnabled(true);
		//
		ViewScheduleController viewScheduleController =
			new ViewScheduleController(scheduleManager); 
		scheduleTable.setModel(new ScheduleTableModel(viewScheduleController));
		//
		ScheduleTableSelectionListener listener =			
			new ScheduleTableSelectionListener(
				new ToggleScheduleController(scheduleManager), scheduleTable);
		scheduleTable.getSelectionModel().addListSelectionListener(listener);
		//
		ScheduleTableCellRenderer renderer = new ScheduleTableCellRenderer(
			viewScheduleController);
		scheduleTable.setDefaultRenderer(String.class, renderer);
		scheduleTable.setDefaultRenderer(Boolean.class, renderer);
		//
		return scheduleTable; 
	}
	
	/**
	 * Builds the application UI's button panel.
	 * 
	 * @return Instantiated button panel
	 */
	private JPanel buildButtonPanel(
	SchedulePersistenceController persistenceController, JTable scheduleTable) {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		buttonPanel.setLayout(new FlowLayout());
		//
		ScrollScheduleController scrollController =
			new ScrollScheduleController(scheduleManager, scheduleTable);
		buttonPanel.add(buildMorningButton(scrollController));
		buttonPanel.add(buildAfternoonButton(scrollController));
		buttonPanel.add(buildEveningButton(scrollController));
		//
		buildNormalizeButtons(buttonPanel);
		//
		UndoController undoController = new UndoController(scheduleManager);
		buttonPanel.add(buildUndoButton(undoController));
		//
		buttonPanel.add(buildSaveButton(persistenceController));
		//
		buttonPanel.add(buildTransferButton());
		//
		return buttonPanel;
	}
	
	/**
	 * Builds the application UI's "Scroll to morning" button.
	 * 
	 * @return Instantiated morning scroll button
	 */
	private JButton buildMorningButton(
	final ScrollScheduleController scrollController) {
		JButton btnMorning = new JButton("Mañana");
		btnMorning.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				scrollController.scrollToMorning();
			}
		});
		return btnMorning;
	}

	/**
	 * Builds the application UI's "Scroll to afternoon" button.
	 * 
	 * @return Instantiated afternoon scroll button
	 */
	private JButton buildAfternoonButton(
	final ScrollScheduleController scrollController) {
		JButton btnAfternoon = new JButton("Tarde");
		btnAfternoon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				scrollController.scrollToAfternoon();
			}
		});
		return btnAfternoon;
	}

	/**
	 * Builds the application UI's "Scroll to evening" button.
	 * 
	 * @return Instantiated evening scroll button
	 */
	private JButton buildEveningButton(
	final ScrollScheduleController scrollController) {
		JButton btnEvening = new JButton("Noche");
		btnEvening.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				scrollController.scrollToEvening();
			}
		});
		return btnEvening;
	}
	
	/**
	 * Builds the normalize UI buttons and adds them
	 * to the button panel.
	 * 
	 * @param buttonPanel Panel to add normalized buttons to.
	 */
	private void buildNormalizeButtons(JPanel buttonPanel) {
		final NormalizationController controller =
			new NormalizationController(scheduleManager);
		final JButton btnWorkdays = new JButton("L-V");
		btnWorkdays.setEnabled(controller.canNormalize());
		btnWorkdays.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				controller.normalizeWorkDays();
			}
		});
		buttonPanel.add(btnWorkdays);
		//
		final JButton btnWeekends= new JButton("S-D");
		btnWeekends.setEnabled(controller.canNormalize());
		btnWeekends.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				controller.normalizeWeekends();
			}
		});
		buttonPanel.add(btnWeekends);
		//
		final JButton btnWeek = new JButton("L-D");
		btnWeek.setEnabled(controller.canNormalize());
		btnWeek.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				controller.normalizeWeek();
			}
		});		
		buttonPanel.add(btnWeek);
		//
		ScheduleChangeListener changeListener = new ScheduleChangeListener() {
			public void scheduleChanged(int day, int segment) {
				boolean canNormalize = controller.canNormalize();
				btnWorkdays.setEnabled(canNormalize);
				btnWeekends.setEnabled(canNormalize);
				btnWeek.setEnabled(canNormalize);
			}
		};
		scheduleManager.addScheduleChangeListener(changeListener);
	}
	
	/**
	 * Builds the application UI's "Undo" button.
	 * 
	 * @return Instantiated undo button
	 */
	private JButton buildUndoButton(
	final UndoController undoController) {
		final JButton btnUndo = new JButton("Deshacer");
		btnUndo.setEnabled(undoController.canUndo());
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				undoController.undo();
			}
		});
		undoController.addScheduleChangeListener(new ScheduleChangeListener() {
			public void scheduleChanged(int day, int segment) {
				btnUndo.setEnabled(undoController.canUndo());
			}
		});
		return btnUndo;
	}
	
	/**
	 * Builds the application UI's "Save" button.
	 * 
	 * @return Instantiated save button
	 */
	private Component buildSaveButton(
	final SchedulePersistenceController persistenceController) {
		final JButton btnSave = new JButton("Guardar");
		btnSave.setEnabled(persistenceController.hasPendingChanges());
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				persistenceController.save();
				btnSave.setEnabled(false);
			}
		});
		persistenceController.addScheduleChangeListener(
			new ScheduleChangeListener() {
				public void scheduleChanged(int day, int segment) {
					btnSave.setEnabled(persistenceController.hasPendingChanges());
				}
		});
		return btnSave;
	}
	
	
	/**
	 * Builds the application UI's "Transfer" button.
	 * 
	 * @return Instantiated transfer button
	 */
	private Component buildTransferButton() {
		JButton btnTransfer = new JButton("Programar");
		btnTransfer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				transferController.showTransferDialog();
			}
		});
		return btnTransfer;
	}
	
	/**
	 * Builds the main window listener,
	 * which prevents the main window
	 * from being closed if there are
	 * unsaved changes to the schedule,
	 * unless the user explicitly states
	 * so.
	 * 
	 * @param persistenceController
	 * @param mainWindow
	 * @return MainWindow listener
	 */
	private WindowListener buildMainWindowListener(
	final SchedulePersistenceController persistenceController,
	final MainWindow mainWindow) {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				boolean canExit = true;
				if(persistenceController.hasPendingChanges()) {
					if(JOptionPane.showConfirmDialog(
						mainWindow,
						"Hay cambios pendientes de guardar.  ¿Desea salir sin guardarlos?",
						"¿Salir sin Guardar Cambios?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
					) == JOptionPane.NO_OPTION)
						canExit = false;
				}
				if(canExit)
					System.exit(0);
			}
		};
	}
}