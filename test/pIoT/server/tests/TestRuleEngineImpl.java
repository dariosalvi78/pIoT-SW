package pIoT.server.tests;

import static org.junit.Assert.*;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pIoT.client.tests.ExtendedDataMessage;
import pIoT.server.DBServiceImpl;
import pIoT.server.RulesServiceImpl;
import pIoT.server.rules.PreParser.PreParsed;
import pIoT.server.rules.PreParser.PreParsed.Constraint;
import pIoT.shared.CompileRuleException;
import pIoT.shared.DataBaseException;
import pIoT.shared.DuplicateRuleException;
import pIoT.shared.Node;
import pIoT.shared.ParseRuleException;
import pIoT.shared.Rule;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.notifications.Notification;


public class TestRuleEngineImpl {

	DBServiceImpl db;

	@Before
	public void setUp() throws Exception {
		DBServiceImpl.dbFileName = "DBTEST";
		db = new DBServiceImpl();

		RulesServiceImpl.addSupportedClass(ExtendedDataMessage.class);
	}

	@After
	public void tearDown() throws Exception {
		db.destroy();
		DBServiceImpl.deleteDB();
	}

	@Test
	public void testPermutations(){
		TreeMap<String, ArrayList<Object>> lists = new TreeMap<>();
		ArrayList<Object> l = new ArrayList<>();
		l.add("1");
		l.add("2");
		l.add("3");
		lists.put("v1", l);
		l = new ArrayList<>();
		l.add("A");
		l.add("B");
		l.add("C");
		lists.put("v2", l);
		l = new ArrayList<>();
		l.add("X");
		l.add("Y");
		lists.put("v3", l);

		TreeMap<String, Object> curr = new TreeMap<>();
		ArrayList<TreeMap<String, Object>> ret = new ArrayList<>();

		RulesServiceImpl.generatePermutations(lists, 0, curr, ret);

		assertEquals("1",ret.get(0).get("v1"));
		assertEquals("A",ret.get(0).get("v2"));
		assertEquals("X",ret.get(0).get("v3"));

		assertEquals("1",ret.get(1).get("v1"));
		assertEquals("A",ret.get(1).get("v2"));
		assertEquals("Y",ret.get(1).get("v3"));

		assertEquals("1",ret.get(2).get("v1"));
		assertEquals("B",ret.get(2).get("v2"));
		assertEquals("X",ret.get(2).get("v3"));

		assertEquals("2",ret.get(10).get("v1"));
		assertEquals("C",ret.get(10).get("v2"));
		assertEquals("X",ret.get(10).get("v3"));
	}

	@Test
	public void testQueryPreparsed() throws Exception {
		//store 10 messages
		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		DBServiceImpl.store(dev1);
		for(int i=0; i<10; i++){
			Date msgDate = new Date(now.getTime() + i*100);
			DataMessage mess = new DataMessage(msgDate, "message"+i, 1);
			DBServiceImpl.store(mess);
		}
		PreParsed p = new PreParsed();
		p.className = "DataMessage";
		p.constraints = new ArrayList<Constraint>();
		Constraint c = new Constraint();
		c.variableName = "limit";
		c.value = "last";
		p.constraints.add(c);

		RulesServiceImpl re = new RulesServiceImpl();

		ArrayList<Object> data = re.queryPreparsed(p);

		assertEquals(1, data.size());
		assertEquals("message9", ((DataMessage)data.get(0)).getSourceMessage());

		p = new PreParsed();
		p.className = "DataMessage";
		p.constraints = new ArrayList<Constraint>();
		c = new Constraint();
		c.variableName = "limit";
		c.value = "first";
		p.constraints.add(c);
		data = re.queryPreparsed(p);

		assertEquals(1, data.size());
		assertEquals("message0", ((DataMessage)data.get(0)).getSourceMessage());


	}

	@Test
	public void testNoData() throws Exception{
		//store rule
		Rule sr = new Rule("simpleRule",
				"DataMessage(limit = last).sourceMessage == 'message9'", 
				"SENDNOTIFICATION",
				"test",
				1, "myrules", null);
		db.store(sr);

		RulesServiceImpl re = new RulesServiceImpl();

		ArrayList<Notification> nots;

		re.reason();

		nots = db.getUnfixedNotifications();
		assertEquals(0, nots.size());
	}

	@Test
	public void testSimpleRule() throws Exception{
		//store rule
		Rule sr = new Rule("simpleRule",
				"DataMessage(limit = last).sourceMessage == 'message9'", 
				"SENDNOTIFICATION",
				"test",
				1, "myrules", null);
		db.store(sr);

		RulesServiceImpl re = new RulesServiceImpl();

		ArrayList<Notification> nots;

		//store 10 messages
		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		DBServiceImpl.store(dev1);
		for(int i=0; i<9; i++){
			Date msgDate = new Date(now.getTime() + i*100);
			DataMessage mess = new DataMessage(msgDate, "message"+i, 1);
			DBServiceImpl.store(mess);

			re.reason();
			nots = db.getUnfixedNotifications();
			assertEquals(0, nots.size());
		}

		Date msgDate = new Date(now.getTime() + 900);
		DataMessage mess = new DataMessage(msgDate, "message9", 1);
		DBServiceImpl.store(mess);

		re.reason();

		nots = db.getUnfixedNotifications();
		assertEquals(1, nots.size());
		assertEquals("test", nots.get(0).getMessage());
	}

	@Test
	public void testTwoSimpleRules() throws Exception{
		//store rule
		Rule sr = new Rule("simpleRule1",
				"DataMessage(limit = last).sourceMessage == 'message1'", 
				"SENDNOTIFICATION",
				"testRule1",
				1, "myrules", null);
		db.store(sr);
		sr = new Rule("simpleRule2",
				"DataMessage(limit = last).sourceMessage == 'message2'", 
				"SENDNOTIFICATION",
				"testRule2",
				1, "myrules", null);
		db.store(sr);

		RulesServiceImpl re = new RulesServiceImpl();

		ArrayList<Notification> nots;

		//store 10 messages
		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		db.getDB().store(dev1);

		Date msgDate = new Date(now.getTime());
		DataMessage mess = new DataMessage(msgDate, "message1", 1);
		db.getDB().store(mess);

		re.reason();

		nots = db.getUnfixedNotifications();
		assertEquals(1, nots.size());
		assertEquals("testRule1", nots.get(0).getMessage());
		db.getDB().delete(nots.get(0));

		msgDate = new Date(now.getTime() + 100);
		mess = new DataMessage(msgDate, "message2", 1);
		db.getDB().store(mess);

		re.reason();

		nots = db.getUnfixedNotifications();
		assertEquals(1, nots.size());
		assertEquals("testRule2", nots.get(0).getMessage());
	}

	@Test
	public void testTwoDataTypes() throws Exception{
		//store rule
		Rule sr = new Rule("simpleRule1",
				"DataMessage(limit = last).sourceMessage == 'message1'", 
				"SENDNOTIFICATION",
				"testRule1",
				1, "myrules", null);
		db.store(sr);
		sr = new Rule("simpleRule2",
				"ExtendedDataMessage(limit = last).extendedMessage == 'message2'", 
				"SENDNOTIFICATION",
				"testRule2",
				1, "myrules", null);
		db.store(sr);

		RulesServiceImpl re = new RulesServiceImpl();

		ArrayList<Notification> nots;

		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		db.getDB().store(dev1);

		Date msgDate = new Date(now.getTime());
		DataMessage mess = new DataMessage(msgDate, "message1", 1);
		db.getDB().store(mess);

		re.reason();

		nots = db.getUnfixedNotifications();
		assertEquals(1, nots.size());
		assertEquals("testRule1", nots.get(0).getMessage());
		db.getDB().delete(nots.get(0));

		msgDate = new Date(now.getTime() + 100);
		ExtendedDataMessage ed = new ExtendedDataMessage(msgDate, "src", 1, "message2",
				null, null);
		mess = new DataMessage(msgDate, "message2", 1);
		db.getDB().store(ed);

		re.reason();

		nots = db.getUnfixedNotifications();
		assertEquals(1, nots.size());
		assertEquals("testRule2", nots.get(0).getMessage());
	}

	@Test
	public void testTwoTypesInOneRule() throws Exception{
		//store rule
		Rule sr = new Rule("rule",
				"DataMessage(limit = last).sourceAddress == 1 &&"
						+ " ExtendedDataMessage( limit = last ).extendedMessage == 'message'", 
						"SENDNOTIFICATION",
						"testRule",
						1, "myrules", null);
		db.store(sr);

		RulesServiceImpl re = new RulesServiceImpl();

		ArrayList<Notification> nots;

		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		db.getDB().store(dev1);

		ExtendedDataMessage ed = new ExtendedDataMessage(new Date(now.getTime() + 100), "src", 1, "message",
				null, null);
		db.getDB().store(ed);

		re.reason();

		nots = db.getUnfixedNotifications();
		assertEquals(1, nots.size());
	}

	@Test
	public void testRemove() throws Exception{
		RulesServiceImpl re = new RulesServiceImpl();

		Rule sr = new Rule("rule",
				"DataMessage(limit = last).sourceAddress == 1 &&"
						+ " ExtendedDataMessage( limit = last ).extendedMessage == 'message'", 
						"SENDNOTIFICATION",
						"testRule",
						1, "myrules", null);
		re.saveRule(sr);

		assertEquals(sr.getFullyQualifiedName(), re.getRules().get(0).getFullyQualifiedName());

		re.removeRule(sr);

		assertEquals(0, re.getRules().size());
	}

	@Test
	public void testComplexQuery() throws Exception{
		RulesServiceImpl re = new RulesServiceImpl();

		Rule r = new Rule("simpleRule",
				"ExtendedDataMessage(limit = last, extendedMessage = 'message').sourceAddress > 3", 
				"SENDNOTIFICATION",
				"testRule",
				1, "myrules", null);

		re.saveRule(r);


		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		db.getDB().store(dev1);
		
		ExtendedDataMessage ed = new ExtendedDataMessage(now, "src mess", 1, "xxx", null, null);
		db.getDB().store(ed);
		
		re.reason();

		ArrayList<Notification> nots =db.getUnfixedNotifications();
		assertEquals(0, nots.size());
		
		ed = new ExtendedDataMessage(new Date(now.getTime()+1000), "message", 1, "message", null, null);
		db.getDB().store(ed);
		
		re.reason();

		nots =db.getUnfixedNotifications();
		assertEquals(0, nots.size());

		ed = new ExtendedDataMessage(new Date(now.getTime()+2000), "message", 5, "message", null, null);
		db.getDB().store(ed);
		
		re.reason();

		nots =db.getUnfixedNotifications();
		assertEquals(1, nots.size());
	}
}
