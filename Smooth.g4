grammar Smooth;

module: function* ;
function: functionName ':' pipeExpression ';' ;
pipeExpression: expression ( '|' functionCall )* ;
expression : functionCall | STRING ;
functionCall: functionName ( '(' argList? ')' )? ;
argList: arg ( ',' arg )* ;
arg: paramName '=' expression ; 


functionName: IDENTIFIER ;
paramName: IDENTIFIER ;


IDENTIFIER: LETTER ( LETTER | DIGIT )* ;
STRING: '"' (ESC|.)*? '"' ;

fragment ESC : '\\"' | '\\\\' ;
fragment LETTER: 'a'..'z' | 'A'..'Z' | '_' ;
fragment DIGIT: '0'..'9' ;

