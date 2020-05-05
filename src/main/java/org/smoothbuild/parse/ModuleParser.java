package org.smoothbuild.parse;

import static java.lang.String.join;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.Antlr.errorLine;
import static org.smoothbuild.util.Antlr.markingLine;
import static org.smoothbuild.util.Strings.unlines;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.BitSet;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
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
import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ModulePath;

import okio.BufferedSource;

public class ModuleParser {
  public static ModuleContext parseModule(ModulePath path, Logger logger) {
    CharStream charStream;
    Path filePath = path.smooth().path();
    try {
      charStream = charStream(filePath);
    } catch (NoSuchFileException e) {
      logger.error("'" + filePath + "' doesn't exist.");
      return null;
    } catch (IOException e) {
      logger.error("Cannot read build script file '" + filePath + "'.");
      return null;
    }

    ErrorListener errorListener = new ErrorListener(path, logger);
    SmoothLexer lexer = new SmoothLexer(charStream);
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);

    SmoothParser parser = new SmoothParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);
    return parser.module();
  }

  private static CharStream charStream(Path scriptFile) throws IOException {
    try (BufferedSource source = buffer(source(scriptFile))) {
      return CharStreams.fromStream(source.inputStream());
    }
  }

  public static class ErrorListener implements ANTLRErrorListener {
    private final ModulePath path;
    private final Logger logger;

    public ErrorListener(ModulePath path, Logger logger) {
      this.path = path;
      this.logger = logger;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int lineNumber,
        int charNumber, String message, RecognitionException e) {
      Location location = createLocation(offendingSymbol, lineNumber);
      String text = unlines(
          message,
          errorLine(recognizer, lineNumber),
          markingLine((Token) offendingSymbol, charNumber));
      logger.log(parseError(location, text));
    }

    private Location createLocation(Object offendingSymbol, int line) {
      if (offendingSymbol == null) {
        return location(path, line);
      } else {
        return locationOf(path, (Token) offendingSymbol);
      }
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
        int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
      String message = join("\n",
          "Found ambiguity in grammar.",
          "Report this as a bug together with file: " + path.smooth().path() + ", details:",
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
      reportError(recognizer, startIndex, "Attempting full context");
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa,
        int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
      reportError(recognizer, startIndex, "Context sensitivity");
    }

    private void reportError(Parser recognizer, int startIndex, String message) {
      Token token = recognizer.getTokenStream().get(startIndex);
      logger.log(parseError(locationOf(path, token), message));
    }
  }
}
