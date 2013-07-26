grammar Smooth;


functionDeclaration: functionSignature ':' functionBody ';' ;
functionSignature : functionName ;
functionBody: functionCall ( '|' functionCall )* ;
functionCall: functionName ( '(' functionArgumentList? ')' )? ;
functionArgumentList: functionArgument ( ',' functionArgument )* ;
functionArgument: functionParameterName '=' functionCall ; 


functionName: IDENTIFIER ;
functionParameterName: IDENTIFIER ;


IDENTIFIER: ('a'..'z') ('a'..'z' | 'A'..'Z' | '_') ;

