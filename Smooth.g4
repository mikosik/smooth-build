grammar Smooth;

module: function* EOF;
function: functionName ':' pipe ';' ;
pipe: expression ( p+='|' call )* ;
expression : call | STRING ;
call: functionName ( '(' argList? ')' )? ;
argList: arg ( ',' arg )* ;
arg: paramName '=' expression ; 


functionName: IDENTIFIER ;
paramName: IDENTIFIER ;


IDENTIFIER: LETTER ( LETTER | DIGIT )* ;
STRING: '"' (ESC|.)*? '"' ;

fragment ESC : '\\"' | '\\\\' ;
fragment LETTER: 'a'..'z' | 'A'..'Z' | '_' ;
fragment DIGIT: '0'..'9' ;

WS : [ \t\n\r]+ -> skip ;
