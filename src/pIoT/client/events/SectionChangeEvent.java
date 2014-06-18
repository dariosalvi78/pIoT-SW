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
 * This package contains custom events used for updating parts of the GUI.
 */
package pIoT.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that tells if a main section of the home page has been selected.
 * @author Dario Salvi
 *
 */
public class SectionChangeEvent extends GwtEvent<SectionChangeHandler> {
	
	public enum Section{
		DashBoard, Devices, Data, Actions, Rules, Serial;
	};
	
	public static com.google.gwt.event.shared.GwtEvent.Type<SectionChangeHandler> type = new Type<SectionChangeHandler>();
	
	private Section currentSection;
	
	public SectionChangeEvent(Section section){
		currentSection = section;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SectionChangeHandler> getAssociatedType() {
		return type;
	}

	@Override
	protected void dispatch(SectionChangeHandler handler) {
		handler.onSectionChanged(this);
	}

	public Section getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(Section currentSection) {
		this.currentSection = currentSection;
	}
	

}
