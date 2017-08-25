grammar Smooth;

module: func* EOF;
func: name ( '(' paramList? ')' )? '=' pipe ';' ;
paramList: param ( ',' param )* ','? ;
param: type name ;
pipe: expr ( p+='|' call )* ;
expr : call | STRING | array;
call: name '(' argList? ')' ;
argList: arg ( ',' arg )* ','? ;
arg: ( name '=' )? expr ; 
array: '[' ( expr (',' expr)* (',')? )?  ']' ;
type: basicType | arrayType ;
basicType: IDENTIFIER ;
arrayType: '[' type ']' ;
name: IDENTIFIER ;

IDENTIFIER: LETTER ( LETTER | NON_LETTER )* ;
STRING: '"' (ESC | ~('\r' | '\n'))*? '"' ;

fragment ESC : '\\"' | '\\\\' ;
fragment LETTER: 'a'..'z' | 'A'..'Z' ;
fragment NON_LETTER: '0'..'9' | '_' | '-' | '.' ;

COMMENT: '#'  ~( '\r' | '\n' )* -> skip ;
WS : [ \t\n\r]+ -> skip ;

