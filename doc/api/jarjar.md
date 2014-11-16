## jarjar

Repackages java library using [jarjar](https://code.google.com/p/jarjar/).

 * Blob __in__ - jar with java class files to be repackages.
 * String __rules__ - jarjar rules for repackaging, 
(see [jarjar rules](https://code.google.com/p/jarjar/wiki/CommandLineDocs#Rules_file_format))


Returns __Blob__ containing jar with repackaged classes.

### examples

Repackages classes in "jars/myJar.jar" file according to given rules.

```
myRules: "rule net.example.* org.repackaged-example.$@1" ;
repackaged.jar: file("jars/myJar.jar") | jarjar(rules=myRules);
```
