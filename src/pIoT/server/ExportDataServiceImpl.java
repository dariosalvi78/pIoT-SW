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
 * Package containing server side functionalities.
 */
package pIoT.server;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pIoT.shared.DataBaseException;
import pIoT.shared.messages.DataMessage;


/** A servlet that produces a CSV file from data messages.
 * @author Dario Salvi
 *
 */
public class ExportDataServiceImpl extends HttpServlet {

	private static Logger logger = Logger.getLogger(ExportDataServiceImpl.class.getName());

	public ExportDataServiceImpl() {
		logger.info("Data Exporter Service started");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		logger.fine("Received export request: "+req.toString());

		String dataname = (String) req.getParameter("dataname");

		String className = extractClassname(dataname);
		String[] properties = extractProperties(dataname);
		String lastprop = properties[properties.length-1];

		String devicename = (String) req.getParameter("devicename");

		ServletOutputStream out = response.getOutputStream();
		// do not cache
		response.setHeader("Expires", "0");  
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");  
		response.setHeader("Pragma", "public");
		// content length is needed for MSIE
		//response.setContentLength(data.length);
		// set the filename and the type
		response.setContentType("text/csv");  
		response.addHeader("Content-Disposition", "attachment;filename=" + lastprop + ".csv");  

		try {
			produceData(className, properties, devicename, out);
		}  catch (Exception e) {
			logger.log(Level.SEVERE,"Cannot extract data for "+dataname ,e);
			throw new ServletException(e);
		}
		out.flush();
		out.close();
	}

	public static String extractClassname(String dataname){
		String[] pkg = dataname.split("/");
		String last = pkg[pkg.length-1];
		pkg[pkg.length-1] = last.substring(0, last.indexOf("."));
		String className = "";
		for(int i=0; i<pkg.length; i++)
			className += i<pkg.length-1 ? pkg[i]+".":pkg[i];
		return className;
	}

	public static String[] extractProperties(String dataname){
		String[] props = dataname.split("\\.");
		return Arrays.copyOfRange(props, 1, props.length);
	}

	private void produceData(String classname, String[] properties, String devicename, OutputStream out) throws IllegalArgumentException, ClassNotFoundException, DataBaseException, IllegalAccessException, InvocationTargetException, IntrospectionException{
		String propsStr ="";
		for(int i=0; i< properties.length; i++)
			propsStr+= (properties[i] + (i<properties.length-1? ".": ""));
		logger.finer("Querying data for exporting, class: "+classname+" properties: "+propsStr);
		Class<?> cl = Class.forName(classname);
		ArrayList<?> data = DBServiceImpl.getDataMessages(cl, devicename, -1, -1);

		PrintStream stream = new PrintStream(out);
		stream.println("timestamp,nodeaddress,"+properties[properties.length-1]);

		for(int i=0; i<data.size(); i++){
			Object o = data.get(i);
			long ts = ((DataMessage) o).getReceivedTimestamp().getTime();
			int node = ((DataMessage) o).getSourceAddress();
			Object val = ExportDataServiceImpl.getPropertyValue(cl, propsStr, o);
			String value = val.toString();
			stream.println(ts+ ","+node+","+ value);
		}
	}

	/**
	 * Retrieves the type of the property with the given name of the given
	 * Class.<br>
	 * Supports nested properties following bean naming convention.
	 * 
	 * "foo.bar.name"
	 * 
	 * @see PropertyUtils#getPropertyDescriptors(Class)
	 * 
	 * @return Null if no property exists.
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static Object getPropertyValue(Class<?> clazz, String propertyName, Object instance) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (clazz == null)
			throw new IllegalArgumentException("Clazz must not be null.");
		if (propertyName == null)
			throw new IllegalArgumentException("PropertyName must not be null.");
		if (instance == null)
			throw new IllegalArgumentException("Instance must not be null.");

		final String[] path = propertyName.split("\\.");

		for (int i = 0; i < path.length; i++) {
			propertyName = path[i];
			final PropertyDescriptor[] propDescs = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
			for (final PropertyDescriptor propDesc : propDescs)
				if (propDesc.getName().equals(propertyName)){
					clazz = propDesc.getPropertyType();
					instance = propDesc.getReadMethod().invoke(instance);
					if (i == path.length - 1)
						return instance;
				}
		}
		return null;
	}
}
