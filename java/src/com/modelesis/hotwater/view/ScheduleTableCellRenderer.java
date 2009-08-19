/**
 * 
 */
package com.modelesis.hotwater.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * The ScheduleTableCellRenderer class
 * offers color-coded display of schedule
 * values in a JTable.
 * 
 * @author ramon
 */
@SuppressWarnings("serial")
public class ScheduleTableCellRenderer extends JLabel implements TableCellRenderer {
	
	/** Background color for hour column. */
	protected Color hourColumnBackground;
	
	/** Text color for hour column. */
	protected Color hourColumnForeground;
	
	/** Standard (segment/data) font. */
	protected Font standardFont;
	
	/** Hour font. */
	protected Font hourFont;
	
	/** Background color for active (heater on) segments. */
	protected Color activeSegmentBackground;
	
	/** Background color for passive (heater off) segments. */
	protected Color passiveSegmentBackground;
	
	/**
	 * Constructs a new cell renderer object.
	 */
	public ScheduleTableCellRenderer() {
		super("", RIGHT);
		setOpaque(true);
		hourColumnBackground = new Color(51, 102, 153);
		hourColumnForeground = Color.white;
		standardFont = getFont();
		hourFont =
			new Font(standardFont.getName(), Font.BOLD, standardFont.getSize());
		activeSegmentBackground = new Color(255, 255, 204);
		passiveSegmentBackground = new Color(204, 204, 255); 
	}

	/**
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {
		if(col == 0) {  // Hour/segment
			setBackground(hourColumnBackground);
			setForeground(hourColumnForeground);
			setText((String)value);
			if(row % 6 == 0)  // Hour per se
				setFont(hourFont);
		}
		else {			// Schedule data
			setFont(standardFont);
			setText("");
			boolean active = (Boolean)value;
			if(active)
				setBackground(activeSegmentBackground);
			else
				setBackground(passiveSegmentBackground);
		}
		
		return this;
	}
}