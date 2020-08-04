grammar Smooth;

module      : ( struct | func )* EOF ;
struct      : TYPE_IDENTIFIER '{' fieldList? '}' ;
fieldList   : field ( ',' field )* ','? ;
field       : type name ;
func        : type? name ( '(' paramList? ')' )? ('=' expr)? ';' ;
paramList   : param ( ',' param )* ','? ;
param       : type name ( '=' expr )? ;
expr        : nonPipeExpr ( p+='|' call )* ;
nonPipeExpr : nonPipeExpr accessor
            | call
            | STRING
            | BLOB
            | array
            ;
call        : name ( p='(' argList? ')' )? ;
argList     : arg ( ',' arg )* ','? ;
arg         : ( name '=' )? expr ;
array       : '[' ( expr (',' expr)* (',')? )?  ']' ;
accessor    : '.' name ;
type        : TYPE_IDENTIFIER      # typeIdentifier
            | '[' type ']'         # arrayType
            ;
name        : IDENTIFIER ;

IDENTIFIER        : SMALL_LETTER ( IDENTIFIER_CHAR )* ;
TYPE_IDENTIFIER   : LARGE_LETTER ( IDENTIFIER_CHAR )* ;
STRING            : '"' (ESC | ~('\r' | '\n'))*? '"' ;
BLOB              : '0x' HEX_DIGIT* ;

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

