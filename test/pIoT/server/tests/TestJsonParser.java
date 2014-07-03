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

import org.junit.Before;
import org.junit.Test;

import pIoT.client.tests.ExtendedDataMessage;
import pIoT.server.ObjectParser;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.messages.HelloMessage;
import pIoT.shared.messages.LightMessage;

public class TestJsonParser {
	
	@Before
	public void setup(){
		ObjectParser.reset();
	}
	

	@Test
	public void testRegExpr() {
		String test = "   {  \"amessage\"    :\"HELLO\"}";
		assertEquals(5, test.split("\"").length);
		assertEquals("amessage", test.split("\"")[1]);
	}
	
	@Test
	public void testParser() throws Exception{
		ObjectParser.addClassType(DataMessage.class);
		ObjectParser.addClassType(ExtendedDataMessage.class);
		String buff = "";
		assertNull(ObjectParser.parse(buff));
		buff = "     { \"dataMessage\"  :  ";
		assertNull(ObjectParser.parse(buff));
		buff = "{ \"sourceAddress\": ";
		assertNull(ObjectParser.parse(buff));
		buff = " 125, \"sourceMessage\": \"hello!\" }   ";
		assertNull(ObjectParser.parse(buff));
		buff = "  } ";
		Object o = ObjectParser.parse(buff);
		assertNotNull(o);
		assertTrue(o instanceof DataMessage);
		DataMessage m = (DataMessage)o;
		assertEquals(125, m.getSourceAddress());
		assertEquals("hello!", m.getSourceMessage());
		
		//now put some noise
		buff = "  { dewewerwer } }  ";
		assertNull(ObjectParser.parse(buff));
		
		buff = " { \"dataMessage\": { \"sourceAddress\": 200, \"sourceMessage\": \"hi\"} } ";
		o = ObjectParser.parse(buff);
		assertNotNull(o);
		m = (DataMessage)o;
		assertEquals(200, m.getSourceAddress());
		assertEquals("hi", m.getSourceMessage());
		
		//more crap
		buff= "{ \"afdaerwer\" {{  { \"asdasrwer\" }";
		assertNull(ObjectParser.parse(buff));
		buff = " } \"asfdadf\" }";
		assertNull(ObjectParser.parse(buff));
		buff = "  } bbabbababab ";
		assertNull(ObjectParser.parse(buff));
		buff = " \"  \"  aaerwer\"  \"\"  ";
		assertNull(ObjectParser.parse(buff));
		
		
		buff = " { \"extendedDataMessage\": { \"extendedMessage\" : \"superhello\", ";
		assertNull(ObjectParser.parse(buff));
		buff = "\"data\":     { \"aBool\": True, \"anArray\": [1,2,3,4] } }   } ";
		o = ObjectParser.parse(buff);
		assertNotNull(o);
		assertTrue(o instanceof ExtendedDataMessage);
		ExtendedDataMessage em = (ExtendedDataMessage)o;
		assertEquals("superhello", em.getExtendedMessage());
		assertEquals(true, em.getData().isABool());
		assertEquals(1, em.getData().getAnArray()[0]);
		assertEquals(2, em.getData().getAnArray()[1]);
		assertEquals(3, em.getData().getAnArray()[2]);
		assertEquals(4, em.getData().getAnArray()[3]);
	}
	
	@Test
	public void testParser2(){
		ObjectParser.addClassType(DataMessage.class);
		String buff = null;
		assertNull(ObjectParser.parse(buff));
		buff = "{ \"DataMessage\": { \"s";
		assertNull(ObjectParser.parse(buff));
		buff = null;
		assertNull(ObjectParser.parse(buff));
		buff = "ourceAddress\" : 10 } }\n";
		Object o = ObjectParser.parse(buff);
		assertNotNull(o);
		DataMessage m = (DataMessage)o;
		assertEquals(10, m.getSourceAddress());
	}
	
	@Test
	public void testParser3(){
		ObjectParser.addClassType(DataMessage.class);
		ObjectParser.addClassType(HelloMessage.class);
		ObjectParser.addClassType(LightMessage.class);
		
		String buff = null;
		assertNull(ObjectParser.parse(buff));
		buff = "Received a mesage of type 0 from node 1234\n";
		assertNull(ObjectParser.parse(buff));
		buff = "{ \"HelloMessage\": { ";
		assertNull(ObjectParser.parse(buff));
		buff = "\"sourceAddress\":1234, \"temperature\":23.33, \"Vcc\":3.08 }}\nReceived a mesage ";
		Object o = ObjectParser.parse(buff);
		assertNotNull(o);
		assertTrue(o instanceof HelloMessage);
		HelloMessage hm = (HelloMessage)o;
		assertEquals(1234,hm.getSourceAddress());
		assertEquals(23.33F,hm.getTemperature(),0.01);
		assertEquals(3.08F,hm.getVcc(),0.01);
		
		buff = "of type 100 from node 1234\n";
		assertNull(ObjectParser.parse(buff));
		buff="{ \"LightMessage\": { \"sourceAddress\":1234, \"intensity\":246 }}\n";
		o = ObjectParser.parse(buff);
		assertNotNull(o);
		assertTrue(o instanceof LightMessage);
		LightMessage lm = (LightMessage)o;
		assertEquals(1234, lm.getSourceAddress());
		assertEquals(246, lm.getIntensity());
		
		buff = "Received a mesage of type 0 from node 1234\n"
			+ "{ \"HelloMessage\": { \"sourceAddress\":1234, \"temperature\":21.36, \"Vcc\":3.09 }}\n"
			+ "Received a mesage of type 100 from node 1234"
			+ "{ \"LightMessage\": { \"sourceAddress\":1234, \"intensity\":244 }}\n";
		o = ObjectParser.parse(buff);
		assertNotNull(o);
		assertTrue(o instanceof HelloMessage);
		o = ObjectParser.parse("");
		assertNotNull(o);
		assertTrue(o instanceof LightMessage);
	}
	
	
	
	
	

}
