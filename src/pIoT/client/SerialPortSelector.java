/**
 * pIoT Server.
 * A server for:
 * <ul>
 * <li> storing data from pIoT nodes
 * <li> sending commands to pIoT nodes
 * <li> viewing and interpreting data
 * <li> setting up rules for reacting to events
 * </ul>
 * License: GNU GENERAL PUBLIC LICENSE Version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Package containing the Web GUI.
 */
package pIoT.client;

import java.util.ArrayList;

import pIoT.client.services.SerialService;
import pIoT.client.services.SerialServiceAsync;
import pIoT.shared.SerialPortException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * GUI for selecting the serial port and starting/stopping it.
 * @author Dario Salvi
 *
 */
public class SerialPortSelector extends Composite {

	private final SerialServiceAsync serialService = GWT.create(SerialService.class);
	private final Button startStopButton;
	final ListBox dropBox;
	
	public SerialPortSelector()  {
		HorizontalPanel decP = new HorizontalPanel();
		decP.getElement().getStyle().setMargin(5, Unit.PX);
		
		Label title = new Label("Select serial port");
		decP.add(title);

		dropBox = new ListBox(false);
		dropBox.setMultipleSelect(false);
		dropBox.getElement().getStyle().setMarginLeft(10, Unit.PX);
		update();

		decP.add(dropBox);

		startStopButton = new Button();
		startStopButton.getElement().getStyle().setMarginLeft(10, Unit.PX);
		decP.add(startStopButton);
		getStarted();

		startStopButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int index =  dropBox.getSelectedIndex();
				final String portname = dropBox.getItemText(index);
				serialService.startStop(portname, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if(result) startStopButton.setText("Stop");
						else startStopButton.setText("Start");
					}

					@Override
					public void onFailure(Throwable caught) {
						if(caught instanceof SerialPortException)
							Window.alert("Can't communicate with serial port " + portname + ".\n "+ caught.getMessage());
						else Window.alert("Can't communicate with the server.\n "+ caught.getMessage());
					}
				});
			}
		});

		initWidget(decP);
	}

	public void update(){

		serialService.getComPorts(new AsyncCallback<ArrayList<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Can't communicate with the server:\n "+ caught.getMessage());
			}
			@Override
			public void onSuccess(ArrayList<String> result) {
				if((result == null) || (result.isEmpty())){
					dropBox.setEnabled(false);
					startStopButton.setEnabled(false);
				}
				else for(String s : result){
					dropBox.setEnabled(true);
					startStopButton.setEnabled(true);
					dropBox.addItem(s);
				}

			}
		});

		serialService.getCurrentComPort(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				if((result != null) && (!result.isEmpty()))
					for(int i = 0; i<dropBox.getItemCount(); i++){
						if(dropBox.getItemText(i).equalsIgnoreCase(result))
							dropBox.setItemSelected(i, true);
					}

			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Can't communicate with the server:\n "+ caught.getMessage());
			}
		});
	}

	void getStarted(){
		serialService.isStarted(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if(result)
					startStopButton.setText("Stop");
				else startStopButton.setText("Start");
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Can't communicate with the server:\n "+ caught.getMessage());
			}
		});
	}

}
