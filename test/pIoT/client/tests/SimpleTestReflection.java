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

import java.util.Arrays;
import java.util.List;

import org.dt.reflector.client.PropertyUtils;
import org.dt.reflector.client.Reflectable;
import org.dt.reflector.client.Reflector;

import com.google.gwt.junit.client.GWTTestCase;

public class SimpleTestReflection extends GWTTestCase {

	public SimpleTestReflection() {
	}
	
	@Override
	public String getModuleName() {
		return "pIoT.pIoTServer_test";
	}

	public static class MyClass implements Reflectable{
		private int myInt;
		private boolean myBool;
		
		public MyClass(){
		}

		public int getMyInt() {
			return myInt;
		}

		public void setMyInt(int myInt) {
			this.myInt = myInt;
		}

		public boolean isMyBool() {
			return myBool;
		}

		public void setMyBool(boolean myBool) {
			this.myBool = myBool;
		}
	}
	
	public void testReflection(){
		MyClass myobject = new MyClass();
		
		myobject.setMyBool(true);
		myobject.setMyInt(10);
		
		Reflector refl = PropertyUtils.getReflector(MyClass.class);
		assertNotNull(refl);
		
		List<String> props = Arrays.asList(refl.list(myobject));
		assertTrue(props.contains("myInt"));
		assertTrue(props.contains("myBool"));
		
		assertEquals("10", refl.get(myobject, "myInt").toString());
		assertEquals("true", refl.get(myobject, "myBool").toString());
	}
	
}
