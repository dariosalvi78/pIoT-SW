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
import java.util.List;

import org.dt.reflector.client.PropertyUtils;
import org.dt.reflector.client.Reflector;

import pIoT.client.events.SectionChangeEvent;
import pIoT.client.events.SectionChangeHandler;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
		itemsN.addItem("5");
		itemsN.addItem("10");
		itemsN.addItem("20");
		itemsN.addItem("50");
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

				String deviceName = devices.getItemText(devices.getSelectedIndex());

				if(deviceName.equalsIgnoreCase("All"))
					deviceName = null;

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
							Widget w = renderObject(mess);
							messagesPanel.add(w);
						}
					}
					@Override
					public void onFailure(Throwable caught) {
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
				Window.alert("Cannot get stored objects names.\n" + caught.getMessage());
			}
		});
	}

	private Widget renderObject(Object message){
		DecoratorPanel decP = new DecoratorPanel();

		FlexTable layout = new FlexTable();
		layout.setCellSpacing(5);

		Class<?> clazz = message.getClass();

		String className = clazz.getSimpleName();
		layout.setHTML(0, 0, className);
		layout.getFlexCellFormatter().setColSpan(0, 0, 2);
		layout.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		setFieldRows(layout, message);


		decP.add(layout);
		return decP;
	}

	private Widget renderBasicType(Object value){
		if(value instanceof String) { //String
			String text = (String) value;
			if((text.length() > 20) || (text.indexOf('\n') != -1)){
				TextArea ta = new TextArea();
				ta.setText(text);
				ta.setReadOnly(true);
				ta.setWidth("300px");
				ta.setHeight("300px");
				return ta;
			} else{
				TextBox tb = new TextBox();
				tb.setText(value.toString());
				tb.setReadOnly(true);
				return tb;
			}
		} else if((value instanceof Byte) || //char
				(value instanceof Character)){
			Label valueLabel = new Label(value.toString());
			return valueLabel;
		}
		else if((value instanceof Integer) || //numbers
				(value instanceof Long)||
				(value instanceof Short)||
				(value instanceof Float)||
				(value instanceof Double)){
			HorizontalPanel hp = new HorizontalPanel();
			Label number = new Label(value.toString());
			hp.add(number);
			Anchor plot = new Anchor("plot");
			plot.getElement().getStyle().setMarginLeft(5, Unit.PX);
			hp.add(plot);
			return hp;
		} else if(value instanceof Date){ //Dates
			Label valueLabel = new Label(value.toString());
			return valueLabel;
		} else if(value instanceof Boolean){ //Booleans
			CheckBox cb = new CheckBox();
			cb.setEnabled(false);
			cb.setValue((Boolean) value);
			return cb;
		} 
		return null;
	}

	private void setFieldRows(FlexTable layout, Object message){
		int fieldN = 1;

		Class<?> clazz = message.getClass();
		Reflector refl = PropertyUtils.getReflector(clazz);

		for(String fieldName : refl.list(message)){
			Object fieldValue = refl.get(message, fieldName);
			if(fieldValue == null){ //The field is null
				layout.setHTML(fieldN, 0, fieldName);
				layout.setWidget(fieldN, 1, new HTML("empty"));
			}
			else{ //Field is not null
				Class<?> fieldclazz = fieldValue.getClass();

				if(fieldclazz.isArray()){ //The field is an array
					layout.setHTML(fieldN, 0, fieldName);
					layout.setHTML(fieldN, 0, "cannot parse arrays :(");
				}
				else if(fieldclazz.isPrimitive() || //boolean, byte, char, short, int, long, float, and double
						(fieldValue instanceof Integer) || //numbers
						(fieldValue instanceof Long)||
						(fieldValue instanceof Short)||
						(fieldValue instanceof Float)||
						(fieldValue instanceof Double)||
						(fieldValue instanceof Byte)||
						(fieldValue instanceof Character)||
						(fieldValue instanceof Date)|| //Date
						(fieldValue instanceof Boolean) //Booleand
						){ 
					layout.setHTML(fieldN, 0, fieldName);
					layout.setWidget(fieldN, 1, renderBasicType(fieldValue));
					fieldN++;
				}
				else if(fieldValue instanceof String) { //The field a String
					String text = (String) fieldValue;
					if((text.length() > 20) || (text.indexOf('\n') != -1)){
						DisclosurePanel dp = new DisclosurePanel(fieldName);
						dp.setAnimationEnabled(true);
						dp.setContent(renderBasicType(fieldValue));
						layout.setWidget(fieldN, 0, dp);
						layout.getFlexCellFormatter().setColSpan(fieldN, 0, 2);
					} else{
						layout.setHTML(fieldN, 0, fieldName);
						layout.setWidget(fieldN, 1, renderBasicType(fieldValue));
					}
					fieldN++;
				} else { //The field is a complex class

					if(fieldValue instanceof List){ //The field is a List (do like an array)
						List<?> list = (List<?>) fieldValue;
						int len = list.size();
						if(len ==0) {
							layout.setHTML(fieldN, 0, fieldName);
							layout.setWidget(fieldN, 1, new HTML("empty"));
							fieldN++;
						}
						for(int i=0; i<len; i++){
							Object element = list.get(i);
							Class<?> elementclazz = element.getClass();
							if(elementclazz.isPrimitive() ||
									(element instanceof String) ||
									(element instanceof Date)){
								layout.setHTML(fieldN, 0, fieldName+"["+ i + "]");
								layout.setWidget(fieldN, 1, renderBasicType(element));
								fieldN++;
							} else {
								DisclosurePanel dp = new DisclosurePanel(fieldName+"["+ i + "]");
								dp.setAnimationEnabled(true);
								dp.setContent(renderObject(element));
								layout.setWidget(fieldN, 0, dp);
								layout.getFlexCellFormatter().setColSpan(fieldN, 0, 2);
								fieldN++;
							}
						} 
					}
					//TODO: render collections, iterables, maps?
					else { //Generic class rendering
						DisclosurePanel dp = new DisclosurePanel(fieldName);
						dp.setAnimationEnabled(true);
						dp.setContent(renderObject(fieldValue));
						layout.setWidget(fieldN, 0, dp);
						layout.getFlexCellFormatter().setColSpan(fieldN, 0, 2);
						fieldN++;
					}
				}
			}
		}
	}


	@Override
	public void onSectionChanged(SectionChangeEvent evt) {
		//Refresh data types and devices list that can have changed
		updateDevicesList();
		updateDataMenu();
	}

}
