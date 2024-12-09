grammar SmoothAntlr;

@ header
{
package org.smoothbuild.antlr.lang;
}
module
   : (struct | namedFunc | namedValue)* EOF
   ;

struct
   : NAME '{' itemList '}'
   ;

namedFunc
   : annotation? type? NAME '(' itemList ')' ('=' pipe)? ';'
   ;

namedValue
   : annotation? type? NAME ('=' pipe)? ';'
   ;

itemList
   : (item (',' item)* ','?)?
   ;

item
   : type NAME ('=' expr)?
   ;

annotation
   : '@' NAME '(' STRING ')'
   ;

pipe
   : expr (p += '>' expr)*
   ;

expr
   : chain
   | lambda
   ;

chain
   : chainHead (chainPart)*
   ;

chainHead
   : NAME
   | array
   | parens
   | BLOB
   | INT
   | STRING
   ;

chainPart
   : argList
   | select
   ;

parens
   : '(' pipe ')'
   ;

argList
   : '(' (arg (',' arg)* ','?)? ')'
   ;

arg
   : (NAME '=')? expr
   ;

select
   : '.' NAME
   ;

array
   : '[' (expr (',' expr)* (',')?)? ']'
   ;

lambda
   : '(' itemList ')' '->' expr
   ;

type
   : NAME # typeName
   | '[' type ']' # arrayType
   | '(' (type (',' type)*)? ')' '->' type # funcType
   ;

NAME
   : SHORT_NAME (':' SHORT_NAME)*
   ;

fragment SHORT_NAME
   : (LETTER | '_') (LETTER | '_' | DIGIT)*
   ;

INT
   : '-'? DIGIT+
   ;

BLOB
   : '0x' HEX_DIGIT*
   ;

STRING
   : '"' (ESC | ~ ('\r' | '\n'))*? '"'
   ;

fragment ESC
   : '\\"'
   | '\\\\'
   ;

fragment LETTER
   : 'a' .. 'z'
   | 'A' .. 'Z'
   ;

fragment HEX_DIGIT
   : DIGIT
   | 'A' .. 'F'
   | 'a' .. 'f'
   ;

fragment DIGIT
   : '0' .. '9'
   ;

COMMENT
   : '#' ~ ('\r' | '\n')* -> skip
   ;

WS
   : [ \t\n\r]+ -> skip
   ;

