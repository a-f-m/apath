# apath

* *apath* is a *small* java library for selecting objects via simple, basic **path** expressions. One application is to access hierarchical structures like JSON, XML, S-Expressions, or others. Nevertheless, because only a *view* of the underlying data is defined, arbitrary structures can be handled as hierarchies.

* *apath* paths consists of *steps*, following the principles of <a href="https://www.w3.org/TR/2014/REC-xpath-30-20140408">XPath</a> and <a href="http://goessner.net/articles/JsonPath">JSONPath</a>, and offers a few basic, predefined *step constructors*. Although steps have intended semantics, users are free to give them own semantics in the context of their application. For instance, one implementation of the **a**bstract *childrenByName(name)*-step over JSON could be to skip the JSON-Array object (if it is the value of property *name*) as first class and to return the array items immediately. This makes it possible to use the same path for selecting objects from JSON and XML, assuming some conventions for JSON structures. 

* One of the further conceptual goals was to allow for programmatically defining new custom step constructors. For example, if you want to access a typical department/employee structure, you can define a *members(name, salary)* step constructor that retrieves department members accordingly. Alternatively, *apath* provides filters to get members in the classic way. Note that steps are not intended to be orthogonal, for reasons of textual expressiveness. 

* *apath* is "open" in both directions, i.e., as sketched above, in the layer below it arbitrary structures can be made ready for selection. In the layer above it, arbitrary concrete syntax parsers could be defined, that builds paths and steps accordingly. One scenario could be the implementation of XPath- or JSONPath- (subset) parsers.

* The library is equipped with step constructors for <a href="https://github.com/netplex/json-smart-v2">json-smart</a> and jdk-xml-dom. Currently, no optimizations are performed. Some [benchmarks](#bench) below compare *apath* with <a href="https://github.com/jayway/JsonPath">jayway</a>, <a href="https://xml.apache.org/xalan-j/">xalan</a> and <a href="http://saxon.sourceforge.net/">saxon</a>.



**Restrictions.** The library is intended for use with in-memory JSON objects, XML Dom's, or others, although it is possible to access persistence systems. But so far it is unsuited in this scenario due to the fact that *apath* iterate over solutions step by step and does not work with query plans incorporating underlying index structures (in contrast to elaborated XPath engines).    





# Very Quick Tour

## How to use it with JSON

The obligatory book snippet (borrowed from <a href="http://goessner.net/articles/JsonPath">JSONPath</a>):

~~~json
{
	"root": {
	    "store": {
	        "book": [
	            {
	                "category": "reference",
	                "author": "Nigel Rees",
	                "title": "Sayings of the Century",
	                "price": 8.95
	            },
	            {
	                "category": "fiction",
	                "author": "Evelyn Waugh",
	                "title": "Sword of Honour",
	                "price": 12.99
	            }
	        ],
	        "bicycle": {
	            "color": "red",
	            "price": 19.95
	        }
	    },
	    "expensive": 10
	}
}
~~~

The following snippet shows the set-up. First, the JSON	file is parsed (1). Then the step builder (2) and the *apath* processor is created (3). Here we use the step builder for JSONSmart.

~~~json
// net.minidev.json.JSONObject

JSONObject jo = (JSONObject) JSONValue.parse(new FileReader("books.json")); //(1)

StepBuilder sb = new JsonSmartStepBuilder(); //(2)
PathProcessor processor = new PathProcessor(); //(3)
~~~

To select all book authors, an appropriate path with its steps is created (1) in the following snippet. The fourth step is the selection of all children (a '*' in JSONPath and XPath)

~~~json
Path path = new Path(
		sb.childrenByName("root"),
		sb.childrenByName("store"),
		sb.childrenByName("book"),
		sb.allChildren(),
		sb.childrenByName("author")); //(1)

List<Object> results = processor.selectAll(jo, path); //(2)
~~~

Then all authors are retrieved (2). Note that <code>results</code> contains a list of objects of the underlying structure, in our case (JSONSmart) java string objects.

To get all prices, one can use the *descendants* step:

~~~json
List<Object> results = 
	processor.selectAll(
		jo,
		new Path(sb.childrenByName("root"),
				 sb.childrenByName("store"),
				 sb.descendants(),
				 sb.childrenByName("price")));

System.out.println(results); // ~> [19.95, 8.95, 12.99]
~~~

To exemplify the composability of *apath* the last selection could be split into two statements:

~~~json
JSONObject store = (JSONObject) processor
		.selectAll(jo, new Path(sb.childrenByName("root"), sb.childrenByName("store"))).get(0);

results = processor.selectAll(store, new Path(sb.descendants(), sb.childrenByName("price")));
~~~

## How to be more compact

Although *apath* shall not be *yet another* concrete path language, the library offers a simple path builder for better readability, especially for users that do not aim at building extensions (e.g. own step constructors). The following path objects are equivalent to the above ones.

~~~json
SimplePathBuilder pb = new SimplePathBuilder(sb);

Path path = pb.buildPathFromTerms("root", "store", "book", "*", "author");
...
path = pb.buildPathFromTerms("root", "store", "..", "price");
~~~

where '*' and '..' stand for <code>allChildren()</code> and <code>descendants()</code>, respectively.

Note that, as described in the intro, path builders for concrete languages like JSONPath or XPath (subsets) could be developed.

## How to use predicates

... coming soon ...

## How to use it with XML

... coming soon ...

## How to align JSON and XML

... coming soon ...

# Step Builders

... coming soon ...

## How to build a hierarchical view over java objects

... coming soon ...

# <a name="bench"></a> Benchmarks

... coming soon ...

# Scenarios

- views

tbd

# Long term goals

caching

learning

actions

matching (unif)

step branches (or sideway steps)

enforce path existence (path-**a**gnostic)

~~~javascript
lalala
~~~

&bdquo;&ldquo;

# Design notes:

no delegation (to avoid memory overhead)

no inheritance (to handle objects of final classes)

programmatic path construction (to not enforce exclusive use of an external concrete syntax like XPath)

Attention: initial version has no api doc