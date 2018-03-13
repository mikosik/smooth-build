
### Glossary of terms used in documentation and code

 - __assignable__ - Value is assignable to different value if they have the same type or type of former is convertible to the type of latter.
 - __argument auto-assignment__ - Process in which compiler assigns arguments from function call to actual parameters of called function. It is based on matching argument with parameter to which given argument is assignable. If there more than two different ways of doing such assignment then compilation fails. 
 - __array type__ - Type that represents sequence of elements. Each element has the same type which is known as array's element type.
 - __basic type__ - Type that is built into language. Basic type cannot be declared by user. Basic types include `String`, `Blob`.
 - __default value__ - Value assigned to function parameter upon its declaration. Function can be called without specifying such parameter in which case default value will be assigned to it.
 - __optional parameter__ - Function parameter that has default value. Call to a function does not have to provide argument for any optional parameter.
 - __platform API__ - Set of builtin functions provided by smooth platform.
 - __required parameter__ - Function parameter that does not have default value. Each call to a function must provide arguments for all its required parameters.
 - __shadowing__ - Making some function inaccessible inside other function body by declaring latter with parameter that has name equal to former function name.
 - __struct type__ - Type that is constructed by combining other types which are called its fields.
