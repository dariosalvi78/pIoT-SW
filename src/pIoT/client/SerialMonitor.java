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

import pIoT.client.events.SectionChangeEvent;
import pIoT.client.events.SectionChangeHandler;
import pIoT.client.events.SectionChangeEvent.Section;
import pIoT.client.services.SerialService;
import pIoT.client.services.SerialServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Viewer of the Serial data.
 * @author Dario Salvi
 *
 */
public class SerialMonitor extends ResizeComposite implements SectionChangeHandler{
	
	private static int REFRESH_INTERVAL = 1000;
	private final SerialServiceAsync serialService = GWT.create(SerialService.class);
	private final TextArea serialIn;
	private Timer refreshTimer;
	private SerialPortSelector selector;

	public SerialMonitor() {
		//Serial monitor
		final DockLayoutPanel  panel = new  DockLayoutPanel (Unit.PX);
		//panel.setWidth("97%");
		//panel.setHeight("97%");
		//panel.getElement().getStyle().setMargin(10, Unit.PX);
		
		selector = new SerialPortSelector();
		panel.addNorth(selector, 50);
		
		final Button sendButt = new Button("Send");
		sendButt.getElement().getStyle().setMargin(10,Unit.PX);
		panel.addSouth(sendButt, 50);
		
		SplitLayoutPanel splitp = new SplitLayoutPanel();
		//splitp.setWidth("100%");
		//splitp.setHeight("100%");

		final TextArea serialOut = new TextArea();
		serialOut.setTitle("Out");
		serialOut.setReadOnly(false);
		serialOut.setWidth("97%");
		//serialOut.setHeight("90%");
		serialOut.getElement().getStyle().setMargin(10, Unit.PX);
		splitp.addSouth(serialOut, 150);
		
		sendButt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String message = serialOut.getText();
				serialService.sendData(message, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						serialOut.setText("");
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot send message: \n"+caught.getMessage());
					}
				});
			}
		});
		
		serialIn = new TextArea();
		serialIn.setTitle("In");
		serialIn.setReadOnly(true);
		serialIn.setWidth("97%");
		//serialIn.setHeight("99%");
		serialIn.getElement().getStyle().setMargin(10, Unit.PX);
		splitp.add(serialIn);
		
		panel.add(splitp);
		
		initWidget(panel);
	}

	private void start(){
		//Clear text
		serialIn.setText("");
		//Start timer
		refreshTimer = new Timer() {
		      @Override
		      public void run() {
		    	  serialService.getBuffer(new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						if(result != null)
							serialIn.setText(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot connect to server:\n "+caught.getMessage());
					}
				});
		      }
		    };
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	}
	
	private void stop(){
		//Stop timer
		if(refreshTimer != null)
			refreshTimer.cancel();
	}

	@Override
	public void onSectionChanged(SectionChangeEvent evt) {
		if(evt.getCurrentSection() == Section.Serial){
			start();
			selector.update();
		}
		else stop();
	}
}
