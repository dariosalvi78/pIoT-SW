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

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import pIoT.server.ActionsServiceImpl;
import pIoT.server.DBServiceImpl;
import pIoT.shared.notifications.Notification;

/**
 * @author Dario Salvi
 *
 */
public class TestNotifications {

	DBServiceImpl db;
	ActionsServiceImpl actions;
	
	@Before
	public void setUp() throws Exception {
		DBServiceImpl.dbFileName = "DBTEST";
		db = new DBServiceImpl();

		actions = new ActionsServiceImpl();
	}

	@After
	public void tearDown() throws Exception {
		db.destroy();
		DBServiceImpl.deleteDB();
	}
	
	@Test
	public void testStoreNotification() throws Exception{
		Date now = Calendar.getInstance().getTime();
		Notification not = new Notification("test", now, false);
		
		actions.storeNotification(not);
		
		assertEquals(1, db.getUnfixedNotifications().size());
		
		actions.fixNotification(not);
		
		assertEquals(0, db.getUnfixedNotifications().size());
		
		not = new Notification("test", now, false);
		
		actions.storeNotification(not);
		
		assertEquals(1, db.getUnfixedNotifications().size());
	}

}
