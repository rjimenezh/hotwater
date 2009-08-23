/**
 * 
 */
package com.modelesis.hotwater.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import com.modelesis.hotwater.control.ViewScheduleController;

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
	
	protected ViewScheduleController controller;
	
	protected Border standardBorder;
	
	protected Color selectedColor;
	
	protected Border topSelectedBorder;
	
	protected Border midSelectedBorder;
	
	protected Border bottomSelectedBorder;
	
	/**
	 * Constructs a new cell renderer object.
	 */
	public ScheduleTableCellRenderer(ViewScheduleController ctrl) {
		super("", RIGHT);
		setOpaque(true);
		hourColumnBackground = new Color(51, 102, 153);
		hourColumnForeground = Color.white;
		standardFont = getFont();
		hourFont = new Font(
			standardFont.getName(), Font.BOLD, standardFont.getSize());
		activeSegmentBackground = new Color(255, 255, 204);
		passiveSegmentBackground = new Color(204, 204, 255);
		//		
		standardBorder = getBorder();
		selectedColor = Color.yellow;
		topSelectedBorder = BorderFactory.createCompoundBorder(standardBorder,
			BorderFactory.createMatteBorder(2, 2, 0, 2, selectedColor));
		midSelectedBorder = BorderFactory.createCompoundBorder(standardBorder,
			BorderFactory.createMatteBorder(0, 2, 0, 2, selectedColor));
		bottomSelectedBorder = BorderFactory.createCompoundBorder(standardBorder,
			BorderFactory.createMatteBorder(0, 2, 2, 2, selectedColor));
		controller = ctrl;
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
			setBorder(standardBorder);
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
			if(controller.getSelectedDay() == col)
				switch(row) {
					case 0: setBorder(topSelectedBorder); break;
					case 143: setBorder(bottomSelectedBorder); break;
					default: setBorder(midSelectedBorder);
				}
			else
				setBorder(standardBorder);
		}
		
		return this;
	}
}