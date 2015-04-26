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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.istack.internal.logging.Logger;

import pIoT.client.services.ChartService;
import pIoT.shared.ChartConfiguration;
import pIoT.shared.ChartValue;
import pIoT.shared.ChartValue.Type;
import pIoT.shared.DataBaseException;
import pIoT.shared.DataSetConfiguration;
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;

/**
 * Implementation of the {@link ChartService}
 * @author Dario Salvi
 *
 */
public class ChartServiceImpl extends RemoteServiceServlet implements ChartService{

	private static Logger logger = Logger.getLogger(ChartServiceImpl.class);

	public ChartServiceImpl() {
		logger.info("Charts service started");
	}

	@Override
	public LinkedHashMap<Long, ArrayList<ChartValue>> getData(ChartConfiguration cf)
			throws DataBaseException, IllegalArgumentException {
		logger.info("Chart service call recevied");
		LinkedHashMap<Long, ArrayList<ChartValue>> tempTable = 	new LinkedHashMap<Long, ArrayList<ChartValue>>();

		for(final DataSetConfiguration dsc : cf.getDatasets()){
			//Query Data
			ArrayList<DataMessage> data = getDataMessages(dsc.getClassName(), 
					dsc.getDeviceName(), cf.getStartDate(), cf.getEndDate());

			for(int i=0; i<data.size(); i++){
				Object o = data.get(i);
				long ts = ((DataMessage) o).getReceivedTimestamp().getTime();
				if(tempTable.get(ts) == null)
					tempTable.put(ts, new ArrayList<ChartValue>(cf.getDatasets().size()));
				ChartValue val = getPropertyChartValue(dsc.getPropertyName(), ts, o);
				tempTable.get(ts).add(val);
			}
		}

		return tempTable;
	}

	public static ChartValue getPropertyChartValue(String propertyName, long ts, Object instance) throws  IllegalArgumentException {
		logger.fine("Getting property "+propertyName+" from "+instance.getClass().getName());
		Class<?> clazz = instance.getClass();
		if (propertyName == null)
			throw new IllegalArgumentException("PropertyName must not be null.");

		final String[] path = propertyName.split("\\.");
		ChartValue val = null;

		for (int i = 0; i < path.length; i++) {
			propertyName = path[i];
			PropertyDescriptor[] propDescs;
			try {
				propDescs = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
			} catch (IntrospectionException e1) {
				throw new IllegalArgumentException(e1);
			}
			for (final PropertyDescriptor propDesc : propDescs)
				if (propDesc.getName().equals(propertyName)){
					clazz = propDesc.getPropertyType();
					try {
						instance = propDesc.getReadMethod().invoke(instance);
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new IllegalArgumentException(e);
					}
					if (i == path.length - 1) {
						if(instance == null){
							if((clazz == Double.class) || (clazz == Integer.class)
									|| (clazz == Float.class) || (clazz == Long.class))
								val = new ChartValue(ts, Type.number);
							if(clazz == Boolean.class)
								val = new ChartValue(ts, Type.bool);
							if(clazz == Date.class)
								val = new ChartValue(ts, Type.date);
							if(clazz == String.class)
								val = new ChartValue(ts, Type.string);
						}
						if(instance instanceof Double)
							val = new ChartValue(ts, (Double)instance);
						if(instance instanceof Integer)
							val = new ChartValue(ts, (double)(Integer)instance);
						if(instance instanceof Float)
							val = new ChartValue(ts, (double)(Float)instance);
						if(instance instanceof Long)
							val = new ChartValue(ts, (double)(Long)instance);
						if(instance instanceof Date)
							val = new ChartValue(ts, (Date)instance);
						if(instance instanceof Boolean)
							val = new ChartValue(ts, (Boolean)instance);
						if(instance instanceof String)
							val = new ChartValue(ts, (String)instance);
						return val;
					}
				}
		}
		return null;
	}

	public static ArrayList<DataMessage> getDataMessages(String className, final String deviceName, final Date startDate, final Date endDate){
		logger.info("Preparing data for "+className+" device "+deviceName+" from "+startDate+" to "+endDate);
		final Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("The class "+className+" must be a DataMessage");
		}
		if(! DataMessage.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException("The class "+className+" must be a DataMessage");

		int devadd = 0;
		if(deviceName != null){
			//Get the source address of the device
			Query query = DBServiceImpl.getDB().query();
			query.constrain(Node.class);
			query.descend("name").constrain(deviceName);

			ObjectSet<Node> devicesset = query.execute();
			if(devicesset.isEmpty())
				throw new IllegalArgumentException("The name provided for the device "+deviceName+" does not exist");

			//Get the first element (only one should exist)
			Node device = devicesset.next();
			devadd = device.getAddress();
		}
		final int devAddress = devadd;

		ArrayList<DataMessage> retval = new ArrayList<DataMessage>();

		//Query for the messages
		Predicate<DataMessage> predicate = new Predicate<DataMessage>() {
			@Override
			public boolean match(DataMessage candidate) {
				if(!clazz.isAssignableFrom(candidate.getClass()))
					return false;

				if(startDate != null)
					if(startDate.compareTo(candidate.getReceivedTimestamp())>=0)
						return false;

				if(endDate != null)
					if(endDate.compareTo(candidate.getReceivedTimestamp())<=0)
						return false;

				if(deviceName != null)
					if(candidate.getSourceAddress() != devAddress)
						return false;
				
				return true;
			}
		};
		QueryComparator<DataMessage> comparator = new QueryComparator<DataMessage>() {
			public int compare(DataMessage mess1, DataMessage mess2) {
				return mess1.getReceivedTimestamp().compareTo(mess2.getReceivedTimestamp());
			}
		};

		ObjectSet<DataMessage> messagesset = null;
		try{
			messagesset = DBServiceImpl.getDB().query(predicate, comparator);
		}
		catch(NullPointerException ne){
			//simply means there is no data	
		}
		logger.fine("Got "+retval.size()+" samples");
		
		retval.addAll(messagesset);
		return retval;
	}

	@Override
	public ArrayList<ChartConfiguration> getChartsConfiguration()
			throws DataBaseException {
		logger.fine("Loading charts from DB");
		Query query = DBServiceImpl.getDB().query();
		query.constrain(ChartConfiguration.class);
		ArrayList<ChartConfiguration> ret = new ArrayList<ChartConfiguration>();
		ObjectSet<ChartConfiguration> obs = query.execute();
		ret.addAll(obs);
		return ret;
	}

	@Override
	public void addChartConfiguration(ChartConfiguration cf)
			throws DataBaseException {
		DBServiceImpl.getDB().store(cf);
		DBServiceImpl.getDB().commit();
		logger.info("Added chart " + cf.getTitle());
	}

	@Override
	public void updateChartConfiguration(ChartConfiguration oldcf,
			ChartConfiguration newcf) throws DataBaseException {
		removeChartConfiguration(oldcf);
		addChartConfiguration(newcf);
	}

	@Override
	public void removeChartConfiguration(ChartConfiguration cf)
			throws DataBaseException {
		ObjectSet<ChartConfiguration> os = DBServiceImpl.getDB().queryByExample(cf);
		for(ChartConfiguration r : os){
			logger.info("Removing chart "+r.getTitle());
			DBServiceImpl.getDB().delete(r);
			DBServiceImpl.getDB().commit();
		}
	}
}
