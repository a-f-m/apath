# apath

* *apath* is a *small* java library for selecting objects via simple, basic **path** expressions. One application is to access hierarchical structures like JSON, XML, S-Expressions, or others. Nevertheless, because only a *view* of the underlying data is defined, arbitrary structures can be handled as hierarchies.

* *apath* paths consists of *steps*, following the principles of <a href="https://www.w3.org/TR/2014/REC-xpath-30-20140408">XPath</a> and <a href="http://goessner.net/articles/JsonPath">JSONPath</a>, and offers a few basic, predefined *step constructors*. Although steps have intended semantics, users are free to give them own semantics in the context of their application. For instance, one implementation of the **a**bstract *childrenByName(name)*-step over JSON could be to skip the JSON-Array object (if it is the value of property *name*) as first class and to return the array items immediately. This makes it possible to use the same path for selecting objects from JSON and XML, assuming some conventions for JSON structures. 

* One of the further conceptual goals was to allow for programmatically defining new custom step constructors. For example, if you want to access a typical department/employee structure, you can define a *members(name, salary)* step constructor that retrieves department members accordingly. Alternatively, *apath* provides filters to get members in the classic way. Note that steps are not intended to be orthogonal, for reasons of textual expressiveness. 

* *apath* is "open" in both directions, i.e., as sketched above, in the layer below it arbitrary structures can be made ready for selection. In the layer above it, arbitrary concrete syntax parsers could be defined, that builds paths and steps accordingly. One scenario could be the implementation of XPath- or JSONPath- (subset) parsers.

* The library is equipped with step constructors for <a href="https://github.com/netplex/json-smart-v2">json-smart</a>, <a href="http://www.json.org/">json.org</a>, jdk-xml-dom, and <a href="http://www.jdom.org/news/index.html">jdom2</a>. Currently, no optimizations are performed. Some [benchmarks](#bench) below compare *apath* with <a href="https://github.com/jayway/JsonPath">jayway</a>, <a href="https://xml.apache.org/xalan-j/">xalan</a>, <a href="http://jaxen.org/">jaxen</a>, and <a href="http://saxon.sourceforge.net/">saxon</a>.



**Restrictions.** The library is intended for use with in-memory JSON objects, XML Dom's, or others, although it is possible to access persistence systems. But so far it is unsuited in this scenario due to the fact that *apath* iterate over solutions step by step and does not work with query plans incorporating underlying index structures (in contrast to elaborated XPath engines).    
## Very Quick Tour

tbd
