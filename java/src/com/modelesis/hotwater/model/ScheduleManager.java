/**
 * 
 */
package com.modelesis.hotwater.model;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.modelesis.hotwater.model.seriface.SerialInterface;

/**
 * The ScheduleManager class defines
 * the external (component) interface
 * of the scheduling mechanism.
 * 
 * @author ramon
 *
 */
public class ScheduleManager {
	
	/** Preferences key for schedule. */
	private static final String SCHEDULE_KEY = "HOTWATER_SCHEDULE";
	
	/** Underlying schedule. */
	protected Schedule schedule;
	
	/** Last saved schedule. */
	protected Schedule lastSavedSchedule;
	
	/** Mementos used for undo and redo. */
	protected List<ScheduleMemento> mementos;
	
	/**
	 * Creates a new instance of ScheduleManager.
	 */
	public ScheduleManager() {
		schedule = new Schedule();
		mementos = new ArrayList<ScheduleMemento>();
	}

	/**
	 * Binds the specified schedule change listener
	 * with the managed schedule.
	 * 
	 * @param lst Schedule change listener to bind to schedule
	 */
	public void addScheduleChangeListener(ScheduleChangeListener lst) {
		schedule.addScheduleChangeListener(lst);
	}
	
	/**
	 * Loads the schedule from
	 * the current user's preferences store.
	 */
	public void loadSchedule() {
		Preferences prefs = Preferences.userNodeForPackage(ScheduleManager.class);
		String serSched = prefs.get(SCHEDULE_KEY, "");
		ScheduleDAO dao = new ScheduleDAO();
		Schedule deSerSched = dao.deSerialize(serSched);
		schedule = new Schedule(deSerSched); // so as not to affect listeners! 
		lastSavedSchedule = new Schedule(schedule);
	}
	
	/**
	 * States whether the schedule has pending
	 * changes against its last loaded version.
	 *  
	 * @return Whether the schedule is 'dirty'
	 */
	public boolean hasChangedSinceLastSaved() {
		return !(schedule.equals(lastSavedSchedule));
	}
	
	/**
	 * Saves the schedule from the
	 * current user's preferences store.
	 */
	public void saveSchedule() {
		ScheduleDAO dao = new ScheduleDAO();
		String serSched = dao.serialize(schedule);
		Preferences prefs = Preferences.userNodeForPackage(ScheduleManager.class);
		prefs.put(SCHEDULE_KEY, serSched);
		// lastSavedSchedule could be null if
		// the schedule is being saved without having
		// being loaded first, or if loading failed
		if(lastSavedSchedule != null)
			schedule.copyInto(lastSavedSchedule);
	}
	
	/**
	 * Delegate method for {@link Schedule#get(int, int)}
	 * 
	 * @param weekDay Weekday: 0 - Sunday, 6 - Saturday
	 * @param segment Segment: 0 - 00:00/00:09; 143 - 23:50/23:59
	 * @return Whether heater power is on at the specified slot
	 */
	public boolean get(int weekDay, int segment) {
		return schedule.get(weekDay, segment);
	}

	/**
	 * Delegate method for {@link Schedule#normalizeWeek(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeek(int weekDay) {
		mementos.add(schedule.getMemento());
		try {
			schedule.normalizeWeek(weekDay);
		}
		catch(Throwable t) {
			mementos.remove(mementos.size() - 1);
		}
	}

	/**
	 * Delegate method for {@link Schedule#normalizeWeekDays(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeekDays(int weekDay) {
		mementos.add(schedule.getMemento());
		try {
			schedule.normalizeWeekDays(weekDay);
		}
		catch(Throwable t) {
			mementos.remove(mementos.size() - 1);
		}
	}

	/**
	 * Delegate method for {@link Schedule#normalizeWeekends(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeekends(int weekDay) {
		mementos.add(schedule.getMemento());
		try {
			schedule.normalizeWeekends(weekDay);
		}
		catch(Throwable t) {
			mementos.remove(mementos.size() - 1);
		}
	}

	/**
	 * Delegate method for {@link Schedule#toggle(int, int)}
	 * 
	 * @param weekDay Weekday: 0 - Sunday, 6 - Saturday 
	 * @param segment Segment: 0 - 00:00/00:09; 143 - 23:50/23:59
	 */
	public void toggle(int weekDay, int segment) {
		mementos.add(schedule.getMemento());
		try {
			schedule.toggle(weekDay, segment);
		}
		catch(Throwable t) {
			mementos.remove(mementos.size() - 1);
		}
	}
	
	/**
	 * Defines whether undo operations are available.
	 * 
	 * @return Whether calling undo() will actually undo anything.
	 */
	public boolean canUndo() {
		return !(mementos.isEmpty());
	}
	
	/**
	 * Undoes latest change to the schedule.
	 */
	public void undo() {
		int lastMemento = mementos.size();
		if(lastMemento > 0) {
			lastMemento--;
			schedule.restoreStatus(mementos.remove(lastMemento));
		}	
	}
	
	/**
	 * Transfer scheduling data to
	 * a HotWater device, over the
	 * specified serial interface and
	 * using the specified port.
	 * 
	 * @param serialInterface Serial interface to transfer over
	 * @param port Port where the HotWater device listens
	 */
	public void transferData(SerialInterface serialInterface, String port) {
		ScheduleDAO dao = new ScheduleDAO();
		byte[] data = dao.serializeForTransfer(schedule);
		serialInterface.transferData(port, data);
	}
}