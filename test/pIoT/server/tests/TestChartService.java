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
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pIoT.client.tests.ExtendedData;
import pIoT.client.tests.ExtendedDataMessage;
import pIoT.server.ChartServiceImpl;
import pIoT.server.DBServiceImpl;
import pIoT.shared.ChartConfiguration;
import pIoT.shared.ChartValue;
import pIoT.shared.ChartValue.Type;
import pIoT.shared.DataSetConfiguration;
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;

/**
 * @author Dario Salvi
 *
 */
public class TestChartService {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		DBServiceImpl.dbFileName = "DBTEST";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		DBServiceImpl.deleteDB();
	}

	@Test
	public void testGetFilteredDataMessages() throws Exception {
		//store 10 messages
		long now = Calendar.getInstance().getTimeInMillis();
		Node dev1 = new Node(1, "dev1", "home");
		DBServiceImpl.store(dev1);
		//yesterday's data
		long yesteday = new Date(now - 26*60*60*1000).getTime();
		for(int i=0; i<10; i++){
			Date msgDate = new Date(yesteday + i*100);
			DataMessage mess = new DataMessage(msgDate, "message"+i, 1);
			DBServiceImpl.store(mess);
		}

		//today's data
		for(int i=10; i<20; i++){
			Date msgDate = new Date(now + i*100);
			DataMessage mess = new DataMessage(msgDate, "message"+i, 1);
			DBServiceImpl.store(mess);
		}

		//retrieve them
		ArrayList<DataMessage> messages = ChartServiceImpl.getDataMessages(DataMessage.class.getName(), "dev1",
				new Date(now), new Date(now+24*60*60*1000));
		assertEquals(10, messages.size());
		//check they're ordered in descending way
		for(int i=0; i<10; i++){
			assertEquals("message"+(10+i), messages.get(i).getSourceMessage());
		}
	}

	@Test
	public void testDataGet() throws Exception{
		//store 10 messages
		long now = Calendar.getInstance().getTimeInMillis();
		Node dev1 = new Node(1, "dev1", "home");
		DBServiceImpl.store(dev1);
		//generate data
		for(int i=0; i<10; i++){
			Date msgDate = new Date(now + i*100);
			DataMessage mess = new DataMessage(msgDate, "message"+i, 1);
			DBServiceImpl.store(mess);
		}
		
		ChartServiceImpl charts = new ChartServiceImpl();
		ChartConfiguration cf = new ChartConfiguration();
		cf.setStartDate(new Date(now - 10000));
		cf.setEndDate(new Date(now + 10000));
		cf.setDatasets(new ArrayList<DataSetConfiguration>());
		DataSetConfiguration dscf = new DataSetConfiguration();
		dscf.setClassName(DataMessage.class.getName());
		dscf.setDeviceName("dev1");
		dscf.setPropertyName("sourceMessage");
		cf.getDatasets().add(dscf);
		LinkedHashMap<Long, ArrayList<ChartValue>> tb = charts.getData(cf);
		assertEquals(10,tb.size());
		int pos = 0;
		for(Long ts : tb.keySet()){
			assertEquals(Type.string, tb.get(ts).get(0).getType());
			assertEquals("message"+pos, tb.get(ts).get(0).getString());
			pos++;
		}
	}
	
	@Test
	public void testExtendedDataGet() throws Exception{
		//store 10 messages
		long now = Calendar.getInstance().getTimeInMillis();
		Node dev1 = new Node(1, "dev1", "home");
		DBServiceImpl.store(dev1);
		//generate data
		for(int i=0; i<10; i++){
			Date msgDate = new Date(now + i*100);
			ExtendedDataMessage edm = new ExtendedDataMessage(msgDate, "{bbb}", 1, "{ext}",
					new ExtendedData(true, new int[]{1, 2,3}, new ArrayList<Integer>()), 
					new ExtendedDataMessage.NestedData("message"+i));
			DBServiceImpl.store(edm);
		}
		
		ChartServiceImpl charts = new ChartServiceImpl();
		ChartConfiguration cf = new ChartConfiguration();
		cf.setStartDate(new Date(now - 10000));
		cf.setEndDate(new Date(now + 10000));
		cf.setDatasets(new ArrayList<DataSetConfiguration>());
		DataSetConfiguration dscf = new DataSetConfiguration();
		dscf.setClassName(DataMessage.class.getName());
		dscf.setDeviceName("dev1");
		dscf.setPropertyName("nested.nest");
		cf.getDatasets().add(dscf);
		LinkedHashMap<Long, ArrayList<ChartValue>> tb = charts.getData(cf);
		assertEquals(10,tb.size());
		int pos = 0;
		for(Long ts : tb.keySet()){
			assertEquals(Type.string, tb.get(ts).get(0).getType());
			assertEquals("message"+pos, tb.get(ts).get(0).getString());
			pos++;
		}
	}

}
