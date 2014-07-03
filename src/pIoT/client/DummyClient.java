/**
 * 
 */
package pIoT.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pIoT.client.services.DummyService;
import pIoT.client.services.DummyServiceAsync;
import pIoT.shared.messages.LightMessage;

/**
 * A client that does nothing, but is necessary for
 * calling the {@link DummyService} that is necessary for 
 * fixing some strange GWT behaciours.
 * Unfortunately.
 * @author Dario Salvi
 *
 */
public class DummyClient {
	
	private final DummyServiceAsync serv = GWT.create(DummyService.class);

	/**
	 * 
	 */
	public DummyClient() {
		LightMessage mess = new LightMessage(new Date(), "stupid GWT", 0, 0);
		serv.stupidGWT(mess, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

}
