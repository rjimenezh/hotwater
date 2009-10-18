/**
 * 
 */
package com.modelesis.hotwater.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.modelesis.hotwater.control.TransferScheduleController;
import com.modelesis.hotwater.control.TransferScheduleListener;
import com.modelesis.hotwater.model.seriface.SerialState;

/**
 * Implements the application UI's transfer dialog.
 * 
 * @author ramon
 *
 */
@SuppressWarnings("serial")
public class TransferDialog extends JDialog 
implements TransferScheduleListener, ItemListener {
	
	/** Transfer controller. */
	protected TransferScheduleController controller;
	
	/** Auto-detect device transfer option. */
	protected JRadioButton autoDetect;
	
	/** Explicit port selection transfer option. */
	protected JRadioButton explicitPort;
	
	/** Serial port list. */
	protected JComboBox serialPorts;	
	
	/** Transfer status. */
	protected JLabel status;
	
	/** Do transfer. */
	protected JButton btnTransfer;
	
	/** Cancel transfer. */
	protected JButton btnCancel;
	
	/**
	 * Builds a new instance of the transfer dialog.
	 * 
	 * @param mainWindow Application UI's main window
	 * @param ctrl Transfer controller
	 */
	public TransferDialog(MainWindow mainWindow, TransferScheduleController ctrl) {
		super(mainWindow, "Programar el Dispositivo", true);
		controller = ctrl; 
		controller.setListener(this);
		buildUI();
	}
	
	/**
	 * Triggered when a transfer option is
	 * selected.  Used to enable/disable
	 * serial port list as applicable.
	 */
	@Override
	public void itemStateChanged(ItemEvent evt) {
		if(evt.getSource() == autoDetect)
			serialPorts.setEnabled(false);
		else
			serialPorts.setEnabled(true);
	}

	/**
	 * Triggered when the dialog is invoked by
	 * the user.  Used to refresh transfer
	 * settings before displaying.
	 */
	@Override
	public void updateSettingsAndDisplay(boolean autoDetect, String lastValidPort) {
		if(autoDetect)
			this.autoDetect.setSelected(true);
		else
			explicitPort.setSelected(true);
		for(int i = 0; i < serialPorts.getItemCount(); i++)
			if(serialPorts.getItemAt(i).equals(lastValidPort)) {
				serialPorts.setSelectedIndex(i);
				break;
			}
		setVisible(true);
	}

	/**
	 * Triggered by in-progress transfers.
	 * Updates status, chains asynchronous events.
	 */
	@Override
	public void transferEvent(SerialState state, String info) {
		switch(state) {
			case PROBING_PORT:
				status.setText("Intentando detectando HotWater en puerto " + info
					+ "...");
				break;
			case HOTWATER_FOUND:
				status.setText("Dispositivo HotWater detectado en " + info);
				if(info != null)
					controller.transferData(info);
				break;
			case DETECTION_FAILED:
				JOptionPane.showMessageDialog(this,
					"No se encontró un dispositivo HotWater conectado.\n" +
					"Por favor asegúrese de que hay un dispositivo conectado.\n" +
					"Si este mensaje persiste, intente especificar " +
					"explícitamente\nel puerto a utilizar",
					"Detección Automática Falló",
					JOptionPane.ERROR_MESSAGE);
				break;
			case CONNECTING:
				status.setText(
					"Estableciendo conexión con dispositivo (" + info + ")...");
				break;
			case SENDING_DATA:
				status.setText("Programando dispositivo HotWater...");
				btnTransfer.setEnabled(false);
				btnCancel.setEnabled(false);
				break;
			case DATA_SENT:
				JOptionPane.showMessageDialog(this,
					"El dispositivo HotWater fue programado exitosamente",
					"Programación Exitosa",
					JOptionPane.INFORMATION_MESSAGE);
				btnTransfer.setEnabled(true);
				btnCancel.setEnabled(true);
				status.setText("");
				setVisible(false);
				controller.persistSettings(autoDetect.isSelected(),
					serialPorts.isEnabled() ? (String)serialPorts.getSelectedItem()
					: null);
				break;
			case TRANSFER_ERROR:
				JOptionPane.showMessageDialog(this,
					"La programación del dispositivo HotWater falló.\n" +
					"Por favor, verifique la conexión e intente de nuevo",
					"Error Programando el Dispositivo",
					JOptionPane.ERROR_MESSAGE);
				btnTransfer.setEnabled(true);
				btnCancel.setEnabled(true);
				status.setText("");
				setVisible(false);
		}
	}

	/**
	 * Triggered when the serial port list
	 * has changed.
	 */
	@Override
	public void updateSerialPortList(String[] portList) {
		serialPorts.removeAllItems();
		for(String port : portList)
			serialPorts.addItem(port);
	}
	
	/**
	 * Builds the user interface elements of the dialog.
	 */
	private void buildUI() {
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(4, 1));
		Border optionsBorder =
			BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		optionsBorder = BorderFactory.createTitledBorder(
			optionsBorder, "Conexión al Dispositivo");
		optionsPanel.setBorder(optionsBorder);
		ButtonGroup options = new ButtonGroup();
		autoDetect =
			new JRadioButton("Auto-detectar dispositivo (recomendado)");
		autoDetect.addItemListener(this);
		options.add(autoDetect); optionsPanel.add(autoDetect);
		explicitPort =
			new JRadioButton("El dispositivo HotWater está en el puerto siguiente:");
		explicitPort.addItemListener(this);
		options.add(explicitPort); optionsPanel.add(explicitPort);
		serialPorts = new JComboBox();
		optionsPanel.add(serialPorts);
		add(optionsPanel);
		//
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(2, 1));
		status = new JLabel("", SwingConstants.CENTER);
		bottomPanel.add(status);
		//
		btnTransfer = new JButton("Programar");
		btnTransfer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(autoDetect.isSelected()) {
					controller.detectDevice();
				}
				else
					controller.transferData((String)serialPorts.getSelectedItem());
			}			
		});
		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status.setText("");
				controller.cancel();
				setVisible(false);
			}
		});
		JPanel buttons = new JPanel();
		buttons.add(btnTransfer);
		buttons.add(btnCancel);
		bottomPanel.add(buttons);
		add(bottomPanel, BorderLayout.SOUTH);
		//
		pack();
	}
}