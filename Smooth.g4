grammar Smooth;

module: function* EOF;
function: functionName ':' pipe ';' ;
pipe: expression ( p+='|' call )* ;
expression : call | STRING | set;
call: functionName ( '(' argList? ')' )? ;
argList: arg ( ',' arg )* ','? ;
arg: ( paramName '=' )? expression ; 
set: '[' ( setElem (',' setElem)* (',')? )?  ']' ;
setElem: call | STRING ;


functionName: IDENTIFIER ;
paramName: IDENTIFIER ;


IDENTIFIER: LETTER ( LETTER | NON_LETTER )* ;
STRING: '"' (ESC|.)*? '"' ;

fragment ESC : '\\"' | '\\\\' ;
fragment LETTER: 'a'..'z' | 'A'..'Z' ;
fragment NON_LETTER: '0'..'9' | '_' | '-' | '.' ;

COMMENT: '#' .*? '\n' -> skip ;
WS : [ \t\n\r]+ -> skip ;

