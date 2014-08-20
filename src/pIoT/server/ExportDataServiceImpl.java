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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/** A servlet that produces a CSV file from data messages.
 * @author Dario Salvi
 *
 */
public class ExportDataServiceImpl extends HttpServlet {

	public ExportDataServiceImpl() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
	        throws ServletException, IOException {

	    String dataname = (String) req.getSession().getAttribute("dataname");
	    String devicename = (String) req.getSession().getAttribute("devicename");
	    
	    ServletOutputStream out = response.getOutputStream();
	    // do not cache
	    response.setHeader("Expires", "0");  
	    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");  
	    response.setHeader("Pragma", "public");
	    // content length is needed for MSIE
	    //response.setContentLength(data.length);
	    // set the filename and the type
	    response.setContentType("text/csv");  
	    response.addHeader("Content-Disposition", "attachment;filename=" + dataname + ".csv");  
	    
	    produceData(dataname, devicename, out);
	    out.flush();
	    out.close();
	}
	
	private void produceData(String dataname, String devicename, OutputStream out){
		/*ObjectContainer db = DBServiceImpl.getDB();
		Query query = getDB().query();
		query.constrain(Node.class);
		query.descend("name").constrain(deviceName);*/
		PrintStream stream = new PrintStream(out);
		stream.println("TEST,DATA");
		stream.println("1,2");
		stream.println("3,4");
		stream.println("5,6");
	}
}
