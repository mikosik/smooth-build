package org.smoothbuild.cli.match;

import static java.lang.String.join;
import static org.antlr.v4.runtime.CharStreams.fromString;
import static org.smoothbuild.common.Antlr.errorLine;
import static org.smoothbuild.common.Antlr.extractSourceCode;
import static org.smoothbuild.common.Antlr.markingLine;
import static org.smoothbuild.common.base.Strings.unlines;

import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherLexer;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser;
import org.smoothbuild.antlr.reportmatcher.ReportMatcherParser.MatcherContext;
import picocli.CommandLine.TypeConversionException;

public class CreateMatcherContext {
  public static MatcherContext createMatcherContext(String expression) {
    var errorListener = new ErrorListener();

    var reportMatcherLexer = new ReportMatcherLexer(fromString(expression));
    reportMatcherLexer.removeErrorListeners();
    reportMatcherLexer.addErrorListener(errorListener);

    var parser = new ReportMatcherParser(new CommonTokenStream(reportMatcherLexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    return parser.matcher();
  }

  public static class ErrorListener implements ANTLRErrorListener {
    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int lineNumber,
        int charNumber,
        String message,
        RecognitionException e) {
      String detailedMessage = unlines(
          message,
          errorLine(recognizer, lineNumber),
          markingLine((Token) offendingSymbol, charNumber));
      throw new TypeConversionException(detailedMessage);
    }

    @Override
    public void reportAmbiguity(
        Parser recognizer,
        DFA dfa,
        int startIndex,
        int stopIndex,
        boolean exact,
        BitSet ambigAlts,
        ATNConfigSet configs) {
      String message = join(
          "\n",
          "Found ambiguity in grammar.",
          "Report this as a bug together with filter-tasks=': " + extractSourceCode(recognizer)
              + "', details:",
          "startIndex=" + startIndex,
          "stopIndex=" + stopIndex,
          "exact=" + exact,
          "ambigAlts=" + ambigAlts,
          "dfa=" + dfa);
      reportError(message);
    }

    @Override
    public void reportAttemptingFullContext(
        Parser recognizer,
        DFA dfa,
        int startIndex,
        int stopIndex,
        BitSet conflictingAlts,
        ATNConfigSet configs) {
      reportError("Attempting full context");
    }

    @Override
    public void reportContextSensitivity(
        Parser recognizer,
        DFA dfa,
        int startIndex,
        int stopIndex,
        int prediction,
        ATNConfigSet configs) {
      reportError("Context sensitivity");
    }

    private void reportError(String message) {
      throw new TypeConversionException(message);
    }
  }
}
