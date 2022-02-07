package org.smoothbuild.out.report;

import static org.smoothbuild.out.report.MatcherParser.parseMatcher;

import org.smoothbuild.antlr.taskmatcher.TaskMatcherBaseVisitor;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.AndContext;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.BracketsContext;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.MatcherContext;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.MatcherNameContext;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.OrContext;

import picocli.CommandLine.TypeConversionException;

public class MatcherCreator {
  public static TaskMatcher createMatcher(String expression) {
    return buildMatcher(parseMatcher(expression));
  }

  private static TaskMatcher buildMatcher(MatcherContext matcherContext) {
    return new TaskMatcherBaseVisitor<TaskMatcher>() {
      @Override
      public TaskMatcher visitMatcher(MatcherContext mContext) {
        return mContext.expression().accept(this);
      }

      @Override
      public TaskMatcher visitAnd(AndContext andContext) {
        return TaskMatchers.and(andContext.expression(0).accept(this), andContext.expression(1).accept(this));
      }

      @Override
      public TaskMatcher visitOr(OrContext orContext) {
        return TaskMatchers.or(orContext.expression(0).accept(this), orContext.expression(1).accept(this));
      }

      @Override
      public TaskMatcher visitBrackets(BracketsContext bracketsContext) {
        return bracketsContext.expression().accept(this);
      }

      @Override
      public TaskMatcher visitMatcherName(MatcherNameContext nameContext) {
        String name = nameContext.MATCHER_NAME().getText();
        return TaskMatchers.findMatcher(name)
            .orElseThrow(() -> new TypeConversionException("Unknown matcher '" + name + "'."));
      }
    }.visit(matcherContext);
  }
}
