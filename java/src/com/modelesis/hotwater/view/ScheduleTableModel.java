/**
 * 
 */
package com.modelesis.hotwater.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.table.AbstractTableModel;

import com.modelesis.hotwater.model.ScheduleChangeListener;
import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The ScheduleTableModel class defines a Swing
 * table model to display the schedule in a JTable.
 * 
 * @author ramon
 *
 */
@SuppressWarnings("serial")
public class ScheduleTableModel extends AbstractTableModel
implements ScheduleChangeListener {
	
	/** Associated schedule manager. */
	protected ScheduleManager scheduleManager;
	
	/** Hour format object. */
	protected SimpleDateFormat hourFormat;
	
	/**
	 * Constructs a new instance of the class.
	 * 
	 * @param mgr Schedule manager to associate this table model to
	 */
	public ScheduleTableModel(ScheduleManager mgr) {
		scheduleManager = mgr;
		mgr.setScheduleChangeListener(this);
		hourFormat = new SimpleDateFormat("h:00aa");
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex == 0)
			return String.class;
		
		return Boolean.class;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 8;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String columnName = "";
		
		switch(columnIndex) {
			case 0 : columnName = "Hora"; break;
			case 1 : columnName = "Lunes"; break;
			case 2 : columnName = "Martes"; break;
			case 3 : columnName = "Miercoles"; break;
			case 4 : columnName = "Jueves"; break;
			case 5 : columnName = "Viernes"; break;
			case 6 : columnName = "Sabado"; break;
			case 7 : columnName = "Domingo";
		}
		
		return columnName;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return 144;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) {   // Hour/segment
			int segment = rowIndex % 6;
			if(segment > 0)
				return ":" + segment + "0";
			//
			int hour = rowIndex / 6;
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hour);
			return hourFormat.format(cal.getTime());
		}
		else { // Actual schedule data
			if(columnIndex == 7) columnIndex = 0;  // Sunday
			boolean val = scheduleManager.get(columnIndex, rowIndex);
			return val;
		}
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void scheduleChanged(int day, int segment) {
		if(day == 0) day = 7;  // Sunday
		fireTableCellUpdated(segment, day);
	}	
}