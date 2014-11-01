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



import java.util.Date;

import pIoT.client.charts.LinesChart;
import pIoT.client.events.SectionChangeEvent;
import pIoT.client.events.SectionChangeHandler;
import pIoT.client.events.SectionChangeEvent.Section;
import pIoT.shared.ChartConfiguration;
import pIoT.shared.DataSetConfiguration;
import pIoT.shared.ChartConfiguration.ChartType;
import pIoT.shared.messages.examples.Hello;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * DashBoard, a place where stats and graphs are put.
 * @author Dario Salvi
 *
 */
public class DashBoard extends Composite implements SectionChangeHandler{

	private final StatisticsViewer stats;
	
	public DashBoard(){
		FlowPanel panel = new FlowPanel();
		
		NotificationsViewer notifications = new NotificationsViewer();
		notifications.getElement().getStyle().setMargin(5, Unit.PX);
		notifications.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		panel.add(notifications);
		
		
		stats = new StatisticsViewer();
		stats.getElement().getStyle().setMargin(5, Unit.PX);
		stats.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		panel.add(stats);		

		ChartConfiguration cf = new ChartConfiguration();
		cf.setStartDate(new Date(2014-1900, 10-1, 14));
		cf.setEndDate(new Date(2014-1900, 10-1, 15));
		cf.setType(ChartType.LINES);
		cf.setValuesName("Temp (degrees)");
		DataSetConfiguration dsc = new DataSetConfiguration();
		dsc.setClassName(Hello.class.getName());
		dsc.setDeviceName("Gardenino");
		dsc.setPropertyName("temperature");
		cf.getDatasets().add(dsc);
		
		LinesChart chart = new LinesChart(500, 330, cf);
		chart.getElement().getStyle().setMargin(5, Unit.PX);
		chart.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		chart.setWidth("500px");
		chart.setHeight("330px");
		panel.add(chart);
		
		initWidget(panel);
	}

	@Override
	public void onSectionChanged(SectionChangeEvent evt) {
		if(evt.getCurrentSection() == Section.DashBoard){
			stats.update();
		}
			
	}
	
}
