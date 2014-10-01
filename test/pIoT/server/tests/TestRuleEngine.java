package pIoT.server.tests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Test;

import pIoT.server.rules.Engine;
import pIoT.shared.Rule;
import pIoT.shared.messages.DataMessage;

public class TestRuleEngine {

	@Test
	public void testSimpleRule1() throws Exception {
		Engine eng = new Engine();

		Rule r = new Rule("simperule",
				"input0.sourceAddress == 1",
				"testout", "test", 1, "myrules", null);
		eng.addRule(r);


		DataMessage mess = new DataMessage(Calendar.getInstance().getTime(),
				"message", 2);
		HashMap<String, Object> data = new HashMap<>();
		data.put("input0", mess);
		assertNull(eng.getBestOutcome(null, null, data));

		mess = new DataMessage(Calendar.getInstance().getTime(),
				"message", 1);
		data.put("input0", mess);
		assertEquals("testout", eng.getBestOutcome(null, null, data).getOutcome());
	}

	@Test
	public void testRemove() throws Exception {
		Engine eng = new Engine();

		Rule r = new Rule("simperule",
				"input0.sourceAddress == 1",
				"testout", "test", 1, "myrules", null);
		eng.addRule(r);

		DataMessage mess = new DataMessage(Calendar.getInstance().getTime(),
				"message", 1);
		HashMap<String, Object> data = new HashMap<>();
		data.put("input0", mess);
		assertEquals("testout", eng.getBestOutcome(null, null, data).getOutcome());

		eng.removeRule(r);
		mess = new DataMessage(Calendar.getInstance().getTime(),
				"message", 2);
		data = new HashMap<>();
		data.put("input0", mess);
		assertNull(	eng.getBestOutcome(null, null, data));
	}

	@Test
	public void testSimpleRule2() throws Exception {
		Engine eng = new Engine();

		Rule r = new Rule("simperule",
				"input0.sourceMessage == 'message2'",
				"testout", "test", 1, "myrules", null);
		eng.addRule(r);


		DataMessage mess = new DataMessage(Calendar.getInstance().getTime(),
				"message1", 2);
		HashMap<String, Object> data = new HashMap<>();
		data.put("input0", mess);
		assertNull(eng.getBestOutcome(null, null, data));

		mess = new DataMessage(Calendar.getInstance().getTime(),
				"message2", 2);
		data.put("input0", mess);
		eng.getBestOutcome(null, null, data);
		assertEquals("testout", eng.getBestOutcome(null, null, data).getOutcome());
	}

	@Test
	public void testDependingRules() throws Exception {
		Engine eng = new Engine();

		Rule r = new Rule("ruleAddr",
				"input0.sourceAddress == 1",
				"addrOut", "test", 1, "myrules", null);
		eng.addRule(r);
		r = new Rule("ruleTS",
				"input0.receivedTimestamp.time > 1000",
				"TSOut", "test", 1, "myrules", null);
		eng.addRule(r);
		r = new Rule("ruleMesg",
				"input0.sourceMessage == 'message' && #ruleAddr && #ruleTS",
				"msgOut", "test", 2, "myrules", null);
		eng.addRule(r);

		DataMessage mess = new DataMessage(Calendar.getInstance().getTime(),
				"message1", 2);
		HashMap<String, Object> data = new HashMap<>();
		data.put("input0", mess);
		assertEquals("TSOut",eng.getBestOutcome(null, null, data).getOutcome());

		mess = new DataMessage(Calendar.getInstance().getTime(),
				"message", 1);
		data.put("input0", mess);
		assertEquals("msgOut", eng.getBestOutcome(null, null, data).getOutcome());
	}

	@Test
	public void testSelectedRules() throws Exception {
		Engine eng = new Engine();

		Rule r = new Rule("ruleAddr",
				"input0.sourceAddress == 1",
				"addrOut", "test", 1, "myrules", null);
		eng.addRule(r);
		r = new Rule("ruleTS",
				"input0.receivedTimestamp.time > 1000",
				"TSOut", "test", 1, "myrules", null);
		eng.addRule(r);
		r = new Rule("ruleMesg",
				"input0.sourceMessage == 'message'",
				"msgOut", "test", 2, "myrules", null);
		eng.addRule(r);

		DataMessage mess = new DataMessage(Calendar.getInstance().getTime(),
				"message", 2);
		HashMap<String, Object> data = new HashMap<>();
		data.put("input0", mess);
		assertNull(eng.getBestOutcome(null, Arrays.asList(new String[]{"myrules.ruleAddr"}), data));

		mess = new DataMessage(Calendar.getInstance().getTime(),
				"message", 2);
		data.put("input0", mess);
		assertEquals("msgOut", eng.getBestOutcome(null, Arrays.asList(new String[]{"myrules.ruleAddr", "myrules.ruleTS" ,"myrules.ruleMesg"}), data).getOutcome());
	}
}
