grammar Smooth;

module      : ( struct | ref )* EOF ;
struct      : TNAME '{' fieldList? '}' ;
fieldList   : field ( ',' field )* ','? ;
field       : type NAME ;
ref         : nat? type? NAME paramList? ('=' expr)? ';' ;
nat         : '@Native(' STRING (',' (pure='PURE' | impure='IMPURE'))? ')' ;
paramList   : '(' ( param ( ',' param )* ','? )? ')' ;
param       : type NAME ( '=' expr )? ;
expr        : exprHead ( p+='|' chainCall )* ;
exprHead    : chain | literal ;
literal     : STRING
            | BLOB
            | array
            ;
chain       : NAME ( chainPart )* ;
chainCall   : NAME ( fieldRead )* argList ( chainPart )* ;
chainPart   : argList | fieldRead ;
argList     : '(' ( arg ( ',' arg )* ','? )? ')' ;
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

