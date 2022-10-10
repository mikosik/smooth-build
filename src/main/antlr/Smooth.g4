grammar Smooth;

mod          : ( struct | function | value )* EOF ;
struct       : NAME '{' itemList '}' ;
function     : ann? type? NAME '(' itemList ')' ('=' expr)? ';' ;
value        : ann? type? NAME ('=' expr)? ';' ;
itemList     : ( item ( ',' item )* ','? )? ;
item         : type NAME ( '=' expr )? ;
ann          : '@' NAME '(' STRING ')' ;
expr         : exprHead ( p+='|' chainCall )* ;
exprHead     : chain | literal ;
literal      : array
             | INT
             | BLOB
             | STRING
             ;
chain        : NAME ( chainPart )* ;
chainCall    : NAME ( select )* argList ( chainPart )* ;
chainPart    : argList | select ;
argList      : '(' ( arg ( ',' arg )* ','? )? ')' ;
arg          : ( NAME '=' )? expr ;
array        : '[' ( expr (',' expr)* (',')? )?  ']' ;
select       : '.' NAME ;
type         : NAME                        # typeName
             | '[' type ']'                 # arrayT
             | type '(' typeList ')'        # funcT
             ;
typeList     : ( type (',' type)* ','? )? ;

NAME         : NON_DIGIT_CHAR NAME_CHAR* ;
INT          : '-'? DIGIT+ ;
BLOB         : '0x' HEX_DIGIT* ;
STRING       : '"' (ESC | ~('\r' | '\n'))*? '"' ;

fragment ESC              : '\\"'
                          | '\\\\'
                          ;
fragment NON_DIGIT_CHAR   : SMALL_LETTER
                          | LARGE_LETTER
                          | '_'
                          ;
fragment NAME_CHAR        : SMALL_LETTER
                          | LARGE_LETTER
                          | DIGIT
                          | '_'
                          ;
fragment SMALL_LETTER     : 'a'..'z' ;
fragment LARGE_LETTER     : 'A'..'Z' ;
fragment HEX_DIGIT        : DIGIT
                          | 'A'..'F'
                          | 'a'..'f'
                          ;
fragment DIGIT            : '0'..'9' ;

COMMENT                   : '#'  ~( '\r' | '\n' )* -> skip ;
WS                        : [ \t\n\r]+ -> skip ;

