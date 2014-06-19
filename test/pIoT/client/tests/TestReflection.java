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
 * Package containing some automatic tests,
 * these contain pieces that are rendered on client side
 */
package pIoT.client.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dt.reflector.client.PropertyUtils;
import org.dt.reflector.client.Reflector;

import pIoT.shared.ReflectableList;
import pIoT.shared.messages.DataMessage;

import com.google.gwt.junit.client.GWTTestCase;

public class TestReflection extends GWTTestCase {

	public TestReflection() {
	}

	@Override
	public String getModuleName() {
		return "pIoT.pIoTServer_test";
	}
	
	public void testAlwaysPass() {
	    assertTrue(true);
	  }
	
	public void testReflectionM(){

		DataMessage mess = new DataMessage(new Date(123456789),
				"my message", 5);
		Class<?> clazz = mess.getClass();
		Reflector refl = PropertyUtils.getReflector(clazz);
		
		List<String> props = Arrays.asList(refl.list(mess));
		assertTrue(props.contains("receivedTimestamp"));
		assertTrue(props.contains("sourceAddress"));
		assertTrue(props.contains("sourceMessage"));
		
		assertEquals(new Date(123456789).toString(), refl.get(mess, "receivedTimestamp").toString());
		assertEquals("5", refl.get(mess, "sourceAddress").toString());
		assertEquals("my message", refl.get(mess, "sourceMessage"));
	}
	
	public void testReflectionEM(){
		
		ExtendedData ed = new ExtendedData();
		ed.setABool(true);
		ed.setAnArray(new int[]{1,2,3,4});
		ReflectableList list = new ReflectableList();
		/*list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);*/
		ed.setAList(list);
		ExtendedDataMessage emess = new ExtendedDataMessage(new Date(123456789),
				"my message", 5, "ext message", ed);
		
		Class<?> clazz = emess.getClass();
		Reflector refl = PropertyUtils.getReflector(clazz);
		List<String> props = Arrays.asList(refl.list(emess));
		assertTrue(props.contains("receivedTimestamp"));
		assertTrue(props.contains("sourceAddress"));
		assertTrue(props.contains("sourceMessage"));
		assertTrue(props.contains("extendedMessage"));
		assertEquals("ext message", refl.get(emess, "extendedMessage"));
		assertTrue(props.contains("data"));
		
		Object emd = refl.get(emess, "data");
		assertNotNull(emd);
		clazz = emd.getClass();
		refl = PropertyUtils.getReflector(clazz);
		assertNotNull(refl);
		props = Arrays.asList(refl.list(emd));
		assertTrue(props.contains("aBool"));
		assertEquals(true, refl.get(emd, "aBool"));
		
		assertTrue(props.contains("anArray"));
		Object arr = refl.get(emd, "anArray");
		clazz = arr.getClass();
		assertTrue(clazz.isArray());
		
		assertTrue(props.contains("aList"));
		assertNotNull(emd);
		Object lst = refl.get(emd, "aList");
		clazz = lst.getClass();
		refl = PropertyUtils.getReflector(clazz);
		assertNotNull(refl);
		props = Arrays.asList(refl.list(lst));
		System.out.println("PROPS---");
		for(String prop : props)
			System.out.println(prop);
		
	}
}
