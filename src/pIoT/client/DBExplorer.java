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
import java.util.Date;

import pIoT.client.DataVisualizer.UpdateHandler;
import pIoT.client.events.SectionChangeEvent;
import pIoT.client.events.SectionChangeHandler;
import pIoT.client.events.SectionChangeEvent.Section;
import pIoT.client.services.DBService;
import pIoT.client.services.DBServiceAsync;
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A generic visualiser of {@link DataMessage}, it uses client-side reflection
 * to render all kinds of classes.
 * @author Dario Salvi
 *
 */
public class DBExplorer extends ResizeComposite implements SectionChangeHandler{

	private final DBServiceAsync DB = GWT.create(DBService.class);
	private final SplitLayoutPanel mainPanel = new SplitLayoutPanel();

	//Menu stuff
	final VerticalPanel datamenu = new VerticalPanel();

	//Upper menu stuff
	ListBox devices = new ListBox();
	ListBox itemsN = new ListBox();
	Anchor previous = new Anchor("<previous");
	Label indexes = new Label("");
	Anchor next = new Anchor("next>");
	int totalItems = 0;
	int page = 0;

	//Main area stuff
	VerticalPanel messagesPanel = new VerticalPanel();
	String currentClass = null;

	public DBExplorer() {

		mainPanel.getElement().getStyle().setMargin(10, Unit.PX);

		mainPanel.addWest(datamenu, 100);

		final HorizontalPanel navigationMenu = new HorizontalPanel();
		Label devicesLabel = new Label("Device:");
		devicesLabel.getElement().getStyle().setMarginLeft(5, Unit.PX);
		navigationMenu.add(devicesLabel);

		devices.setName("Device");
		devices.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				page = 0;
				updateContent();
			}
		});
		devices.getElement().getStyle().setMarginLeft(5, Unit.PX);
		devices.setSelectedIndex(0);
		updateDevicesList();
		navigationMenu.add(devices);

		Label showingOf = new Label("showing");
		showingOf.getElement().getStyle().setMarginLeft(10, Unit.PX);
		navigationMenu.add(showingOf);

		itemsN.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				page = 0;
				updateContent();
			}
		});
		itemsN.addItem("1");
		itemsN.addItem("5");
		itemsN.addItem("10");
		itemsN.addItem("20");
		itemsN.addItem("All");
		itemsN.setSelectedIndex(0);
		itemsN.getElement().getStyle().setMarginLeft(5, Unit.PX);
		navigationMenu.add(itemsN);

		Label items = new Label("items");
		items.getElement().getStyle().setMarginLeft(5, Unit.PX);
		navigationMenu.add(items);

		indexes.getElement().getStyle().setMarginLeft(20, Unit.PX);
		navigationMenu.add(indexes);

		previous.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				page--;
				updateContent();
			}
		});
		previous.getElement().getStyle().setMarginLeft(20, Unit.PX);
		previous.setVisible(false);
		navigationMenu.add(previous);

		next.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				page++;
				updateContent();
			}
		});
		next.getElement().getStyle().setMarginLeft(20, Unit.PX);
		next.setVisible(false);
		navigationMenu.add(next);

		mainPanel.addNorth(navigationMenu, 25);

		messagesPanel.getElement().getStyle().setPadding(10, Unit.PX);
		messagesPanel.setSpacing(5);
		ScrollPanel scrollable = new ScrollPanel();
		scrollable.add(messagesPanel);
		mainPanel.add(scrollable);

		updateDataMenu();

		initWidget(mainPanel);
	}

	private void updateDevicesList(){
		devices.clear();
		devices.addItem("All");
		DB.getDevices(new AsyncCallback<ArrayList<Node>>() {
			@Override
			public void onSuccess(ArrayList<Node> result) {
				for(Node dev : result)
					if(dev.getName() != null)
						devices.addItem(dev.getName());
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Can't retrieve the list of devices.\n" + caught.getMessage());
			}
		});
	}

	private void updateContent(){
		messagesPanel.clear();

		if(currentClass == null)
			return;

		messagesPanel.add(new HTML("<h2>...retrieving and parsing data, please wait...</h2>"));

		DB.getClassStoredCount(currentClass, new AsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				totalItems = result;
				
				String devn = devices.getItemText(devices.getSelectedIndex());
				final String deviceName = devn.equalsIgnoreCase("All")? null: devn;

				//Get multiplier
				int multiplier;
				int itemNSel = itemsN.getSelectedIndex();
				String itemNT = itemsN.getItemText(itemNSel);
				if(itemNT.equalsIgnoreCase("All"))
					multiplier = -1;
				else multiplier = Integer.parseInt(itemNT);

				//Calculate indexes
				int startIndex, endIndex;
				if(multiplier <0){
					startIndex = 0;
					endIndex = -1;
				} else {
					startIndex = page * multiplier;
					endIndex = ((page+1) * multiplier) -1;
				}

				//Set indexes text
				String text = "";
				if(multiplier <0)
					text += "all";
				else{
					text += "from " + (startIndex+1) +" to " + ((totalItems < endIndex)? (totalItems) : (endIndex+1)) + " of "+ totalItems;
				}
				indexes.setText(text);

				//set previous and next
				if(multiplier <0){
					previous.setVisible(false);
					next.setVisible(false);
				} else {
					previous.setVisible(startIndex>0);
					next.setVisible(endIndex < totalItems-1);
				}

				DB.getDataMessages(currentClass, deviceName, startIndex, endIndex, new AsyncCallback<ArrayList<DataMessage>>() {

					@Override
					public void onSuccess(ArrayList<DataMessage> result) {
						messagesPanel.clear();

						for(DataMessage mess : result){
							final Date originalTimestamp = new Date(mess.getReceivedTimestamp().getTime());
							String exportlink;
							if(deviceName!= null)
								exportlink = "/pIoTServer/export/devicename="+deviceName+"&dataname="+currentClass;
							else exportlink = "/pIoTServer/export/dataname="+currentClass;
							messagesPanel.add(DataVisualizer.renderObject(mess, true, true, exportlink, "Update", new UpdateHandler() {
								
								@Override
								public void handle(Object o) {
									DB.updateDataMessage(originalTimestamp, (DataMessage) o, new AsyncCallback<Void>() {

										@Override
										public void onSuccess(Void result) {
											Window.alert("Message updated");
										}
										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Cannot update message\n"+caught.getMessage());
											GWT.log("Cannot update message", caught);
										}
									});
								}
							}));
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Cannot retrieve data of class name "+currentClass, caught);
						Window.alert("Cannot retrieve data of class name "+currentClass+".\n" + caught.getMessage());
						messagesPanel.clear();
						messagesPanel.add(new HTML(caught.getMessage()));
					}
				});
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot connect to server.\n"+caught.getMessage());
			}
		});
	}

	private void updateDataMenu(){
		datamenu.clear();

		//Always add All
		Anchor allLabel = new Anchor("All");
		allLabel.getElement().getStyle().setMargin(5, Unit.PX);
		allLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentClass = DataMessage.class.getName();
				page = 0;
				updateContent();
			}
		});
		datamenu.add(allLabel);

		//Add navigation
		DB.getDataMessageClassNames(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onSuccess(ArrayList<String> result) {
				for(final String className : result){
					if(!className.equals(DataMessage.class.getName())){
						int ix = className.lastIndexOf(".");
						String reducedName = className.substring(ix + 1);
						Anchor classNameLabel = new Anchor(reducedName);
						classNameLabel.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								currentClass = className;
								page = 0;
								updateContent();
							}
						});
						datamenu.add(classNameLabel);
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Cannot get stored data names", caught);
				Window.alert("Cannot get stored data names.\n" + caught.getMessage());
			}
		});
	}




	@Override
	public void onSectionChanged(SectionChangeEvent evt) {
		if(evt.getCurrentSection() == Section.Data){
			//Refresh data types and devices list that can have changed
			updateDevicesList();
			updateDataMenu();
		}
	}

}
