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

import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A generic confirmation dialog.
 * @author Dario Salvi
 *
 */
public class ConfirmationDialog extends DialogBox {
	
	public interface ConfirmationHandler{
		public void confirm(boolean confirmed);
	}

	/**
	 * 
	 */
	public ConfirmationDialog(String message, final ConfirmationHandler handler) {
		super(false, true);
		setTitle("Confirmation");
		setText("Confirmation");
		setAnimationEnabled(true);
		setGlassEnabled(true);

		VerticalPanel vpanel = new VerticalPanel();
		Label messagelbl = new Label(message);
		vpanel.add(messagelbl);
		HorizontalPanel hpanel = new HorizontalPanel();
		Button OK = new Button("Ok");
		OK.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				handler.confirm(true);
			}
		});

		hpanel.add(OK);
		Button Cancel = new Button("Cancel");
		Cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				handler.confirm(false);
			}
		});
		
		hpanel.add(Cancel);
		vpanel.add(hpanel);
		setWidget(vpanel);
	}


}
