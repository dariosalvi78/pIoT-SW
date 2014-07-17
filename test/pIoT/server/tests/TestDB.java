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

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class TestDB {
	
	private static ObjectContainer db;
	private final String dbFileName = "testDB";

	@Before
	public void setUp() throws Exception {
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), dbFileName);
	}

	@After
	public void tearDown() throws Exception {
		db.close();
		new File(dbFileName).delete();
	}

	@Test
	public void simpleTest() {
		Node dev1 = new Node(1, "nord1", "kitchen");
		Node dev2 = new Node(2, "nord2", "bathroom");
		db.store(dev1);
		db.store(dev2);
		db.commit();
		
		List<Node> devices =  db.query(Node.class);
		assertEquals(2, devices.size());
		assertTrue(devices.get(0).getAddress() ==1 ||
				devices.get(0).getAddress() == 2);
		assertTrue(devices.get(1).getAddress() == 1 ||
				devices.get(1).getAddress() == 2);
		
		Node protodev1 = new Node(1, null, null);
		devices = db.queryByExample(protodev1);
		assertEquals(1, devices.size());
		assertEquals("nord1", devices.get(0).getName());
		
		dev1.setLocation("livingroom");
		db.store(dev1);
		db.commit();
		devices = db.queryByExample(protodev1);
		assertEquals("livingroom", devices.get(0).getLocation());
		
		db.delete(dev1);
		db.commit();
		devices =  db.query(Node.class);
		assertEquals(1, devices.size());
		assertEquals("nord2", devices.get(0).getName());
	}
	
	@Test
	public void anotherTest() throws ClassNotFoundException{
		
		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		db.store(dev1);
		for(int i=0; i<10; i++){
			Date msgDate = new Date(now.getTime() + i*100);
			DataMessage mess = new DataMessage(msgDate, "message"+i, 1);
			db.store(mess);
		}
		
		Query query = db.query();
		query.constrain(Node.class);
		query.descend("name").constrain("dev1");

		ObjectSet<Node> devicesset = query.execute();
		assertEquals(1, devicesset.size());
		Node device = devicesset.next();
		int devAddress = device.getAddress();
		assertEquals(1, devAddress);
		
		Class<?> clazz = Class.forName(DataMessage.class.getName());
		
		query = db.query();
		query.constrain(clazz);
		query.descend("receivedTimestamp").orderDescending();
		query.descend("sourceAddress").constrain(devAddress);
		
		ObjectSet<DataMessage> messagesset = query.execute();
		
		assertEquals(10, messagesset.size());
		
	}

}
