package org.smoothbuild.app.run.eval.report;

import static org.smoothbuild.app.run.eval.report.ReportMatchers.and;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.findMatcher;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.or;

import org.smoothbuild.antlr.taskmatcher.TaskMatcherBaseVisitor;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.AndContext;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.BracketsContext;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.MatcherContext;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.MatcherNameContext;
import org.smoothbuild.antlr.taskmatcher.TaskMatcherParser.OrContext;
import org.smoothbuild.common.log.ReportMatcher;
import picocli.CommandLine.TypeConversionException;

public class MatcherCreator {
  public static ReportMatcher createMatcher(String expression) {
    return buildMatcher(ReportMatcherParser.parseMatcher(expression));
  }

  private static ReportMatcher buildMatcher(MatcherContext matcherContext) {
    return new TaskMatcherBaseVisitor<ReportMatcher>() {
      @Override
      public ReportMatcher visitMatcher(MatcherContext mContext) {
        return mContext.expression().accept(this);
      }

      @Override
      public ReportMatcher visitAnd(AndContext andContext) {
        return and(
            andContext.expression(0).accept(this), andContext.expression(1).accept(this));
      }

      @Override
      public ReportMatcher visitOr(OrContext orContext) {
        return or(orContext.expression(0).accept(this), orContext.expression(1).accept(this));
      }

      @Override
      public ReportMatcher visitBrackets(BracketsContext bracketsContext) {
        return bracketsContext.expression().accept(this);
      }

      @Override
      public ReportMatcher visitMatcherName(MatcherNameContext nameContext) {
        String name = nameContext.MATCHER_NAME().getText();
        return findMatcher(name)
            .getOrThrow(() -> new TypeConversionException("Unknown matcher '" + name + "'."));
      }
    }.visit(matcherContext);
  }
}
