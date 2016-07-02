package org.afm.apath.core;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;

import org.afm.apath.builders.JsonSmartStepBuilder;
import org.afm.apath.builders.SimplePathBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class PathProcessorTest extends PathProcessor {
	
	private JSONObject jo;
	
	private PathProcessor processor;

	@Before
	public void setUp() throws Exception {
		jo = 	(JSONObject) JSONValue
				.parse(new FileReader("src/test/resources/org/afm/apath/core/books.json"));
		processor = new PathProcessor();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBasics() {
		
		JsonSmartStepBuilder sb = new JsonSmartStepBuilder();
		SimplePathBuilder pb = new SimplePathBuilder(sb);
		
		//--
		
		Path path = pb.buildPathFromTerms("root", "store", "book", "*", "author");
		
		assertEquals("[Nigel Rees, Evelyn Waugh]", processor.selectAll(jo, path).toString());
		
		//---
		
		sb.setSkipArrays(true);
		
		path = pb.buildPathFromTerms("root", "store", "book", "author");
		
		assertEquals("[Nigel Rees, Evelyn Waugh]", processor.selectAll(jo, path).toString());
		
		//--
		
//		sb.setSkipArrays(false);
		
		path = pb.buildPathFromTerms("root", "store", "..", "price");
		
		assertEquals("[19.95, 8.95, 12.99]", processor.selectAll(jo, path).toString());
		
		//--
		
		path = pb.buildPathFromTerms("root", "store", "book", 1);
		
		assertEquals(
				"[{\"author\":\"Evelyn Waugh\",\"price\":12.99,\"category\":\"fiction\",\"title\":\"Sword of Honour\"}]",
				processor.selectAll(jo, path).toString());
		
		//--
		
		path = pb.buildPathFromTerms("root", "..", 1);
		
		assertEquals(
				"[{\"author\":\"Evelyn Waugh\",\"price\":12.99,\"category\":\"fiction\",\"title\":\"Sword of Honour\"}, 2]",
				processor.selectAll(jo, path).toString());

	}

}
