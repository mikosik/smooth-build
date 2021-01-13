grammar Smooth;

module      : ( struct | value | func )* EOF ;
struct      : TNAME '{' fieldList? '}' ;
fieldList   : field ( ',' field )* ','? ;
field       : type NAME ;
value       : type? NAME ('=' expr)? ';' ;
func        : type? NAME '(' paramList? ')' ('=' expr)? ';' ;
paramList   : param ( ',' param )* ','? ;
param       : type NAME ( '=' expr )? ;
expr        : nonPipeExpr ( p+='|' call )* ;
nonPipeExpr : nonPipeExpr fieldRead
            | call
            | NAME
            | STRING
            | BLOB
            | array
            ;
call        : NAME p='(' argList? ')' ;
argList     : arg ( ',' arg )* ','? ;
arg         : ( NAME '=' )? expr ;
array       : '[' ( expr (',' expr)* (',')? )?  ']' ;
fieldRead   : '.' NAME ;
type        : TNAME                        # typeName
            | '[' type ']'                 # arrayType
            | type '(' typeList ')'        # functionType
            ;

typeList    : ( type (',' type)* ','? )? ;

NAME        : SMALL_LETTER ( IDENTIFIER_CHAR )* ;
TNAME       : LARGE_LETTER ( IDENTIFIER_CHAR )* ;
STRING      : '"' (ESC | ~('\r' | '\n'))*? '"' ;
BLOB        : '0x' HEX_DIGIT* ;

fragment ESC              : '\\"'
                          | '\\\\'
                          ;
fragment IDENTIFIER_CHAR  : SMALL_LETTER
                          | LARGE_LETTER
                          | NON_LETTER
                          ;
fragment SMALL_LETTER     : 'a'..'z' ;
fragment LARGE_LETTER     : 'A'..'Z' ;
fragment NON_LETTER       : '0'..'9'
                          | '_'
                          ;
fragment HEX_DIGIT        : '0'..'9'
                          | 'A'..'F'
                          | 'a'..'f'
                          ;

COMMENT                   : '#'  ~( '\r' | '\n' )* -> skip ;
WS                        : [ \t\n\r]+ -> skip ;

