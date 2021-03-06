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

import pIoT.client.charts.Plotter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Generic visualiser of objects. Uses reflection for exploring the structure of the
 * objects and represents them in a widget.
 * @author Dario Salvi
 *
 */
public class DataVisualizer {
	
	public static interface UpdateDeleteHandler{
		
		public void update(Object o);
		
		public void delete(Object o);
	}

	/**
	 * Renders a generic object using reflection
	 * @param object the object to be rendered
	 * @param editable if true the widget allows editing the fields
	 * @param allowplot if true a "export" and "plot" link is added to numeric fields,
	 * except source and destination address
	 * @param exportlink the base link for the exporting servlet, this visualiser will
	 * add to it the names of the fields preceded by a point
	 * @param setButtonText the text to be put in the button that saves the edits
	 * @param handler this is used to pass the newly edited object
	 * @return a Widget that represents the object
	 */
	public static Widget renderObject(Object object, 
			boolean editable, boolean allowplot, String exportlink,
			String setButtonText, String deleteButtonText, UpdateDeleteHandler handler){
		return renderObject(object, editable, allowplot, exportlink, object.getClass().getName()+"/", setButtonText, deleteButtonText, handler, new ArrayList<Runnable>());
	}

	private static Widget renderObject(final Object object, 
			boolean editable, boolean allowplot, String exportLink, String plotlink,
			String setButtonText, String deleteButtonText,
			final UpdateDeleteHandler handler, final ArrayList<Runnable> fieldchangers){
		DecoratorPanel decP = new DecoratorPanel();

		FlexTable layout = new FlexTable();
		layout.setCellSpacing(5);

		final Class<?> clazz = object.getClass();

		String className = clazz.getSimpleName();
		layout.setHTML(0, 0, className);
		layout.getFlexCellFormatter().setColSpan(0, 0, 2);
		layout.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		int lastrow = setFieldRows(layout, object, editable, allowplot, exportLink, plotlink, fieldchangers);

		if((editable) && (setButtonText != null) && (handler != null)){
			Button setButton = new Button(setButtonText);
			setButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for(Runnable ch : fieldchangers)
						ch.run();
					handler.update(object);
				}
			});
			layout.setWidget(lastrow, 0, setButton);
		}
		if((editable) && (deleteButtonText != null) && (handler != null)){
			Button deleteButton = new Button(deleteButtonText);
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					handler.delete(object);
				}
			});
			layout.setWidget(lastrow, 1, deleteButton);
		}

		decP.add(layout);
		return decP;
	}

	private static interface BasicTypeModifier{
		public Object getChanger();
		public Widget getWidget();
	}


	private static int setFieldRows(FlexTable layout, final Object object, 
			boolean editable, boolean allowplot, String exportlink, String plotlink,
			final ArrayList<Runnable> fieldchangers){
		int fieldN = 1;

		Class<?> clazz = object.getClass();
		final Reflector refl = PropertyUtils.getReflector(clazz);

		for(final String fieldName : refl.list(object)){
			
			Object fieldValue = refl.get(object, fieldName);
			if(fieldValue == null){ //The field is null
				layout.setHTML(fieldN, 0, fieldName);
				layout.setWidget(fieldN, 1, new HTML("empty"));
			}
			else{ //Field is not null
				Class<?> fieldclazz = fieldValue.getClass();

				if(fieldclazz.isArray()){ //The field is an array
					layout.setHTML(fieldN, 0, fieldName);
					layout.setHTML(fieldN, 1, "cannot parse arrays :(");
				} else if(fieldValue instanceof String) { //The field is a String
					String text = (String) fieldValue;
					boolean edit = editable;
					if(fieldName.equalsIgnoreCase("sourceMessage")) //never edit source message
						edit = false;
					if((text.length() > 20) || (text.indexOf('\n') != -1)){ //long text
						DisclosurePanel dp = new DisclosurePanel(fieldName);
						dp.setAnimationEnabled(true);

						final BasicTypeModifier md = renderBasicType(fieldValue, edit, false, exportlink+"."+fieldName, plotlink+"."+fieldName);
						dp.setContent(md.getWidget());
						fieldchangers.add(new Runnable() {
							@Override
							public void run() {
								Object val = md.getChanger();
								if(val != null)
									refl.set(object, fieldName, val);
							}
						});

						layout.setWidget(fieldN, 0, dp);
						layout.getFlexCellFormatter().setColSpan(fieldN, 0, 2);
					} else{ //short text
						layout.setHTML(fieldN, 0, fieldName);

						final BasicTypeModifier md = renderBasicType(fieldValue, edit, false, exportlink+"."+fieldName, plotlink+"."+fieldName);
						layout.setWidget(fieldN, 1, md.getWidget());
						fieldchangers.add(new Runnable() {
							@Override
							public void run() {
								Object val = md.getChanger();
								if(val != null)
									refl.set(object, fieldName, val);
							}
						});
					}
					fieldN++;
				} else if(isBasic(fieldValue)){ 
					layout.setHTML(fieldN, 0, fieldName);
					boolean plot = allowplot;
					if(fieldName.equalsIgnoreCase("sourceAddress"))
						plot = false;
					final BasicTypeModifier md = renderBasicType(fieldValue, editable, plot, exportlink+"."+fieldName, plotlink+"."+fieldName);
					layout.setWidget(fieldN, 1, md.getWidget());
					fieldchangers.add(new Runnable() {
						@Override
						public void run() {
							Object val = md.getChanger();
							if(val != null)
								refl.set(object, fieldName, val);
						}
					});
					fieldN++;
				}
				 else { //The field is a complex class

					if(fieldValue instanceof List){ //The field is a List 
						//TODO: add new element
						//TODO: remove elements from list

						final List list = (List) fieldValue;
						int len = list.size();
						if(len ==0) {
							layout.setHTML(fieldN, 0, fieldName);
							layout.setWidget(fieldN, 1, new HTML("empty"));
							fieldN++;
						}
						for(int i=0; i<len; i++){
							Object element = list.get(i);
							final int j = i;
							if(isBasic(element)){
								layout.setHTML(fieldN, 0, fieldName+"["+ i + "]");

								final BasicTypeModifier md = renderBasicType(element, editable, allowplot, exportlink+"."+fieldName, plotlink+"."+fieldName);
								layout.setWidget(fieldN, 1, md.getWidget());
								fieldchangers.add(new Runnable() {
									@Override
									public void run() {
										Object val = md.getChanger();
										if(val != null)
											list.set(j, val);
									}
								});

								fieldN++;
							} else {
								DisclosurePanel dp = new DisclosurePanel(fieldName+"["+ i + "]");
								dp.setAnimationEnabled(true);

								final BasicTypeModifier md = renderBasicType(element, editable, allowplot, exportlink+"."+fieldName, plotlink+"."+fieldName);
								dp.setContent(md.getWidget());
								fieldchangers.add(new Runnable() {
									@Override
									public void run() {
										Object val = md.getChanger();
										if(val != null)
											refl.set(object, fieldName, val);
									}
								});

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

						dp.setContent(renderObject(fieldValue, editable, allowplot, exportlink+"."+fieldName, plotlink +"."+fieldName, null, null, null, fieldchangers));

						layout.setWidget(fieldN, 0, dp);
						layout.getFlexCellFormatter().setColSpan(fieldN, 0, 2);
						fieldN++;
					}
				}
			}
		}

		return fieldN;
	}

	private static boolean isBasic(Object o){
		return  o.getClass().isPrimitive() || //boolean, byte, char, short, int, long, float, and double
				//numbers
				(o instanceof Integer) ||
				(o instanceof Long)||
				(o instanceof Short)||
				(o instanceof Float)||
				(o instanceof Double)||
				//Byte
				(o instanceof Byte)||
				//Text
				(o instanceof Character)||
				(o instanceof String)||
				//Date
				(o instanceof Date)||
				//Bool
				(o instanceof Boolean);
	}

	private static BasicTypeModifier renderBasicType(final Object value, boolean editable, boolean allowplot, String exportlink, final String plotlink){

		if(value instanceof String) { //String
			final String text = (String) value;
			if((text.length() > 20) || (text.indexOf('\n') != -1)){ //long text
				final TextArea ta = new TextArea();
				ta.setText(text);
				ta.setReadOnly(!editable);
				ta.setWidth("300px");
				ta.setHeight("300px");
				return new BasicTypeModifier() {
					@Override
					public Widget getWidget() {
						return ta;
					}
					@Override
					public Object getChanger() {
						return ta.getText();
					}
				};
			} else{ //Short text
				final TextBox tb = new TextBox();
				tb.setText(value.toString());
				tb.setReadOnly(!editable);
				return new BasicTypeModifier() {
					@Override
					public Widget getWidget() {
						return tb;
					}
					@Override
					public Object getChanger() {
						return tb.getText();
					}
				};
			}
		}
		else if(value instanceof Byte){ //Byte
			Byte b = (Byte) value;
			final TextBox tb = new TextBox();
			tb.setMaxLength(3);
			tb.setText(b.toString());
			tb.setReadOnly(!editable);
			return new BasicTypeModifier() {
				@Override
				public Widget getWidget() {
					return tb;
				}
				@Override
				public Object getChanger() {
					Byte bb = Byte.parseByte(tb.getText());
					return bb;
				}
			};
		}
		else if(value instanceof Character){ //Char
			Character c = (Character) value;
			final TextBox tb = new TextBox();
			tb.setMaxLength(1);
			tb.setText(c.toString());
			tb.setReadOnly(!editable);
			return new BasicTypeModifier() {
				@Override
				public Widget getWidget() {
					return tb;
				}
				@Override
				public Object getChanger() {
					Character cc = tb.getText().charAt(0);
					return cc;
				}
			};
		}
		else if((value instanceof Integer) || //Number
				(value instanceof Long)||
				(value instanceof Short)||
				(value instanceof Float)||
				(value instanceof Double)){
			final HorizontalPanel hp = new HorizontalPanel();
			final TextBox tb = new TextBox();
			if(value instanceof Float){
				Float d = (Float) value;
				tb.setText(NumberFormat.getFormat("#.##").format(d));
			} else if(value instanceof Double){
				Double d = (Double) value;
				tb.setText(NumberFormat.getFormat("#.##").format(d));
			}
			else{
				tb.setText(value.toString());
			}
			tb.setReadOnly(!editable);
			hp.add(tb);
			if(allowplot){
				Anchor export = new Anchor("export");
				export.setHref(exportlink);
				export.getElement().getStyle().setMarginLeft(5, Unit.PX);
				hp.add(export);
				
				Anchor plot = new Anchor("plot");
				plot.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						int width = (int) (Window.getClientWidth() *0.8F);
						int x = Window.getClientWidth()/2 -width/2;
						int height = (int) (Window.getClientHeight() *0.8F);
						int y = Window.getClientHeight()/2 -height/2;
						Plotter p = new Plotter(plotlink, width, height);
						p.setWidth(width+"px");
						p.setHeight(height+"px");
						p.setPopupPosition(x, y);
						p.setAnimationEnabled(true);
						p.show();
					}
				});
				plot.getElement().getStyle().setMarginLeft(5, Unit.PX);
				hp.add(plot);
			}
			return new BasicTypeModifier() {
				@Override
				public Widget getWidget() {
					return hp;
				}
				@Override
				public Object getChanger() {
					Object val = null;
					if(value instanceof Integer)
						val = Integer.parseInt(tb.getText());
					if(value instanceof Long)
						val = Long.parseLong(tb.getText());
					if(value instanceof Short)
						val = Short.parseShort(tb.getText());
					if(value instanceof Float)
						val = Float.parseFloat(tb.getText());
					if(value instanceof Double)
						val = Double.parseDouble(tb.getText());
					return val;
				}
			};
		}
		else if(value instanceof Date){ //Dates 
			final Date date = (Date) value;
			final Anchor datetext = new Anchor(value.toString());

			if(editable){
				datetext.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						final DateTimePopup stpp = new DateTimePopup(date);
						stpp.addCloseHandler(new CloseHandler<PopupPanel>() {
							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								//Update date text
								datetext.setText(date.toString());
							}
						});

						stpp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
							public void setPosition(int offsetWidth, int offsetHeight) {
								int left = (Window.getClientWidth() - offsetWidth) / 3;
								int top = (Window.getClientHeight() - offsetHeight) / 3;
								stpp.setPopupPosition(left, top);
							}
						});
					}
				});
			}
			return new BasicTypeModifier() {
				@Override
				public Widget getWidget() {
					return datetext;
				}
				@Override
				public Object getChanger() {
					return date;
				}
			};
		} else if(value instanceof Boolean){ //Booleans
			final CheckBox cb = new CheckBox();
			cb.setValue((Boolean) value);
			cb.setEnabled(editable);
			return new BasicTypeModifier() {
				@Override
				public Widget getWidget() {
					return cb;
				}
				@Override
				public Object getChanger() {
					Boolean b = cb.getValue();
					return b;
				}
			};
		} 
		return null;
	}

}
