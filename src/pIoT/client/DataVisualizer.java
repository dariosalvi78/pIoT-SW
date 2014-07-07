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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author Dario
 *
 */
public class DataVisualizer {

	private static interface FieldChanger{
		public void change();
	}

	public static Widget renderObject(Object obj, boolean editable, String setButtonText){
		DecoratorPanel decP = new DecoratorPanel();

		FlexTable layout = new FlexTable();
		layout.setCellSpacing(5);

		Class<?> clazz = obj.getClass();

		String className = clazz.getSimpleName();
		layout.setHTML(0, 0, className);
		layout.getFlexCellFormatter().setColSpan(0, 0, 2);
		layout.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		final ArrayList<FieldChanger> changers = new ArrayList<DataVisualizer.FieldChanger>();

		int lastrow = setFieldRows(layout, obj, editable, changers);

		if((editable) && (setButtonText != null)){
			Button setButton = new Button(setButtonText);
			setButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for(FieldChanger ch : changers)
						ch.change();
				}
			});
			layout.setWidget(lastrow, 0, setButton);
			layout.getFlexCellFormatter().setColSpan(0, 0, 2);
			layout.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		decP.add(layout);
		return decP;
	}

	private static Widget renderBasicType(final Object container, final Reflector refl, final String fieldName, boolean editable, ArrayList<FieldChanger> changers){
		Object value = refl.get(container, fieldName);

		if(value instanceof String) { //String
			final String text = (String) value;
			if((text.length() > 20) || (text.indexOf('\n') != -1)){ //long text
				final TextArea ta = new TextArea();
				ta.setText(text);
				if(editable){
					ta.setReadOnly(false);
					changers.add(new FieldChanger() {
						@Override
						public void change() {
							refl.set(container, fieldName, ta.getText());
						}
					});
				} else {
					ta.setReadOnly(true);
				}
				ta.setWidth("300px");
				ta.setHeight("300px");
				return ta;
			} else{ //Short text
				final TextBox tb = new TextBox();
				tb.setText(value.toString());
				if(editable){
					tb.setReadOnly(false);
					changers.add(new FieldChanger() {
						@Override
						public void change() {
							refl.set(container, fieldName, tb.getText());
						}
					});
				} else {
					tb.setReadOnly(true);
				}
				return tb;
			}
		}
		else if(value instanceof Byte){
			Byte b = (Byte) value;
			final TextBox tb = new TextBox();
			tb.setMaxLength(3);
			tb.setText(b.toString());
			if(editable){
				tb.setReadOnly(false);
				changers.add(new FieldChanger() {
					@Override
					public void change() {
						Byte bb = Byte.parseByte(tb.getText());
						refl.set(container, fieldName, bb);
					}
				});
			} else {
				tb.setReadOnly(true);
			}
			return tb;
		}
		else if(value instanceof Character){
			Character c = (Character) value;
			final TextBox tb = new TextBox();
			tb.setMaxLength(1);
			tb.setText(c.toString());
			if(editable){
				tb.setReadOnly(false);
				changers.add(new FieldChanger() {
					@Override
					public void change() {
						Character cc = tb.getText().charAt(0);
						refl.set(container, fieldName, cc);
					}
				});
			} else {
				tb.setReadOnly(true);
			}
			return tb;
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
		}
		else if(value instanceof Date){ //Dates 
			final Date date = (Date) value;
			final Label valueLabel = new Label(value.toString());

			if(editable){
				valueLabel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						final PopupPanel datepanel = new PopupPanel(true);
						HorizontalPanel hp = new HorizontalPanel();

						Label hourl = new Label("H:");
						hp.add(hourl);
						final TextBox hourtb = new TextBox();
						hourtb.setMaxLength(2);
						hourtb.setText(""+date.getHours());
						hp.add(hourtb);

						Label minsl = new Label("M:");
						hp.add(minsl);
						final TextBox minstb = new TextBox();
						minstb.setMaxLength(2);
						minstb.setText(""+date.getMinutes());
						hp.add(minstb);

						Label secsl = new Label("S:");
						hp.add(secsl);
						final TextBox secstb = new TextBox();
						secstb.setMaxLength(2);
						secstb.setText(""+date.getSeconds());
						hp.add(secstb);

						final DatePicker datepicker = new DatePicker();
						datepicker.setValue(date);
						hp.add(datepicker);
						datepanel.setWidget(hp);

						Button setbutt = new Button("Set");
						setbutt.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								date.setTime(datepicker.getValue().getTime());
								int hours = Integer.parseInt(hourtb.getText());
								date.setHours(hours);
								int minutes = Integer.parseInt(minstb.getText());
								date.setMinutes(minutes);
								int secs = Integer.parseInt(secstb.getText());
								date.setSeconds(secs);
								valueLabel.setText(date.toString());
								datepanel.setVisible(false);
							}
						});
					}
				});
				changers.add(new FieldChanger() {
					@Override
					public void change() {
						refl.set(container, fieldName, date);
					}
				});
			}
			return valueLabel;
		} else if(value instanceof Boolean){ //Booleans
			final CheckBox cb = new CheckBox();
			cb.setValue((Boolean) value);
			if(editable){
				cb.setEnabled(true);
				changers.add(new FieldChanger() {
					@Override
					public void change() {
						Boolean b = cb.getValue();
						refl.set(container, fieldName, b);
					}
				});
			} else {
				cb.setEnabled(false);
			}
			return cb;
		} 
		return null;
	}

	private static int setFieldRows(FlexTable layout, Object message, boolean editable, ArrayList<FieldChanger> changers){
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
					layout.setWidget(fieldN, 1, renderBasicType(message, refl, fieldName, editable, changers));
					fieldN++;
				}
				else if(fieldValue instanceof String) { //The field is a String
					String text = (String) fieldValue;
					if((text.length() > 20) || (text.indexOf('\n') != -1)){
						DisclosurePanel dp = new DisclosurePanel(fieldName);
						dp.setAnimationEnabled(true);
						dp.setContent(renderBasicType(message, refl, fieldName, editable, changers));
						layout.setWidget(fieldN, 0, dp);
						layout.getFlexCellFormatter().setColSpan(fieldN, 0, 2);
					} else{
						layout.setHTML(fieldN, 0, fieldName);
						layout.setWidget(fieldN, 1, renderBasicType(message, refl, fieldName, editable, changers));
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
								layout.setWidget(fieldN, 1, renderBasicType(message, refl, fieldName, editable, changers));
								fieldN++;
							} else {
								DisclosurePanel dp = new DisclosurePanel(fieldName+"["+ i + "]");
								dp.setAnimationEnabled(true);
								dp.setContent(renderBasicType(message, refl, fieldName, editable, changers));
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
						dp.setContent(renderObject(message, editable, null));
						layout.setWidget(fieldN, 0, dp);
						layout.getFlexCellFormatter().setColSpan(fieldN, 0, 2);
						fieldN++;
					}
				}
			}
		}

		return fieldN;
	}

}
