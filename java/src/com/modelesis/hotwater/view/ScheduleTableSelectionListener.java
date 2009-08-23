/**
 * 
 */
package com.modelesis.hotwater.view;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.modelesis.hotwater.control.ToggleScheduleController;

/**
 * @author ramon
 *
 */
public class ScheduleTableSelectionListener implements ListSelectionListener {

	/** Controller associated with this listener. */
	protected ToggleScheduleController toggleScheduleController;
	
	/** Schedule table associated with this listener. */
	protected JTable scheduleTable;
	
	/**
	 * Constructs a new selection listener.
	 * 
	 * @param ctrl Controller to associate to this listener
	 * @param table Table to associate to this listener
	 */
	public ScheduleTableSelectionListener(ToggleScheduleController ctrl, JTable table) {
		toggleScheduleController = ctrl;
		scheduleTable = table;
	}
	
	/**
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent evt) {
		// Ignore selection-in-progress events
		if(evt.getValueIsAdjusting())
			return;
		
		for(int day : scheduleTable.getSelectedColumns())
			for(int segment : scheduleTable.getSelectedRows()) {
				// 'day' cannot be modified
				// lest Sunday multiple selections don't work...
				int weekDay = day;
				if(day != 0) {	// Skip hour column selections
					if(day == 7) weekDay = 0; // Sunday
					toggleScheduleController.toggleSchedule(weekDay, segment);
				}
			}
		
		// Allow immediate re-selection of same segment
		scheduleTable.clearSelection();
	}
}