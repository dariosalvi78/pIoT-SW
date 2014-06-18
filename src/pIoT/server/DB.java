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

import java.io.File;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

/**
 * Holder of the DB singleton.
 *
 */
public class DB {

	private static ObjectContainer db;
	private static final String dbFileName = "DB";
	
	/**
	 * 
	 */
	public static synchronized ObjectContainer getDB() {
		if(db == null)
			db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), dbFileName);
		
		return db;
	}
	
	/**
	 * Deletes the DB entirely.
	 */
	public static void deleteDB() {
		db.close();
		new File(dbFileName).delete();
		db = null;
	}
	
	/**
	 * Stores and commits.
	 */
	public static void store(Object o){
		db.store(o);
		db.commit();
	}

}
