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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

/**
 * Parses a flow of strings into objects, using JSON
 * @author Dario Salvi
 *
 */
public class ObjectParser {
	
	private static Logger logger = Logger.getLogger(ObjectParser.class.getName());

	private static String stringBuffer = "";
	private static String lastMessage = "";
	
	/**
	 * A timeout after which the buffer is reset.
	 */
	public static int TIMEOUT = 2000;
	private static long lastTime = 0;
	
	private static ArrayList<Class<?>> supportedClasses = new ArrayList<>();

	/**
	 * Add a supported class.
	 * Only supported types will be parse.
	 * @param clazz a supported class
	 */
	public static void addClassType(Class<?> clazz){
		supportedClasses.add(clazz);
	}

	/**
	 * Parses the flow of string into a supported class, if possible.
	 * @param piece a piece of string
	 * @return the instance of a supported class, or null
	 */
	public static Object parse(String piece) throws IllegalArgumentException{
		//reset the buffer after timeout
		long now = Calendar.getInstance().getTimeInMillis();
		if((now - lastTime > TIMEOUT)){
			reset();
		}
		lastTime = now;
		
		//analyse the buffer
		if(piece != null)
			stringBuffer += piece;
		
		int level = 0;
		boolean inObject = false;
		int startObjectIndex = 0;
		int startContentIndex = 0;
		int endContentIndex = 0;
		
		for(int i = 0; i < stringBuffer.length(); i++){
			char c = stringBuffer.charAt(i);
			if(c=='{'){
				level++;
				if(!inObject){
					//Here we start to be into an object
					inObject = true;
					startObjectIndex = i;
				}
				if(level == 2){
					//Here the content of the object starts
					startContentIndex = i;
				}
			}
			else if(c=='}'){
				level --;
				if(level == 0){
					if(inObject){
						//Gotcha!
						try{
							String jsonStr = stringBuffer.substring(startObjectIndex, i +1);
							String dataname = getNameOfJsonObject(jsonStr);
							String content = stringBuffer.substring(startContentIndex, endContentIndex +1);
							logger.info("Parsing string "+jsonStr+" associated to class name "+dataname);
							Class<?> cl = getClassFromSimpleName(dataname);
							Gson gson = new Gson();
							Object retVal = gson.fromJson(content, cl);
							lastMessage = jsonStr;
							//reset stuff
							inObject = false;
							startObjectIndex = 0;
							startContentIndex = 0;
							endContentIndex = 0;
							level = 0;
							stringBuffer = stringBuffer.substring(i+1);
							
							return retVal;
						} catch(Exception ex){
							logger.log(Level.WARNING, "Strange data when parsing from serial",ex);
							//In case of problems, reset and go on
							inObject = false;
							startObjectIndex = 0;
							startContentIndex = 0;
							endContentIndex = 0;
							level = 0;
							stringBuffer = stringBuffer.substring(i+1);
							//break;
						}
					}
				} else if(level == 1){
					//the content is here
					endContentIndex = i;
				}
				else if(level <0){
					//This shouldn't happen, we have missed something, let's discard it
					inObject = false;
					startObjectIndex = 0;
					startContentIndex = 0;
					endContentIndex = 0;
					level = 0;
					stringBuffer = stringBuffer.substring(i+1);
					//break;
				}
			}
		}
		
		return null;
	}

	private static String getNameOfJsonObject(String jsonstr){
		String[] splitted = jsonstr.split("\"");
		if(splitted.length <2) throw new IllegalArgumentException("String "+jsonstr+" does not contain an object with a name");
		return splitted[1];
	}

	public static Class<?> getClassFromSimpleName(String name){
		for(Class<?> clazz : supportedClasses){
			if(name.equalsIgnoreCase(clazz.getSimpleName())){
				return clazz;
			}
		}
		throw new IllegalArgumentException("Class of name "+name+" has not been found among registered ones");
	}
	
	/**
	 * Retrieves the last parsed json string.
	 */
	public static String getParsedMessage(){
		return lastMessage;
	}
	
	public static void reset(){
		logger.fine("Resetting serial buffer");
		stringBuffer = "";
	}
}
