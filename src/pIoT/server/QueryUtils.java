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

import java.util.Iterator;

import com.db4o.ObjectSet;

/**
 * An utility for limiting the results of the query.
 * @author from http://community.versant.com/Forums/tabid/98/aft/5797/Default.aspx
 *
 */
public class QueryUtils {

	/**
	 * Limits the results of the query set
	 * @param objectSet the object set returned by the DB
	 * @param from the start index, 0 is the minimum
	 * @param to the end index, it can go up to the size of the objectset
	 * @return an iterable with the objectset limited to the specified indexes
	 */
	public static <T> Iterable<T> limit(final ObjectSet<T> objectSet, final int from, final int to){
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					private int current = from >=0? from : 0;
					
					public boolean hasNext() {
						if(current >= objectSet.size())
							return false;
						if(to >= 0)
							return current <= to;
						else return true;
					}
					
					public T next() {
						T object = objectSet.get(current);
						current++;
						return object;
					}
					
					public void remove() {
						throw new UnsupportedOperationException("Cannot remove");
					}
				};
			}
		};
	}
}
