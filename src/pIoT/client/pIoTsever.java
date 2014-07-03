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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class pIoTsever implements EntryPoint, HasHandlers {

	private HandlerManager handlerManager;
	
	public pIoTsever(){
		handlerManager = new HandlerManager(this);
	}
	
	public void onModuleLoad() {
		
		// Create a tab panel with the main menu of the server
		final TabLayoutPanel tbp = new TabLayoutPanel(30, Unit.PX);
		tbp.setAnimationDuration(1000);
		//tbp.getElement().getStyle().setMarginBottom(0, Unit.PX);
		tbp.setWidth("100%");
		tbp.setHeight((Window.getClientHeight()-20) + "px");
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				int height = event.getHeight()-20;
				tbp.setHeight(height + "px");
			}
		});

		DashBoard home = new DashBoard();
		addSectionChangeHandler(home);
		tbp.add(home, "DashBoard");
		
		DeviceViewer devsv = new DeviceViewer();
		addSectionChangeHandler(devsv);
		tbp.add(devsv, "Devices");
		
		DBExplorer refl = new DBExplorer();
		addSectionChangeHandler(refl);
		tbp.add(refl, "Data");
		
		tbp.add(new HTML("TBD"), "Actions");
		
		tbp.add(new HTML("TBD"), "Rules");
		
		final SerialMonitor mon = new SerialMonitor();
		addSectionChangeHandler(mon);
		tbp.add(mon, "Serial");
		
		tbp.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int sectionN = event.getSelectedItem();
				if(sectionN == 0)
					fireEvent(new SectionChangeEvent(Section.DashBoard));
				else if(sectionN == 1)
					fireEvent(new SectionChangeEvent(Section.Devices));
				else if(sectionN == 2)
					fireEvent(new SectionChangeEvent(Section.Data));
				else if(sectionN == 3)
					fireEvent(new SectionChangeEvent(Section.Actions));
				else if(sectionN == 4)
					fireEvent(new SectionChangeEvent(Section.Rules));
				else if(sectionN == 5)
					fireEvent(new SectionChangeEvent(Section.Serial));
			}
		});

		// Add the tab panel to the root panel.
		RootLayoutPanel.get().add(tbp);
	}
	
	@Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }
	
	public HandlerRegistration addSectionChangeHandler(
            SectionChangeHandler handler) {
        return handlerManager.addHandler(SectionChangeEvent.type, handler);
    }
}
