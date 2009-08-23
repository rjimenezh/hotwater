/**
 * 
 */
package com.modelesis.hotwater.control;

import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The ScrollScheduleController class
 * realizes the Scroll Schedule use case.
 * 
 * @author ramon
 */
public class ScrollScheduleController extends UseCaseController {
	
	/** Schedule to scroll based on user input. */
	protected JTable scheduleTable;
	
	/**
	 * Creates a new instance of the controller.
	 * 
	 * @param mgr Associated schedule manager
	 * @param table JTable to manipulate based on user input.
	 */
	public ScrollScheduleController(ScheduleManager mgr, JTable table) {
		super(mgr);
		scheduleTable = table;
	}
	
	/**
	 * Scroll the schedule table to show (approximately)
	 * the morning hours.
	 */
	public void scrollToMorning() {
		vScrollComponent(scheduleTable, 1, 4);
	}
	
	/**
	 * Scroll the schedule table to show (approximately)
	 * the afternoon hours.
	 */
	public void scrollToAfternoon() {
		vScrollComponent(scheduleTable, 2, 4);
	}
	
	/**
	 * Scroll the schedule table to show (approximately)
	 * the evening hours.
	 */
	public void scrollToEvening() {
		vScrollComponent(scheduleTable, 3, 4);
	}
	
	/**
	 * Logically split a component
	 * into 'n' vertical, equal-size segments,
	 * then scroll vertically to the top of the
	 * 'i'-th (0-based) segment.
	 * 
	 * @param comp Component to scroll
	 * @param i Segment to scroll to
	 * @param n Number of segments
	 */
	private void vScrollComponent(JComponent comp, int i, int n) {
		Rectangle currentVR = comp.getVisibleRect();
		int width = currentVR.width;
		int height = currentVR.height;
		int y = i * comp.getHeight() / n;
		comp.scrollRectToVisible(new Rectangle(0, y, width, height));
	}
}