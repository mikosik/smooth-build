package org.smoothbuild.compile.fs.fp;

import static java.lang.String.join;
import static org.smoothbuild.common.Antlr.errorLine;
import static org.smoothbuild.common.Antlr.markingLine;
import static org.smoothbuild.common.Strings.unlines;

import java.util.BitSet;
import java.util.function.Function;

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
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.base.location.Locations;
import org.smoothbuild.compile.fs.ps.CompileError;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Maybe;

import io.vavr.Tuple2;

public class Parse implements Function<Tuple2<String, FilePath>, Maybe<ModuleContext>> {
  @Override
  public Maybe<ModuleContext> apply(Tuple2<String, FilePath> argument) {
    var logBuffer = new LogBuffer();
    String sourceCode = argument._1();
    FilePath filePath = argument._2();
    var errorListener = new ErrorListener(filePath, logBuffer);
    var smoothAntlrLexer = new SmoothAntlrLexer(CharStreams.fromString(sourceCode));
    smoothAntlrLexer.removeErrorListeners();
    smoothAntlrLexer.addErrorListener(errorListener);

    var smoothAntlrParser = new SmoothAntlrParser(new CommonTokenStream(smoothAntlrLexer));
    smoothAntlrParser.removeErrorListeners();
    smoothAntlrParser.addErrorListener(errorListener);

    return Maybe.of(smoothAntlrParser.module(), logBuffer);
  }

  public static class ErrorListener implements ANTLRErrorListener {
    private final FilePath filePath;
    private final Logger logger;

    public ErrorListener(FilePath filePath, Logger logger) {
      this.filePath = filePath;
      this.logger = logger;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int lineNumber,
        int charNumber, String message, RecognitionException e) {
      var location = createLoc(offendingSymbol, lineNumber);
      String text = unlines(
          message,
          errorLine(recognizer, lineNumber),
          markingLine((Token) offendingSymbol, charNumber));
      logger.log(CompileError.compileError(location, text));
    }

    private Location createLoc(Object offendingSymbol, int line) {
      if (offendingSymbol == null) {
        return Locations.fileLocation(filePath, line);
      } else {
        return locOf(filePath, (Token) offendingSymbol);
      }
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
        int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
      String message = join("\n",
          "Found ambiguity in grammar.",
          "Report this as a bug together with file: " + filePath.path() + ", details:",
          "startIndex=" + startIndex,
          "stopiIndex=" + stopIndex,
          "exact=" + exact,
          "ambigAlts=" + ambigAlts,
          "dfa=" + dfa);
      reportError(recognizer, startIndex, message);
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa,
        int startIndex, int stopIndex, BitSet conflictingAlts,
        ATNConfigSet configs) {
      var message = join("\n",
          "Attempting full context.",
          "Report this as a bug together with file: " + filePath.path() + ", details:",
          "startIndex=" + startIndex,
          "stopiIndex=" + stopIndex,
          "conflictingAlts=" + conflictingAlts,
          "configs=" + configs,
          "dfa=" + dfa);
      reportError(recognizer, startIndex, message);
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa,
        int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
      var message = join("\n",
          "Context sensitivity.",
          "Report this as a bug together with file: " + filePath.path() + ", details:",
          "startIndex=" + startIndex,
          "stopiIndex=" + stopIndex,
          "configs=" + configs,
          "dfa=" + dfa);
      reportError(recognizer, startIndex, message);
    }

    private void reportError(Parser recognizer, int startIndex, String message) {
      Token token = recognizer.getTokenStream().get(startIndex);
      logger.log(CompileError.compileError(locOf(filePath, token), message));
    }
  }

  private static Location locOf(FilePath filePath, Token token) {
    return Locations.fileLocation(filePath, token.getLine());
  }
}
