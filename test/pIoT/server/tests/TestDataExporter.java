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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pIoT.server.DBServiceImpl;
import pIoT.server.ExportDataServiceImpl;
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.messages.examples.Hello;

public class TestDataExporter {

	private static DBServiceImpl db;
	private final String dbFileName = "testDB";

	@Before
	public void setUp() throws Exception {
		DBServiceImpl.dbFileName = dbFileName;
		
		db = new DBServiceImpl();
		
		Node node = new Node(123, "my node", "home");
		db.store(node);
		Hello data = new Hello(Calendar.getInstance().getTime(), 
				"{\"just an example\"}", 123, 3, 24, 10, 0, 0, 0);
		db.store(data);
		data = new Hello(Calendar.getInstance().getTime(), 
				"{\"just an example\"}", 123, 2.99F, 24.3F, 30, 0, 0, 0);
		db.store(data);
		
		node = new Node(321, "another node", "bath");
		db.store(node);
		data = new Hello(Calendar.getInstance().getTime(), 
				"{\"just an example\"}", 321, 2.8F, 32, 20, 0, 0L, 0L);
		db.store(data);
		data = new Hello(Calendar.getInstance().getTime(), 
				"{\"just an example\"}", 321, 2.88F, 31.8F, 40, 0, 0, 0);
		db.store(data);
	}

	@After
	public void tearDown() throws Exception {
		DBServiceImpl.deleteDB();
	}

	@Test
	public void teststringmanip() {
		String dataname = "pIoT/shared/messages/examples/Hello.Vcc.min";
		
		String className = ExportDataServiceImpl.extractClassname(dataname);
	    
	    assertEquals("pIoT.shared.messages.examples.Hello", className);
	    
	    String[] props = ExportDataServiceImpl.extractProperties(dataname);
	    assertEquals(2, props.length);
	    assertEquals("Vcc", props[0]);
	    assertEquals("min", props[1]);
	}
	
	@Test
	public void testDataRetrieving() throws Exception{
		String classname = Hello.class.getName();
		String propsStr ="vcc";
		Class<?> cl = Class.forName(classname);
		assertEquals(Hello.class.getName(), cl.getName());

		ArrayList<?> data = db.getDataMessages(classname, null, -1, -1);
		assertEquals(4, data.size());
		
		for(int i=0; i<data.size(); i++){
			Object o = data.get(i);
			assertNotNull(o);
			assertEquals(Hello.class.getName(), o.getClass().getName());
			
			long ts = ((DataMessage) o).getReceivedTimestamp().getTime();
			int node = ((DataMessage) o).getSourceAddress();
			Object val = ExportDataServiceImpl.getPropertyValue(propsStr, o);
			String value = val.toString();
			System.out.println(ts+" "+node+" "+value);
		}
	}
	
	@Test
	public void testDataRetrieving2() throws Exception{
		String classname = Hello.class.getName();
		String propsStr ="vcc";
		Class<?> cl = Class.forName(classname);
		assertEquals(Hello.class.getName(), cl.getName());

		ArrayList<?> data = db.getDataMessages(classname, "my node", -1, -1);
		assertEquals(2, data.size());
		
		for(int i=0; i<data.size(); i++){
			Object o = data.get(i);
			assertNotNull(o);
			assertEquals(Hello.class.getName(), o.getClass().getName());
			
			long ts = ((DataMessage) o).getReceivedTimestamp().getTime();
			int node = ((DataMessage) o).getSourceAddress();
			Object val = ExportDataServiceImpl.getPropertyValue(propsStr, o);
			String value = val.toString();
			System.out.println(ts+" "+node+" "+value);
		}
	}
	
	public static class MyBean{
		private String name;

		public MyBean(){
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class MySecondBean{
		private String anothername;
		private MyBean abean;
		
		public MySecondBean(){
		}

		public String getAnothername() {
			return anothername;
		}

		public void setAnothername(String anothername) {
			this.anothername = anothername;
		}

		public MyBean getAbean() {
			return abean;
		}

		public void setAbean(MyBean abean) {
			this.abean = abean;
		}
	}
	@Test
	public void testBeanAccess() throws Exception {
		MySecondBean b2 = new MySecondBean();
		b2.anothername = "bb";
		b2.abean = new MyBean();
		b2.abean.name = "aa";
		
		Object val = ExportDataServiceImpl.getPropertyValue("anothername", b2);
		String value = val.toString();
		assertEquals("bb", value);
		
		val = ExportDataServiceImpl.getPropertyValue("abean.name", b2);
		value = val.toString();
		assertEquals("aa", value);
	}

}
