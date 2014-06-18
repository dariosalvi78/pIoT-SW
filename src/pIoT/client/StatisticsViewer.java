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

import pIoT.client.services.DBService;
import pIoT.client.services.DBServiceAsync;
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Some simple statistics.
 * @author Dario Salvi
 *
 */
public class StatisticsViewer extends Composite{

	private final DBServiceAsync DB = GWT.create(DBService.class);
	private Label messagesN;
	private Label devicesN;
	
	public StatisticsViewer() {
		DecoratorPanel frame = new DecoratorPanel();
		
		VerticalPanel vp = new VerticalPanel();
		vp.getElement().getStyle().setMargin(5, Unit.PX);
		HorizontalPanel hp1= new HorizontalPanel();
		hp1.add(new HTML("received "));
		messagesN = new Label("?");
		messagesN.getElement().getStyle().setFontSize(2, Unit.EM);
		messagesN.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		messagesN.getElement().getStyle().setMargin(5, Unit.PX);
		hp1.add(messagesN);
		hp1.add(new HTML(" messages")); 
		vp.add(hp1);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.add(new HTML("from "));
		devicesN = new Label("?");
		devicesN.getElement().getStyle().setFontSize(2, Unit.EM);
		devicesN.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		devicesN.getElement().getStyle().setMargin(5, Unit.PX);
		hp2.add(devicesN);
		hp2.add(new HTML(" devices"));
		vp.add(hp2);
		frame.add(vp);
		
		update();
		
		initWidget(frame);
	}
	
	public void update(){
		DB.getClassStoredCount(DataMessage.class.getName(), new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				messagesN.setText(" "+result+" ");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot retreive messages number.\n"+caught.getMessage());
			}
		});
		DB.getDevices(new AsyncCallback<ArrayList<Node>>() {
			
			@Override
			public void onSuccess(ArrayList<Node> result) {
				devicesN.setText(" "+result.size()+" ");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot retreive devices number.\n"+caught.getMessage());
			}
		});
	}

}
