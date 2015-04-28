/**
 * 
 */
package pIoT.client.charts;

import java.util.ArrayList;
import java.util.logging.Logger;

import pIoT.shared.ChartConfiguration;
import pIoT.shared.ChartConfiguration.ChartType;
import pIoT.shared.DataSetConfiguration;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A generic configurator for charts
 * @author Dario Salvi
 *
 */
public class Plotter  extends PopupPanel {
	private Logger logger = Logger.getLogger(Plotter.class.getName());

	private TextBox titleBox = new TextBox();
	private ListBox typeBox = new ListBox();
	private int width;
	private int heigth;
	ChartConfiguration config = new ChartConfiguration();
	
	/**
	 * 
	 */
	public Plotter(String plotlink, int width, int heigth) {
		super(true);
		this.width = width;
		this.heigth = heigth;
		String classname = plotlink.split("/.")[0];
		String props = plotlink.split("/.")[1];
		DataSetConfiguration ds = new DataSetConfiguration();
		ds.setClassName(classname);
		ds.setPropertyName(props);
		ArrayList<DataSetConfiguration> dss= new ArrayList<>();
		dss.add(ds);
		config.setDatasets(dss);
		setWidget(getContent());
	}
	

	
	Widget getContent(){
		logger.finer("Plotting Lines");
		final VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel header = new HorizontalPanel();
		header.add(new Label("Title"));
		header.add(titleBox);
		titleBox.setText("");
		
		header.add(new Label("Type"));
		
		for(ChartConfiguration.ChartType t : ChartConfiguration.ChartType.values())
			typeBox.addItem(t.name());
		typeBox.getElement().getStyle().setMarginLeft(20, Unit.PX);
		header.add(typeBox);
		typeBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				vp.remove(1);
				Widget plot = null;
				String type = typeBox.getItemText(typeBox.getSelectedIndex());
				if(type.equals(ChartType.LINES.name())){
					plot = new LinesChart(width-30, heigth-30, config);
				}
				
				vp.add(plot);
			}
		});
		
		Button addToDash = new Button("Add to dashboard");
		addToDash.getElement().getStyle().setMarginLeft(20, Unit.PX);
		//TODO: implement the onclick
		header.add(addToDash);
		vp.add(header);
		
		Widget plot = null;
		String type = typeBox.getItemText(typeBox.getSelectedIndex());
		if(type.equals(ChartType.LINES.name())){
			plot = new LinesChart(width-10, heigth-10, config);
			plot.setWidth(width+"px");
			plot.setHeight(heigth+"px");
		}
		
		vp.add(plot);
		return vp;
	}
	
}
