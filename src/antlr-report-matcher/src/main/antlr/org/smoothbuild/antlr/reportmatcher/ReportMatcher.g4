grammar ReportMatcher;

@ header
{
package org.smoothbuild.antlr.reportmatcher;
}
matcher
   : expression EOF
   ;

expression
   : < assoc = right > expression '&' expression # And
   | < assoc = right > expression '|' expression # Or
   | '(' expression ')' # Brackets
   | MATCHER_NAME # MatcherName
   | LABEL_PATTERN # LabelPattern
   ;

MATCHER_NAME
   : [a-zA-Z]+
   ;

LABEL_PATTERN
   : (':' [a-zA-Z*]+)+
   ;

WS
   : [ \t\n\r]+ -> skip
   ;

