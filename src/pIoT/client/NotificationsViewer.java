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

import pIoT.client.services.ActionsService;
import pIoT.client.services.ActionsServiceAsync;
import pIoT.client.services.DBService;
import pIoT.client.services.DBServiceAsync;
import pIoT.shared.Node;
import pIoT.shared.notifications.NewDeviceNotification;
import pIoT.shared.notifications.Notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Viewer of notifications.
 * @author Dario Salvi
 *
 */
public class NotificationsViewer extends Composite {

	private final DBServiceAsync DB = GWT.create(DBService.class);
	private final ActionsServiceAsync actions = GWT.create(ActionsService.class);
	private final StackLayoutPanel stackPanel;
	
	/**
	 * Refresh time in milliseconds.
	 */
	private static final int REFRESH_TIME = 60000;

	/**
	 * 
	 */
	public NotificationsViewer() {
		DecoratorPanel frame = new DecoratorPanel();
		frame.getElement().getStyle().setPadding(5, Unit.PX);
		
		VerticalPanel container = new VerticalPanel();
		container.add(new HTML("Notifications"));
		
		stackPanel = new StackLayoutPanel(Unit.PX);
		stackPanel.setWidth("300px");
		stackPanel.setHeight("300px");
		stackPanel.setAnimationDuration(500);
		container.add(stackPanel);
		Timer refershTimer = new Timer() {
			@Override
			public void run() {
				update();
			}
		};
		refershTimer.scheduleRepeating(REFRESH_TIME);
		update();
		
		frame.add(container);
		initWidget(frame);
	}

	private Widget renderNotification(final Notification not){
		final VerticalPanel vert = new VerticalPanel();
		vert.add(new HTML(not.getMessage()));
		Button okButt = new Button("Dismiss");
		okButt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fixNotification(not);
			}
		});
		vert.add(okButt);
		return vert;
	}
	
	private void update(){
		stackPanel.clear();
		DB.getUnfixedNotifications(new AsyncCallback<ArrayList<Notification>>() {
			@Override
			public void onSuccess(ArrayList<Notification> result) {
				for(Notification n : result){
					if(n instanceof NewDeviceNotification)
						stackPanel.add(renderNewDeviceNotification((NewDeviceNotification)n), 
								"New device", 30);
					else
						stackPanel.add(renderNotification(n), 
								"Notification", 30);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot retrieve notifications. "+ caught.getMessage());
			}
		});
	}

	private Widget renderNewDeviceNotification(final NewDeviceNotification newde){
		final FlexTable layout = new FlexTable();
		layout.setCellSpacing(6);
		FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();
		layout.setHTML(0, 0, newde.getMessage());
		cellFormatter.setColSpan(0, 0, 2);
		cellFormatter.setHorizontalAlignment(
				0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		layout.setHTML(1, 0, "Device address");
	    layout.setHTML(1, 1, ""+newde.getDevice().getAddress());
	    
	    layout.setHTML(2, 0, "Name");
	    final TextBox namebox = new TextBox();
	    layout.setWidget(2, 1, namebox);
	    
	    layout.setHTML(3, 0, "Location");
	    final TextBox locationbox = new TextBox();
	    layout.setWidget(3, 1, locationbox);
	    
	    Button okButt = new Button("Done");
	    okButt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if((namebox.getText() == null) ||
					(namebox.getText().isEmpty())){
					Window.alert("You have to set the name of the device");
					return;
				}
				if((locationbox.getText() == null) ||
						(locationbox.getText().isEmpty())){
						Window.alert("You have to set the location of the device");
						return;
				}
				
				final Node dev = newde.getDevice();
				dev.setName(namebox.getText());
				dev.setLocation(locationbox.getText());
				DB.updateDevice(dev, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						fixNotification(newde);
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot store the device " + dev.getName()+"." + caught.getMessage());
					}
				});
			}
		});
	    layout.setWidget(4, 0, okButt);
		cellFormatter.setColSpan(4, 0, 2);
		cellFormatter.setHorizontalAlignment(
				4, 0, HasHorizontalAlignment.ALIGN_CENTER);
		return layout;
	}
	
	private void fixNotification(final Notification not){
		actions.fixNotification(not, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				update();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot mark notification as fixed. "+caught.getMessage());
				update();
			}
		});
	}
}
