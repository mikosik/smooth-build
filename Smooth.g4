grammar Smooth;

module: function* EOF;
function: name ( '(' paramList? ')' )? '=' pipe ';' ;
paramList: param ( ',' param )* ','? ;
param: type name ;
pipe: expression ( p+='|' call )* ;
expression : call | STRING | array;
call: name ( '(' argList? ')' )? ;
argList: arg ( ',' arg )* ','? ;
arg: ( name '=' )? expression ; 
array: '[' ( expression (',' expression)* (',')? )?  ']' ;
type: basicType | arrayType ;
basicType: IDENTIFIER ;
arrayType: '[' type ']' ;
name: IDENTIFIER ;

IDENTIFIER: LETTER ( LETTER | NON_LETTER )* ;
STRING: '"' (ESC|.)*? '"' ;

fragment ESC : '\\"' | '\\\\' ;
fragment LETTER: 'a'..'z' | 'A'..'Z' ;
fragment NON_LETTER: '0'..'9' | '_' | '-' | '.' ;

COMMENT: '#'  ~( '\r' | '\n' )* -> skip ;
WS : [ \t\n\r]+ -> skip ;

