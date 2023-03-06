grammar TaskMatcher;

@header {
package org.smoothbuild.antlr.taskmatcher;
}

matcher : expression EOF ;

expression : <assoc=right> expression '&' expression # And
           | <assoc=right> expression '|' expression # Or
           | '(' expression ')'                      # Brackets
           | MATCHER_NAME                            # MatcherName
           ;

MATCHER_NAME : [a-zA-Z]+ ;

WS : [ \t\n\r]+ -> skip ;

