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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * A pop up panel for editing date and time.
 * @author Dario Salvi
 *
 */
public class DateTimePopup extends PopupPanel {


	public DateTimePopup(final Date date) {
		super(true);

		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hp = new HorizontalPanel();

		Label hourl = new Label("hh:");
		hp.add(hourl);
		final TextBox hourtb = new TextBox();
		hourtb.setMaxLength(2);
		hourtb.setVisibleLength(2);
		hourtb.setText(""+date.getHours());
		hp.add(hourtb);

		Label minsl = new Label(" mm:");
		hp.add(minsl);
		final TextBox minstb = new TextBox();
		minstb.setMaxLength(2);
		minstb.setVisibleLength(2);
		minstb.setText(""+date.getMinutes());
		hp.add(minstb);

		Label secsl = new Label(" ss:");
		hp.add(secsl);
		final TextBox secstb = new TextBox();
		secstb.setMaxLength(2);
		secstb.setVisibleLength(2);
		secstb.setText(""+date.getSeconds());
		hp.add(secstb);

		vp.add(hp);
		
		final DatePicker datepicker = new DatePicker();
		datepicker.setValue(date);
		vp.add(datepicker);

		Button setbutt = new Button("Set");
		setbutt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				date.setTime(datepicker.getValue().getTime());
				int hours = Integer.parseInt(hourtb.getText());
				date.setHours(hours);
				int minutes = Integer.parseInt(minstb.getText());
				date.setMinutes(minutes);
				int secs = Integer.parseInt(secstb.getText());
				date.setSeconds(secs);
				hide();
			}
		});
		vp.add(setbutt);
		setWidget(vp);
	}

}
