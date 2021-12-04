grammar Smooth;

mod          : ( struct | eval )* EOF ;
struct       : TNAME '{' fieldList? '}' ;
fieldList    : field ( ',' field )* ','? ;
field        : type NAME ;
eval         : ann? type? NAME paramList? ('=' expr)? ';' ;
ann          : '@Native(' STRING (',' (pure='PURE' | impure='IMPURE'))? ')' ;
paramList    : '(' ( param ( ',' param )* ','? )? ')' ;
param        : type NAME ( '=' expr )? ;
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
type         : TNAME                        # typeName
             | '[' type ']'                 # arrayT
             | type '(' typeList ')'        # funcT
             ;
typeList     : ( type (',' type)* ','? )? ;

NAME         : SMALL_LETTER ( IDENTIFIER_CHAR )* ;
TNAME        : LARGE_LETTER ( IDENTIFIER_CHAR )* ;
INT          : '-'? DIGIT+ ;
BLOB         : '0x' HEX_DIGIT* ;
STRING       : '"' (ESC | ~('\r' | '\n'))*? '"' ;

fragment ESC              : '\\"'
                          | '\\\\'
                          ;
fragment IDENTIFIER_CHAR  : SMALL_LETTER
                          | LARGE_LETTER
                          | NON_LETTER
                          ;
fragment SMALL_LETTER     : 'a'..'z' ;
fragment LARGE_LETTER     : 'A'..'Z' ;
fragment NON_LETTER       : DIGIT
                          | '_'
                          ;
fragment HEX_DIGIT        : DIGIT
                          | 'A'..'F'
                          | 'a'..'f'
                          ;
fragment DIGIT            : '0'..'9' ;

COMMENT                   : '#'  ~( '\r' | '\n' )* -> skip ;
WS                        : [ \t\n\r]+ -> skip ;

