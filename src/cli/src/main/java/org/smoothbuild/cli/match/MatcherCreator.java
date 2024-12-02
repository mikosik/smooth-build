package org.smoothbuild.cli.match;

import static org.smoothbuild.cli.match.CreateMatcherContext.createMatcherContext;
import static org.smoothbuild.cli.match.ReportMatchers.and;
import static org.smoothbuild.cli.match.ReportMatchers.findMatcher;
import static org.smoothbuild.cli.match.ReportMatchers.labelMatcher;
import static org.smoothbuild.cli.match.ReportMatchers.or;

import java.util.function.Predicate;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherBaseVisitor;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.AndContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.BracketsContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.LabelPatternContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.MatcherContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.MatcherNameContext;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.OrContext;
import org.smoothbuild.common.log.report.Report;

public class MatcherCreator {
  public static Predicate<Report> createMatcher(String expression) {
    return buildMatcher(createMatcherContext(expression));
  }

  private static Predicate<Report> buildMatcher(MatcherContext matcherContext) {
    return new ReportMatcherBaseVisitor<Predicate<Report>>() {
      @Override
      public Predicate<Report> visitMatcher(MatcherContext mContext) {
        return mContext.expression().accept(this);
      }

      @Override
      public Predicate<Report> visitAnd(AndContext andContext) {
        return and(
            andContext.expression(0).accept(this), andContext.expression(1).accept(this));
      }

      @Override
      public Predicate<Report> visitOr(OrContext orContext) {
        return or(orContext.expression(0).accept(this), orContext.expression(1).accept(this));
      }

      @Override
      public Predicate<Report> visitBrackets(BracketsContext bracketsContext) {
        return bracketsContext.expression().accept(this);
      }

      @Override
      public Predicate<Report> visitMatcherName(MatcherNameContext nameContext) {
        return findMatcher(nameContext.MATCHER_NAME().getText());
      }

      @Override
      public Predicate<Report> visitLabelPattern(LabelPatternContext labelPatternContext) {
        return labelMatcher(labelPatternContext.LABEL_PATTERN().getText());
      }
    }.visit(matcherContext);
  }
}
