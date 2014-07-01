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
		
		
		initWidget(panel);
	}

	@Override
	public void onSectionChanged(SectionChangeEvent evt) {
		if(evt.getCurrentSection() == Section.DashBoard){
			stats.update();
		}
			
	}
	
}
