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

import com.db4o.ObjectSet;

/**
 * Class used to store configuration parameters into the DB.
 * @author Dario
 *
 */
public class Configs {

	private String comPort;
	
	/**
	 * Empty constructor
	 */
	public Configs() {
	}
	
	public String getComPort() {
		return comPort;
	}

	public void setComPort(String comPort) {
		this.comPort = comPort;
	}

	public static Configs retrieveConfigs(){
		ObjectSet<Configs> os = DB.getDB().query(Configs.class);
		if(os.size() >0)
			return os.get(0);
		else{
			Configs cfs = new Configs();
			DB.getDB().store(cfs);
			return cfs;
		}
	}
}
