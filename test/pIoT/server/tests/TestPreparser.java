package pIoT.server.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import pIoT.server.rules.PreParser;
import pIoT.server.rules.PreParser.PreParsed;


public class TestPreparser {

	@Test
	public void testSimplePreparsing(){
		List<PreParsed> res = PreParser.PreParse("MyData().num = 5");
		assertEquals(1, res.size());
		assertEquals("MyData", res.get(0).className);
		assertEquals("MyData()", res.get(0).string);
		assertEquals(0, res.get(0).constraints.size());
		
		res = PreParser.PreParse("MyData( field = 3).num = 5");
		assertEquals(1, res.size());
		assertEquals("MyData", res.get(0).className);
		assertEquals("MyData( field = 3)", res.get(0).string);
		assertEquals(1, res.get(0).constraints.size());
		assertEquals("=", res.get(0).constraints.get(0).operator);
		assertEquals("3", res.get(0).constraints.get(0).value);
		assertEquals("field", res.get(0).constraints.get(0).variableName);
		
		res = PreParser.PreParse("MyData( field <= 3).num = 5");
		assertEquals(1, res.size());
		assertEquals("MyData", res.get(0).className);
		assertEquals(1, res.get(0).constraints.size());
		assertEquals("<=", res.get(0).constraints.get(0).operator);
		assertEquals("3", res.get(0).constraints.get(0).value);
		assertEquals("field", res.get(0).constraints.get(0).variableName);
		
		res = PreParser.PreParse("MyData( field = 3, bool < true).num = 5");
		assertEquals(1, res.size());
		assertEquals("MyData", res.get(0).className);
		assertEquals(2, res.get(0).constraints.size());
		assertEquals("=", res.get(0).constraints.get(0).operator);
		assertEquals("3", res.get(0).constraints.get(0).value);
		assertEquals("field", res.get(0).constraints.get(0).variableName);
		assertEquals("<", res.get(0).constraints.get(1).operator);
		assertEquals("true", res.get(0).constraints.get(1).value);
		assertEquals("bool", res.get(0).constraints.get(1).variableName);
		
		res = PreParser.PreParse("MyData().num = 5 AnotherData( ) here is");
		assertEquals(2, res.size());
		assertEquals("MyData", res.get(0).className);
		assertEquals("MyData()", res.get(0).string);
		assertEquals("AnotherData", res.get(1).className);
		assertEquals("AnotherData( )", res.get(1).string);
		
		res = PreParser.PreParse("MyData( string = 'astring').num = 5");
		assertEquals(1, res.size());
		assertEquals("MyData", res.get(0).className);
		assertEquals(1, res.get(0).constraints.size());
		assertEquals("=", res.get(0).constraints.get(0).operator);
		assertEquals("'astring'", res.get(0).constraints.get(0).value);
		assertEquals("string", res.get(0).constraints.get(0).variableName);
		
		 
		res = PreParser.PreParse("ExtendedDataMessage( limit = last , extendedMessage = 'message' ).sourceAddress > 3");
		assertEquals(1, res.size());
		assertEquals("ExtendedDataMessage", res.get(0).className);
		assertEquals(2, res.get(0).constraints.size());
		assertEquals("=", res.get(0).constraints.get(0).operator);
		assertEquals("last", res.get(0).constraints.get(0).value);
		assertEquals("limit", res.get(0).constraints.get(0).variableName);
		assertEquals("=", res.get(0).constraints.get(1).operator);
		assertEquals("'message'", res.get(0).constraints.get(1).value);
		assertEquals("extendedMessage", res.get(0).constraints.get(1).variableName);
	}

}
