grammar Smooth;

module: ( struct | func )* EOF ;
struct: name '{' fieldList? '}' ;
fieldList: field ( ',' field )* ','? ;
field: type name ;
func: type? name ( '(' paramList? ')' )? ('=' pipe)? ';' ;
paramList: param ( ',' param )* ','? ;
param: type name ( '=' expr )? ;
pipe: expr ( p+='|' call )* ;
expr : expr accessor | call | STRING | array ;
call: name ( p='(' argList? ')' )? ;
argList: arg ( ',' arg )* ','? ;
arg: ( name '=' )? expr ;
array: '[' ( expr (',' expr)* (',')? )?  ']' ;
accessor: '.' name ;
type: nonArrayType | arrayType ;
nonArrayType: IDENTIFIER ;
arrayType: '[' type ']' ;
name: IDENTIFIER ;

IDENTIFIER: LETTER ( LETTER | NON_LETTER )* ;
STRING: '"' (ESC | ~('\r' | '\n'))*? '"' ;

fragment ESC : '\\"' | '\\\\' ;
fragment LETTER: 'a'..'z' | 'A'..'Z' ;
fragment NON_LETTER: '0'..'9' | '_' ;

COMMENT: '#'  ~( '\r' | '\n' )* -> skip ;
WS : [ \t\n\r]+ -> skip ;

