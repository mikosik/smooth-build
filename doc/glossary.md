
### Glossary of terms used in documentation and code

 - __actual type__ - Type created from polytype by replacing its type variables with some types.
 - __assignable__ - Value is assignable to different value if they have the same type
   or type of former is convertible to the type of latter.
 - __array type__ - Type that represents sequence of elements.
   Each element has the same type which is known as array's element type.
 - __base type__ - Unstructured type that is built into language.
   Base type cannot be declared by user.
   They include `Blob`, `Bool`, `Int`, `String`.
  - __composite type__ - type that can be constructed using base types and other composite types
  - __default argument__ - Value assigned to function parameter upon its declaration.
   Function can be called without specifying such parameter in which case default argument is used.
 - __monotype (monomorphic type)__ - Type that does not have free type variables.
 - __polytype (polymorphic type)__ - Type that has free type variables.
 - __pure / impure__ - Pure function is a native function which always returns same result for
   given arguments.
   Pure functions are always cached to disk.
   Impure functions are cached within a scope of a single build run.
 - __shadowing__ - Making some function inaccessible inside other function body
   by declaring latter with a parameter that has name equal to former function name.
 - __standard library__ - modules with types and functions provided by default
   by smooth installation.
 - __struct type__ - Type that is constructed by combining other types which are called its fields.
