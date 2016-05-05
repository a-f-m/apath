* [Install](#Install)
* [Quick Tour](#Quick Tour)
   * [How to use it with JSON](#How to use it with JSON)
   * [How to use it with XML (and be aligned with JSON)](#How to use it with XML)
* [Predefined Steps](#Predefined Steps)
* [Customize](#Customize)
   * [Building your own wrappers](#Building your own wrappers)
   * [Building your own steps](#Building your own steps)
* [Concepts](#Concepts)
* [Performance](#Performance)

## Install<a name="Install"></a>

Download the project and run maven:

~~~maven
mvn package
~~~

or download the pre-built jars (in the project root):

~~~
apath-0.9.0.jar, apath-0.9.0-sources.jar
~~~

Dependencies: see POM.xml


## Quick Tour<a name="Quick Tour"></a>

### How to use it with JSON<a name="How to use it with JSON"></a>

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

**Set-up.** (1) parse JSON - (2) Wrapper for JSONSmart - (3) apath processor:

~~~java
// net.minidev.json.JSONObject

JSONObject jo = (JSONObject) JSONValue.parse(new FileReader("books.json")); //(1)

StepBuilder sb = new JsonSmartStepBuilder(); //(2)
PathProcessor processor = new PathProcessor(); //(3)
~~~

**Select.** (1) create a path - (2) select by path with the processor:

~~~java
Path path = new Path(sb.childrenByName("root"),
		sb.childrenByName("store"),
		sb.childrenByName("book"),
		sb.allChildren(),
		sb.childrenByName("author"),
		sb.stringValue()); //(1)

List<Object> results = processor.selectAll(jo, path); //(2)
// ~> [Nigel Rees, Evelyn Waugh]
~~~

**Be more compact.** Path object equivalent to the above one:

~~~java
SimplePathBuilder pb = new SimplePathBuilder(sb);

Path path = pb.buildPathFromTerms("root", "store", "book", "*", "author", "string()");
~~~

Note that path builders for concrete languages like JSONPath or XPath (subsets) could be developed.

### How to use it with XML (and be aligned with JSON)<a name="How to use it with XML"></a>

The corresponding XML :

~~~xml
<root>
	<store>
		<book>
			<category>reference</category>
			<author>Nigel Rees</author>
			<title>Sayings of the Century</title>
			<price>8.95</price>
		</book>
		<book>
			<category>fiction</category>
			<author>Evelyn Waugh</author>
			<title>Sword of Honour</title>
			<price>12.99</price>
		</book>
		<bicycle>
			<color>red</color>
			<price>19.95</price>
		</bicycle>
	</store>
	<expensive>10</expensive>
</root>
~~~

**Use JDK as usual.**

~~~java
DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
Document doc = builder.parse(new FileInputStream("books.xml"));
~~~

**Select.** (1) use the wrapper for JDK-DOM - (2) no '*' after <code>book</code> is needed (~XPath):

~~~java
sb = new JdkDomStepBuilder(); //(1)
pb = new SimplePathBuilder(sb);

results = processor.selectAll(
		doc, pb.buildPathFromTerms("root", "store", "book", "author", "string()")); //(2)
~~~

**Align with JSON**. (1) tell the JSON wrapper that you don't want arrays as first class before name selection:

~~~java
boolean skipArraysAtNameSelection = true;
StepBuilder sb = new JsonSmartStepBuilder(skipArraysAtNameSelection);
~~~

Now you can use the same path for JSON too.


## Predefined Steps<a name="Predefined Steps"></a>

Comparison with JSONPath and XPath:

| apath step                       | JSONPath           | XPath  |
| -------------------------------- |-------------------|-----|
| childrenByName(String *name*)      | *name*<br> <em>\*.name</em> **(1)** | *name* |
| childByIndex(int i)      | [i]      | [i]  |
| allChildren() | *      |    * |
| descendants() | ..      |    // |

**(1)** if the context is an JSON array and `skipArraysAtNameSelection` is true.

## Customize<a name="Customize"></a>

### Building your own wrappers<a name="Building your own wrappers"></a>

... coming soon ...

#### How to build a hierarchical view over java objects<a name="How to build a hierarchical view over java objects"></a>

... coming soon ...

### Building your own steps<a name="Building your own steps"></a>

... coming soon ...

## Concepts<a name="Concepts"></a>

* *apath* is a *small* (~50K) java library for selecting objects via simple, basic **path** expressions. One application is to access hierarchical structures like JSON, XML, S-Expressions, or others. Nevertheless, because only a *view* of the underlying data is defined, arbitrary structures can be handled as hierarchies.

* *apath* paths consists of *steps*, following the principles of <a href="https://www.w3.org/TR/2014/REC-xpath-30-20140408">XPath</a> and <a href="http://goessner.net/articles/JsonPath">JSONPath</a>, and offers a few basic, predefined *step constructors*. Although steps have intended semantics, users are free to give them own semantics in the context of their application. For instance, one implementation of the **a**bstract *childrenByName(name)*-step over JSON could be to skip the JSON-Array object (if it is the object to which the step is applied) as first class and to apply the step to the array items directly. This makes it possible to use the same path for selecting objects from JSON and XML, assuming some conventions for JSON structures. 

* One of the further conceptual goals was to allow for programmatically defining new custom step constructors. For example, if you want to access a typical department/employee structure, you can define a *members(name, salary)* step constructor that retrieves department members accordingly. Alternatively, *apath* provides filters to get members in the classic way. Note that steps are not intended to be orthogonal, for reasons of textual expressiveness. 

* *apath* is "open" in both directions, i.e., as sketched above, in the layer below it arbitrary structures can be made ready for selection. In the layer above it, arbitrary concrete syntax parsers could be defined, that builds paths and steps accordingly. One scenario could be the implementation of XPath- or JSONPath- (subset) parsers.

* The library is equipped with step constructors for <a href="https://github.com/netplex/json-smart-v2">json-smart</a> and jdk-xml-dom. Currently, no optimizations are performed. A [performance](#Performance) evaluation below compare *apath* with <a href="https://github.com/jayway/JsonPath">jayway</a>, <a href="https://xml.apache.org/xalan-j/">xalan</a> and <a href="http://saxon.sourceforge.net/">saxon</a>.



**Restrictions.** The library is intended for use with in-memory JSON objects, XML Dom's, or others, although it is possible to access persistence systems. But so far it is unsuited in this scenario due to the fact that *apath* iterate over solutions step by step and does not work with query plans incorporating underlying index structures (in contrast to elaborated XPath engines).    


## Performance<a name="Performance"></a>

... coming soon ...


## Scenarios

- views

tbd

## Long term goals

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

## Design notes:

no delegation (to avoid memory overhead)

no inheritance (to handle objects of final classes)

programmatic path construction (to not enforce exclusive use of an external concrete syntax like XPath)

Attention: initial version has no api doc

