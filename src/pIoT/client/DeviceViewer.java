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

import pIoT.client.events.SectionChangeEvent;
import pIoT.client.events.SectionChangeHandler;
import pIoT.client.events.SectionChangeEvent.Section;
import pIoT.client.services.DBService;
import pIoT.client.services.DBServiceAsync;
import pIoT.shared.Node;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that shows all the devices and allows modifying the details.
 * @author Dario Salvi
 *
 */
public class DeviceViewer extends Composite implements SectionChangeHandler{

	private final DBServiceAsync DB = GWT.create(DBService.class);
	private final FlowPanel panel;
	
	public DeviceViewer() {
		
		panel = new FlowPanel();
		
		update();
		
		initWidget(panel);
	}
	
	private void update(){
		panel.clear();
		DB.getDevices(new AsyncCallback<ArrayList<Node>>() {
			
			@Override
			public void onSuccess(ArrayList<Node> result) {
				for(Node dev : result){
					Widget devwidget = renderDevice(dev);
					devwidget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					devwidget.getElement().getStyle().setMargin(5, Unit.PX);
					
					panel.add(devwidget);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot retrieve the list of devices.\n" + caught.getMessage());
			}
		});
	}
	
	private Widget renderDevice(final Node dev){
		DecoratorPanel frame = new DecoratorPanel();
		FlexTable layout = new FlexTable();
		layout.setCellSpacing(5);
		
		String title = "Device addr "+dev.getAddress();
		layout.setHTML(0, 0, title);
		layout.getFlexCellFormatter().setColSpan(0, 0, 2);
		layout.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		layout.setHTML(1, 0, "name");
		final TextBox nameBox = new TextBox();
		nameBox.setText(dev.getName());
		layout.setWidget(1, 1, nameBox);
		
		layout.setHTML(2, 0, "location");
		final TextBox locationBox = new TextBox();
		locationBox.setText(dev.getLocation());
		layout.setWidget(2, 1, locationBox);
		
		layout.setHTML(3, 0, "last contact");
		if(dev.getLastContact() != null)
			layout.setHTML(3, 1, dev.getLastContact().toString());
		
		Button saveButt = new Button("Update");
		saveButt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Node newDev = new Node(dev.getAddress(), nameBox.getText(), locationBox.getText());
				newDev.setLastContact(dev.getLastContact());
				DB.updateDevice(newDev, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						update();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot update device with address "+ dev.getAddress()+".\n" +caught.getMessage());
						update();
					}
				});
			}
		});
		layout.setWidget(4, 0, saveButt);
		layout.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		
		Button deleteButt = new Button("Delete");
		deleteButt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ConfirmationDialog conf = new ConfirmationDialog(
						"This will delete all messages, are you sure you want to proceed?", new ConfirmationDialog.ConfirmationHandler() {
					
					@Override
					public void confirm(boolean confirmed) {
						if(confirmed){
							DB.deleteDevice(dev, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void result) {
									update();
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Cannot delete device. "+caught.getMessage());
								}
							});
						}
					}
				});
				conf.center();
			}
		});
		layout.setWidget(4, 1, deleteButt);
		layout.getFlexCellFormatter().setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		
		frame.add(layout);
		return frame;
	}

	@Override
	public void onSectionChanged(SectionChangeEvent evt) {
		if(evt.getCurrentSection() == Section.Devices){
			update();
		}
	}

}
