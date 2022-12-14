package org.smoothbuild.compile.ps;

import static java.lang.String.join;
import static org.smoothbuild.compile.lang.base.location.Locations.fileLocation;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.util.Antlr.errorLine;
import static org.smoothbuild.util.Antlr.markingLine;
import static org.smoothbuild.util.Strings.unlines;

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
import org.smoothbuild.antlr.lang.SmoothLexer;
import org.smoothbuild.antlr.lang.SmoothParser;
import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Maybe;

public class ParseModule {
  public static Maybe<ModContext> parseModule(FilePath filePath, String sourceCode) {
    var logBuffer = new LogBuffer();
    ErrorListener errorListener = new ErrorListener(filePath, logBuffer);
    SmoothLexer lexer = new SmoothLexer(CharStreams.fromString(sourceCode));
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);

    SmoothParser parser = new SmoothParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);
    var mod = parser.mod();
    var result = logBuffer.containsAtLeast(ERROR) ? null : mod;
    return maybe(result, logBuffer);
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
      logger.log(compileError(location, text));
    }

    private Location createLoc(Object offendingSymbol, int line) {
      if (offendingSymbol == null) {
        return fileLocation(filePath, line);
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
        int startIndex, int stopIndex,  BitSet conflictingAlts,
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
      logger.log(compileError(locOf(filePath, token), message));
    }
  }

  private static Location locOf(FilePath filePath, Token token) {
    return fileLocation(filePath, token.getLine());
  }
}
