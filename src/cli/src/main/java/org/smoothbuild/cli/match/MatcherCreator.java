package org.smoothbuild.cli.match;

import static org.smoothbuild.cli.match.CreateMatcherContext.createMatcherContext;
import static org.smoothbuild.cli.match.ReportMatchers.and;
import static org.smoothbuild.cli.match.ReportMatchers.findMatcher;
import static org.smoothbuild.cli.match.ReportMatchers.or;

import org.smoothbuild.antlr.reportmatcher.ReportMatcherBaseVisitor;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.AndContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.BracketsContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.MatcherContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.MatcherNameContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.OrContext;
import org.smoothbuild.common.log.report.ReportMatcher;
import picocli.CommandLine.TypeConversionException;

public class MatcherCreator {
  public static ReportMatcher createMatcher(String expression) {
    return buildMatcher(createMatcherContext(expression));
  }

  private static ReportMatcher buildMatcher(MatcherContext matcherContext) {
    return new ReportMatcherBaseVisitor<ReportMatcher>() {
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
