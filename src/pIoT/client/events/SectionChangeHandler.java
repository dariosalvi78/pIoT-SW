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

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Dario Salvi
 *
 */
public interface SectionChangeHandler extends EventHandler {
	
	void onSectionChanged(SectionChangeEvent evt);

}
