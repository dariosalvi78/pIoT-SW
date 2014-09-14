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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

import pIoT.client.tests.ExtendedData;
import pIoT.client.tests.ExtendedDataMessage;
import pIoT.client.tests.ExtendedDataMessage.NestedData;
import pIoT.server.DBServiceImpl;
import pIoT.shared.DataBaseException;
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.notifications.NewDeviceNotification;

public class TestDBService {

	DBServiceImpl db;

	@Before
	public void setUp() throws Exception {
		DBServiceImpl.dbFileName = "DBTEST";
		db = new DBServiceImpl();
	}

	@After
	public void tearDown() throws Exception {
		db.destroy();
		DBServiceImpl.deleteDB();
	}

	@Test
	public void testGetAllDataMessages() throws Exception {
		//store 10 messages
		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		DBServiceImpl.store(dev1);
		for(int i=0; i<10; i++){
			Date msgDate = new Date(now.getTime() + i*100);
			DataMessage mess = new DataMessage(msgDate, "message"+i, 1);
			DBServiceImpl.store(mess);
		}
		
		//retrieve them
		ArrayList<DataMessage> messages = db.getDataMessages(DataMessage.class.getName(), "dev1", -1, -1);
		assertEquals(10, messages.size());
		//check they're ordered in descending way
		for(int i=0; i<10; i++){
			assertEquals("message"+(9-i), messages.get(i).getSourceMessage());
		}

		Node dev2 = new Node(2, "anotherdev", "out");
		DBServiceImpl.store(dev2);
		DataMessage mess = new DataMessage(now, "message1", 2);
		DBServiceImpl.store(mess);

		messages = db.getDataMessages(DataMessage.class.getName(), "anotherdev", -1, -1);
		assertEquals(1, messages.size());
	}

	@Test
	public void testLimits() throws Exception{
		//store 100 messages
		Date now = Calendar.getInstance().getTime();
		Node dev = new Node(1, "dev", "home");
		DBServiceImpl.getDB().store(dev);
		for(int i=0; i<100; i++){
			Date msgDate = new Date(now.getTime() + i*100);
			DataMessage mess = new DataMessage(msgDate, "message"+i, 1);
			DBServiceImpl.getDB().store(mess);
		}

		//retrieve the first 10
		ArrayList<DataMessage> messages = db.getDataMessages(DataMessage.class.getName(), "dev", 0, 9);
		//check them
		assertEquals(10, messages.size());
		for(int i=0; i<10; i++){
			assertEquals("message"+(99-i), messages.get(i).getSourceMessage());
		}
		//same result, different query
		messages = db.getDataMessages(DataMessage.class.getName(), "dev", -1, 9);
		//check them
		assertEquals(10, messages.size());
		for(int i=0; i<10; i++){
			assertEquals("message"+(99-i), messages.get(i).getSourceMessage());
		}
		//retrieve the last 10
		messages = db.getDataMessages(DataMessage.class.getName(), "dev", 90, 99);
		//check them
		assertEquals(10, messages.size());
		for(int i=0; i<10; i++){
			assertEquals("message"+(9-i), messages.get(i).getSourceMessage());
		}
		//same result, but another query
		messages = db.getDataMessages(DataMessage.class.getName(), "dev", 90, -1);
		//check them
		assertEquals(10, messages.size());
		for(int i=0; i<10; i++){
			assertEquals("message"+(9-i), messages.get(i).getSourceMessage());
		}
	}

	@Test
	public void testgetClassStoredCount() throws IllegalArgumentException, DataBaseException{
		Date now = Calendar.getInstance().getTime();
		Node dev = new Node(1, "dev", "home");
		DBServiceImpl.getDB().store(dev);
		
		DataMessage mesg1 = new DataMessage(now, "message1", 1);
		DBServiceImpl.getDB().store(mesg1);
		
		DataMessage mesg2 = new DataMessage(now, "message2", 1);
		DBServiceImpl.getDB().store(mesg2);
		
		ExtendedDataMessage mesg3 = new ExtendedDataMessage(now, "message3", 1, "extended message", null, null);
		DBServiceImpl.getDB().store(mesg3);
		
		int dms = db.getClassStoredCount(DataMessage.class.getName(), null);
		assertEquals(3, dms);
		dms = db.getClassStoredCount(DataMessage.class.getName(), "dev");
		assertEquals(3, dms);
		int edms = db.getClassStoredCount(ExtendedDataMessage.class.getName(), null);
		assertEquals(1, edms);
	}
	
	@Test
	public void testgetDataMessageClassNames(){
		Date now = Calendar.getInstance().getTime();
		Node dev = new Node(1, "dev", "home");
		DBServiceImpl.getDB().store(dev);
		DataMessage mesg1 = new DataMessage(now, "message1", 1);
		DBServiceImpl.getDB().store(mesg1);
		DataMessage mesg2 = new DataMessage(now, "message2", 1);
		DBServiceImpl.getDB().store(mesg2);
		
		ExtendedDataMessage mesg3 = new ExtendedDataMessage(now, "message3", 1, "extended message", null, null);
		DBServiceImpl.getDB().store(mesg3);
		
		List<String> datanames = db.getDataMessageClassNames();
		assertEquals(2, datanames.size());
		assertTrue(datanames.get(0).equals(ExtendedDataMessage.class.getName()) || 
				datanames.get(0).equals(DataMessage.class.getName()));

	}
	
	@Test
	public void testExtendedData() throws Exception {
		Date now = Calendar.getInstance().getTime();
		Node dev = new Node(1, "dev", "home");
		DBServiceImpl.getDB().store(dev);
		
		ExtendedData data = new ExtendedData();
		data.setABool(true);
		data.setAnArray(new int[]{1,2,3,4});
		ExtendedDataMessage mesg3 = new ExtendedDataMessage(now, "message3", 1, "extended message", data, null);
		DBServiceImpl.getDB().store(mesg3);
		
		List<DataMessage> messageses = db.getDataMessages(ExtendedDataMessage.class.getName(), "dev", -1, -1);
		assertEquals(1, messageses.size());
		ExtendedDataMessage ver = (ExtendedDataMessage) messageses.get(0);
		assertEquals(mesg3.getExtendedMessage(), ver.getExtendedMessage());
		assertEquals(mesg3.getData().isABool(), ver.getData().isABool());
		for(int i=0; i<4;i++){
			assertEquals(mesg3.getData().getAnArray()[i], ver.getData().getAnArray()[i]);
		}
	}
	
	@Test
	public void testNestedData() throws Exception {
		Date now = Calendar.getInstance().getTime();
		Node dev = new Node(1, "dev", "home");
		DBServiceImpl.getDB().store(dev);
		
		NestedData nested = new NestedData();
		nested.setNest("neststring");
		ExtendedDataMessage mesg = new ExtendedDataMessage(now, "message3", 1, "extended message", null, nested);
		DBServiceImpl.getDB().store(mesg);
		
		List<DataMessage> messageses = db.getDataMessages(ExtendedDataMessage.class.getName(), "dev", -1, -1);
		assertEquals(1, messageses.size());
		ExtendedDataMessage ver = (ExtendedDataMessage) messageses.get(0);
		assertEquals(mesg.getNested().getNest(), ver.getNested().getNest());
	}

	
	@Test
	public void testDeleteNode() throws DataBaseException{
		Date now = Calendar.getInstance().getTime();
		Node dev = new Node(1, "dev", "home");
		DBServiceImpl.getDB().store(dev);
		
		ExtendedData data = new ExtendedData();
		data.setABool(true);
		data.setAnArray(new int[]{1,2,3,4});
		ExtendedDataMessage mesg3 = new ExtendedDataMessage(now, "message3", 1, "extended message", data, null);
		DBServiceImpl.getDB().store(mesg3);
		
		NewDeviceNotification notif = new NewDeviceNotification(now, false, dev);
		DBServiceImpl.getDB().store(notif);
		DBServiceImpl.getDB().commit();
		
		db.deleteDevice(dev);
		
		Query query= DBServiceImpl.getDB().query();
		query.constrain(ExtendedData.class);
		query.descend("sourceAddress").constrain(1);
		ObjectSet<ExtendedData> messagesset = query.execute();
		assertEquals(0, messagesset.size());
		
		query= DBServiceImpl.getDB().query();
		query.constrain(NewDeviceNotification.class);
		query.descend("device").descend("address").constrain(1);
		ObjectSet<NewDeviceNotification> notsset = query.execute();
		assertEquals(0, notsset.size());
	}
}
