/**
 * 
 */
package com.modelesis.hotwater.view;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.modelesis.hotwater.model.ScheduleManager;

/**
 * @author ramon
 *
 */
public class ScheduleTableSelectionListener implements ListSelectionListener {

	/** Schedule manager associated with this listener. */
	protected ScheduleManager scheduleManager;
	
	/** Schedule table associated with this listener. */
	protected JTable scheduleTable;
	
	/**
	 * Constructs a new selection listener.
	 * 
	 * @param mgr Manager to associate to this listener
	 * @param table Table to associate to this listener
	 */
	public ScheduleTableSelectionListener(ScheduleManager mgr, JTable table) {
		scheduleManager = mgr;
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
			for(int segment : scheduleTable.getSelectedRows())
				if(day != 0) {	// Skip hour column selections
					if(day == 7) day = 0; // Sunday
					scheduleManager.toggle(day, segment);
				}
	}
}