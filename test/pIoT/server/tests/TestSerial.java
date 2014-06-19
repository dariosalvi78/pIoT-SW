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
package pIoT.server.tests;

import static org.junit.Assert.*;
import jssc.SerialPortList;

import org.junit.Test;

public class TestSerial {

	@Test
	public void testSerialList() {
		String[] ports = SerialPortList.getPortNames();
		for(String port : ports)
			System.out.println(port);
		assertNotNull(ports);
		assertFalse(ports.length == 0);
	}

}
