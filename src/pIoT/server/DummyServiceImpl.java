/**
 * 
 */
package pIoT.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pIoT.client.services.DummyService;
import pIoT.shared.messages.LightMessage;

/**
 * A Dummy service used to test and fix strange GWT behaviours
 * @author Dario Salvi
 *
 */
public class DummyServiceImpl extends RemoteServiceServlet implements DummyService {

	public DummyServiceImpl() {
	}

	@Override
	public void stupidGWT(LightMessage mess) {
	}

}
