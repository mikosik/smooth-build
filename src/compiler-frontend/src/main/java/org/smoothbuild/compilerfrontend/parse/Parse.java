package org.smoothbuild.compilerfrontend.parse;

import static java.lang.String.join;
import static org.smoothbuild.common.Antlr.errorLine;
import static org.smoothbuild.common.Antlr.markingLine;
import static org.smoothbuild.common.base.Strings.unlines;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;

import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.smoothbuild.antlr.lang.SmoothAntlrLexer;
import org.smoothbuild.antlr.lang.SmoothAntlrParser;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ModuleContext;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.location.Locations;

public class Parse implements TryFunction<Tuple2<String, FullPath>, ModuleContext> {
  @Override
  public Try<ModuleContext> apply(Tuple2<String, FullPath> argument) {
    var logger = new Logger();
    String sourceCode = argument.element1();
    FullPath fullPath = argument.element2();
    var errorListener = new ErrorListener(fullPath, logger);
    var smoothAntlrLexer = new SmoothAntlrLexer(CharStreams.fromString(sourceCode));
    smoothAntlrLexer.removeErrorListeners();
    smoothAntlrLexer.addErrorListener(errorListener);

    var smoothAntlrParser = new SmoothAntlrParser(new CommonTokenStream(smoothAntlrLexer));
    smoothAntlrParser.removeErrorListeners();
    smoothAntlrParser.addErrorListener(errorListener);

    return Try.of(smoothAntlrParser.module(), logger);
  }

  public static class ErrorListener implements ANTLRErrorListener {
    private final FullPath fullPath;
    private final Logger logger;

    public ErrorListener(FullPath fullPath, Logger logger) {
      this.fullPath = fullPath;
      this.logger = logger;
    }

    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int lineNumber,
        int charNumber,
        String message,
        RecognitionException e) {
      var location = createLoc(offendingSymbol, lineNumber);
      String text = unlines(
          message,
          errorLine(recognizer, lineNumber),
          markingLine((Token) offendingSymbol, charNumber));
      logger.log(compileError(location, text));
    }

    private Location createLoc(Object offendingSymbol, int line) {
      if (offendingSymbol == null) {
        return Locations.fileLocation(fullPath, line);
      } else {
        return locOf(fullPath, (Token) offendingSymbol);
      }
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
          "Report this as a bug together with file: " + fullPath.path() + ", details:",
          "startIndex=" + startIndex,
          "stopIndex=" + stopIndex,
          "exact=" + exact,
          "ambigAlts=" + ambigAlts,
          "dfa=" + dfa);
      reportError(recognizer, startIndex, message);
    }

    @Override
    public void reportAttemptingFullContext(
        Parser recognizer,
        DFA dfa,
        int startIndex,
        int stopIndex,
        BitSet conflictingAlts,
        ATNConfigSet configs) {
      var message = join(
          "\n",
          "Attempting full context.",
          "Report this as a bug together with file: " + fullPath.path() + ", details:",
          "startIndex=" + startIndex,
          "stopIndex=" + stopIndex,
          "conflictingAlts=" + conflictingAlts,
          "configs=" + configs,
          "dfa=" + dfa);
      reportError(recognizer, startIndex, message);
    }

    @Override
    public void reportContextSensitivity(
        Parser recognizer,
        DFA dfa,
        int startIndex,
        int stopIndex,
        int prediction,
        ATNConfigSet configs) {
      var message = join(
          "\n",
          "Context sensitivity.",
          "Report this as a bug together with file: " + fullPath.path() + ", details:",
          "startIndex=" + startIndex,
          "stopIndex=" + stopIndex,
          "configs=" + configs,
          "dfa=" + dfa);
      reportError(recognizer, startIndex, message);
    }

    private void reportError(Parser recognizer, int startIndex, String message) {
      Token token = recognizer.getTokenStream().get(startIndex);
      logger.log(compileError(locOf(fullPath, token), message));
    }
  }

  private static Location locOf(FullPath fullPath, Token token) {
    return Locations.fileLocation(fullPath, token.getLine());
  }
}
