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
 * Package containing some automatic tests.
 */
package pIoT.tests;

import com.google.gwt.junit.client.GWTTestCase;

public class TestReflection extends GWTTestCase {

	public TestReflection() {
	}

	@Override
	public String getModuleName() {
		return "bochovj.nordikino.server.NordikinoServer";
	}
	
	public void testAlwaysPass(){
		assertTrue(true);
	}
}
