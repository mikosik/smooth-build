grammar Smooth;

module:       ( struct | func )* EOF ;
struct:       TYPE_IDENTIFIER '{' fieldList? '}' ;
fieldList:    field ( ',' field )* ','? ;
field:        type name ;
func:         type? name ( '(' paramList? ')' )? ('=' pipe)? ';' ;
paramList:    param ( ',' param )* ','? ;
param:        type name ( '=' expr )? ;
pipe:         expr ( p+='|' call )* ;
expr:         expr accessor | call | STRING | array ;
call:         name ( p='(' argList? ')' )? ;
argList:      arg ( ',' arg )* ','? ;
arg:          ( name '=' )? pipe ;
array:        '[' ( expr (',' expr)* (',')? )?  ']' ;
accessor:     '.' name ;
type:         TYPE_IDENTIFIER | arrayType ;
arrayType:    '[' type ']' ;
name:         IDENTIFIER ;

IDENTIFIER:      SMALL_LETTER ( IDENTIFIER_CHAR )* ;
TYPE_IDENTIFIER: LARGE_LETTER ( IDENTIFIER_CHAR )* ;
STRING:          '"' (ESC | ~('\r' | '\n'))*? '"' ;

fragment ESC : '\\"' | '\\\\' ;
fragment IDENTIFIER_CHAR: SMALL_LETTER | LARGE_LETTER | NON_LETTER ;
fragment SMALL_LETTER: 'a'..'z' ;
fragment LARGE_LETTER: 'A'..'Z' ;
fragment NON_LETTER: '0'..'9' | '_' ;

COMMENT: '#'  ~( '\r' | '\n' )* -> skip ;
WS : [ \t\n\r]+ -> skip ;

