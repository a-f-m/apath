* [Headline](#Headline)
* [Install](#Install)
* [Quick Tour](#Quick Tour)
   * [How to use it with JSON](#How to use it with JSON)
   * [How to use it with XML (and be aligned with JSON)](#How to use it with XML)
   * [How to use predicates](#How to use predicates)
   * [Predefined Steps](#Predefined Steps)
* [Customize](#Customize)
   * [Building your own wrappers](#Building your own wrappers)
   * [Building views with your own steps](#Building views with your own steps)
* [Concepts](#Concepts)
* [Performance](#Performance)

## Headline<a name="Headline"></a>

*apath* is a *small* (~50K) java library for selecting objects via simple, basic **path** expressions (see [Concepts](#Concepts)). You can use it to

* **retrieve** JSON or XML **nodes** by unified path expressions, or

* **wrap** arbitrary **structures** and made them ready for path selection, or

* define own steps integrated in paths to provide **custom views**.



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

The obligatory book snippet <a name="bsn"></a> (borrowed from <a href="http://goessner.net/articles/JsonPath">JSONPath</a>):

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

**Align with JSON**. (1) tell the JSON wrapper that you don't want arrays as first class:

~~~java
boolean skipArrays = true;
StepBuilder sb = new JsonSmartStepBuilder(skipArrays);
~~~

Now you can use the same path for JSON too.

### How to use predicates<a name="How to use predicates"></a>

... coming soon ...

### Predefined Steps<a name="Predefined Steps"></a>

Comparison with JSONPath and XPath:

| apath step                       | JSONPath           | XPath  |
| -------------------------------- |-------------------|-----|
| childrenByName(String *name*)      | *name*<br> <em>\*.name</em> **(1)** | *name* |
| childByIndex(int i)      | [i]      | [i]  |
| allChildren() | *      |    * |
| descendants() | ..      |    // |

**(1)** if the context is an JSON array and `skipArrays` is true.

## Customize<a name="Customize"></a>


### Building your own wrappers<a name="Building your own wrappers"></a>

Wrapping objects in *apath* is done by implementing so-called *step builders* as an extension of [org.afm.apath.core.StepBuilder](https://github.com/a-f-m/apath/blob/master/src/main/java/org/afm/apath/core/StepBuilder.java). One example is [org.afm.apath.builders.JsonSmartStepBuilder](https://github.com/a-f-m/apath/blob/master/src/main/java/org/afm/apath/builders/JsonSmartStepBuilder.java).

A typical usage scenario in form of a class diagram is shown [here](https://github.com/a-f-m/apath/blob/master/doc/classes.png).


Note that *apath* aims at maximal loose coupling of the builder and the underlying structure. Therefore no extra wrapper classes for underlying classes (e.g. JSONObject or JSONArray) are created by delegation or inheritance, also to gain better performance. As a consequence, builder elements mainly use the *instanceOf*-operator and are most generally typed (using *Object* or *Iterator<Object\>* for step results). It is similar to the usual style in untyped languages (e.g. JavaScript). **Warning**: due to that searching for errors could be expensive (as usual in untyped languages).

Here we want to re-develop the *JsonSmartStepBuilder* to exemplify the process.

**The skeleton**. Extension of [org.afm.apath.core.StepBuilder](https://github.com/a-f-m/apath/blob/master/src/main/java/org/afm/apath/core/StepBuilder.java) (e.g. generated with Eclipse):

~~~java
public class JsonSmartStepBuilder extends StepBuilder {

	@Override
	public ChildrenByName childrenByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildByIndex childByIndex(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AllChildren allChildren() {
		// TODO Auto-generated method stub
		return null;
	}
}

~~~

**Extending** org.afm.apath.core.Step.ChildrenByName:

~~~java
class JsChildrenByName extends ChildrenByName {

	public JsChildrenByName(String name) {
		super(name);
	}
	
	/**
	 * Applies this step to a node.
	 * ...
	 */
	@Override
	public Object applyTo(Object node, Path enclosingPath, int currStepNo) {
		
		if (node instanceof JSONObject) {
			
			Object jo = ((JSONObject) node).get(name); //(1)
			if (jo != null) {
				return skipArrays && jo instanceof JSONArray ? 
					skipArray(jo, enclosingPath, currStepNo) : jo;
				//(2)
			}
		}
		return null; //(3)
	}
}
~~~

(1) - usual selection if node is an JSONObject

(2) - possibly skipping arrays or return the selection

(3) - `null` means that the step has no results

**Use it** in the `JsonSmartStepBuilder` builder above:

~~~java
@Override
public ChildrenByName childrenByName(String name) {

	return new JsChildrenByName(name);
}

~~~

The *ChildByIndex*-step is performed analogously. Note that results in the *AllChildren*-step are iterators:

~~~java
class JsAllChildren extends AllChildren {

	...

	@Override
	public Iterator<Object> applyTo(Object node, Path enclosingPath, int currStepNo) {

		if (node instanceof JSONObject) {
			return ((JSONObject) node).values().iterator();
			
		} else if (node instanceof JSONArray) {
			return ((JSONArray) node).iterator();
		}
		return Collections.emptyIterator();
	}
}
~~~

### Building views with your own steps<a name="Building views with your own steps"></a>

To exemplify views over JSON more adequate, we modify the above book [snippet](#bsn) such that it has an additional level according to the categories:

~~~json
...
	"book": {
	    "references": [
	        {
	            "author": "Nigel Rees",
	            "title": "Sayings of the Century",
	            "price": 8.95
	        }
	    ],
	    "fictions": [
	        {
	            "author": "Evelyn Waugh",
	            "title": "Sword of Honour",
	            "price": 12.99
	        }
	    ]
	},
...
~~~

Assume that we want to retrieve books with a special category and price without formulating a path equivalent to the JSON structure. Instead, we want to build an own step `books(category, priceThreshold)` to retrieve books according to a category and price threshold. This cann be seen as a classical view. Note that the implementation of this step requires knowledge of the custom structure given by the above snippet:.

**First, exending your step builder**, here the [org.afm.apath.builders.JsonSmartStepBuilder](https://github.com/a-f-m/apath/blob/master/src/main/java/org/afm/apath/builders/JsonSmartStepBuilder.java).

~~~java
public class MyStepBuilder extends JsonSmartStepBuilder {

	class Books implements Step {
		
		private double priceThreshold;
		private String category;
		
		public Books(double priceThreshold, String category) { //(1)
			this.priceThreshold = priceThreshold;
			this.category = category;
		}

		@Override
		public Iterator<Object> applyTo(Object node, Path enclosingPath, int currStepNo) {		
			//(2)
			// ... see below ... 
			return null;
		}
	}
	
	public Books books(double priceThreshold, String category) { //(3)
		return new Books(priceThreshold, category);
	}
}
~~~

(1) - Step construction.

(2) - The actual step evaluation (below).

(3) - Again, for convenience, the builder method to avoid direct instantiation at the caller.

The above snippet is the blueprint for introducing new steps. The **actual step evaluation** is implemented with the `applyTo`-method according to the book category and price threshold:

~~~java
@Override
public Iterator<Object> applyTo(Object node, Path enclosingPath, int currStepNo) {
	
	if (node instanceof JSONObject) {
		// note: no cast checks for readability and compactness
		JSONObject book = (JSONObject) ((JSONObject) node).get("book"); //(1)
		if (book != null) {
			JSONArray categBooks = (JSONArray) ((JSONObject) book).get(category); //(2)
			if (categBooks != null) {
				return new Filter<Object>(categBooks.iterator()) { //(3)
					protected boolean accept(Object x) {
						return (double) ((JSONObject) x).get("price") <= priceThreshold;
					}
				};
			}
		}
	}
	return null;
}
~~~

(1) - Get the book collection.

(2) - Get the books of the category.

(3) - Filter by price threshold. 

Note that the body of `applyTo` could be part of an extra method such that this functionality could be used in contexts where path processing is not relevant. Vice versa, existing methods could be used for customized path processing.

Remark: excessive casting has to be merely performed due to the JsonSmart-API.

**Finally, use your step** as usual, e.g., to retrieve fictions cheaper than 15.00 Euro:

~~~java
MyStepBuilder sb = new MyStepBuilder();
Path path = new Path(sb.descendants(), sb.books(15.0, "fictions"));

List<Object> l = new PathProcessor().selectAll(jo, path);
~~~

#### How to build a hierarchical view over java objects

... coming soon ...

## Concepts<a name="Concepts"></a>

* *apath* is a *small* (~50K) java library for selecting objects via simple, basic **path** expressions. One application is to access hierarchical structures like JSON, XML, S-Expressions, or others. Nevertheless, because only a *view* of the underlying data is defined, arbitrary structures can be handled as hierarchies.

* *apath* paths consists of *steps*, following the principles of <a href="https://www.w3.org/TR/2014/REC-xpath-30-20140408">XPath</a> and <a href="http://goessner.net/articles/JsonPath">JSONPath</a>, and offers a few basic, predefined *step constructors*. Although steps have intended semantics, users are free to give them own semantics in the context of their application. For instance, one implementation of the **a**bstract *childrenByName(name)*-step over JSON could be to skip the JSON-Array object (if it is the object to which the step is applied) as first class and to apply the step to the array items directly. This makes it possible to use the same path for selecting objects from JSON and XML, assuming some conventions for JSON structures. 

* One of the further conceptual goals was to allow for programmatically defining new custom step constructors. For example, if you want to access a typical department/employee structure, you can define a *members(name, salary)* step constructor that retrieves department members accordingly. Alternatively, *apath* provides filters to get members in the classic way. Note that steps are not intended to be orthogonal, for reasons of textual expressiveness. 

* *apath* is "open" in both directions, i.e., as sketched above, in the layer below it arbitrary structures can be made ready for selection. In the layer above it, arbitrary concrete syntax parsers could be defined, that builds paths and steps accordingly. One scenario could be the implementation of XPath- or JSONPath- (subset) parsers.

* The library is equipped with step constructors for <a href="https://github.com/netplex/json-smart-v2">json-smart</a> and jdk-xml-dom. Currently, no optimizations are performed. A [performance](#Performance) evaluation below compare *apath* with <a href="https://github.com/jayway/JsonPath">jayway</a>, jdk-XPath and <a href="http://saxon.sourceforge.net/">saxon</a>.



**Restrictions.** The library is intended for use with in-memory JSON objects, XML Dom's, or others, although it is possible to access persistence systems. But so far it is unsuited in this scenario due to the fact that *apath* iterate over solutions step by step and does not work with query plans incorporating underlying index structures (in contrast to elaborated XPath engines).    


## Performance<a name="Performance"></a>

The following table shows the times in % of `apath over JsonSmart`.


| Engine  | Children | Descendants | Chain |   |
|---------|----------|-------------|-------|---|
| apath over JsonSmart | 100 | 100 | 100 |  |
| apath over JdkDom | 250 | 295 | 495 | (*) | 
| JSONPath/jayway over JsonSmart | 158 | 134 | 219 |  |
| XPath/Saxon over SaxonDom | 45 | 12 | 162 | (\*\*) | 
| XPath/Jdk-XPath over JdkDom | 1520 | 479 | 11318 |  | 

Engines are designed by form `X over Y` where *X* states the language/engine and *Y* the underlying object structure. 

(*) A little surprise is the difference to `XPath over JdkDom`. It could be that `XPath over JdkDom` perform a huge overhead and does not use the underlying objects directly whenever possible.

(\*\*) As expected, *Children* and *Descendants* is faster with saxon (*Descendants* with at least than one order of magnitude), may be due to excessive use of index structures. Future work will focus on that issue. Interestingly, `apath over JsonSmart`-*Chain* is faster, may be due to less index overhead. 

