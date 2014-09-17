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

import pIoT.client.DataVisualizer.UpdateDeleteHandler;
import pIoT.client.events.SectionChangeEvent;
import pIoT.client.events.SectionChangeHandler;
import pIoT.client.services.ActionsService;
import pIoT.client.services.ActionsServiceAsync;
import pIoT.shared.messages.ActionMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Dario Salvi
 *
 */
public class ActionEditor extends ResizeComposite implements SectionChangeHandler {

	private final ActionsServiceAsync actions = GWT.create(ActionsService.class);
	
	//Menu stuff
	ArrayList<ActionMessage> actionMessages;
	final VerticalPanel datamenu = new VerticalPanel();

	//Main area stuff
	VerticalPanel messagesPanel = new VerticalPanel();
	ActionMessage currentMessage = null;

	/**
	 * 
	 */
	public ActionEditor() {
		final SplitLayoutPanel mainPanel = new SplitLayoutPanel();
		mainPanel.getElement().getStyle().setMargin(10, Unit.PX);

		mainPanel.addWest(datamenu, 100);

		messagesPanel.getElement().getStyle().setPadding(10, Unit.PX);
		messagesPanel.setSpacing(5);
		ScrollPanel scrollable = new ScrollPanel();
		scrollable.add(messagesPanel);
		mainPanel.add(scrollable);

		actions.getActionMessageExamples(new AsyncCallback<ArrayList<ActionMessage>>() {

			@Override
			public void onSuccess(ArrayList<ActionMessage> result) {
				datamenu.clear();
				actionMessages = result;
				for(final ActionMessage mess : result){
					mess.getClass().getSimpleName();
					Anchor classNameLabel = new Anchor(mess.getClass().getSimpleName());
					classNameLabel.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							currentMessage = mess;
							updateContent();
						}
					});
					datamenu.add(classNameLabel);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot get example messages\n "+caught.getMessage());
			}
		});

		initWidget(mainPanel);
	}

	private void updateContent(){
		messagesPanel.clear();

		if(currentMessage == null)
			return;

		messagesPanel.add(DataVisualizer.renderObject(currentMessage, true, false, null, "Send", null, new UpdateDeleteHandler() {
			
			@Override
			public void update(Object o) {
				
				//Send the message
				actions.sendMessage(currentMessage, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						Window.alert("Message sent");
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot send message\n"+caught.getMessage());
					}
				});
			}

			@Override
			public void delete(Object o) {
			}
		}));
	}


	@Override
	public void onSectionChanged(SectionChangeEvent evt) {

	}

}
