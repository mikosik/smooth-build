package org.smoothbuild.lang.parse;

import static java.lang.String.join;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.parse.LocationHelpers.locationOf;
import static org.smoothbuild.lang.parse.ParseError.parseError;
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
import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ModuleLocation;

public class ParseModule {
  public static ModuleContext parseModule(ModuleLocation info, Logger logger, String sourceCode) {
    ErrorListener errorListener = new ErrorListener(info, logger);
    SmoothLexer lexer = new SmoothLexer(CharStreams.fromString(sourceCode));
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);

    SmoothParser parser = new SmoothParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);
    return parser.module();
  }

  public static class ErrorListener implements ANTLRErrorListener {
    private final ModuleLocation moduleLocation;
    private final Logger logger;

    public ErrorListener(ModuleLocation moduleLocation, Logger logger) {
      this.moduleLocation = moduleLocation;
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
        return location(moduleLocation, line);
      } else {
        return locationOf(moduleLocation, (Token) offendingSymbol);
      }
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
        int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
      String message = join("\n",
          "Found ambiguity in grammar.",
          "Report this as a bug together with file: " + moduleLocation.path() + ", details:",
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
      logger.log(parseError(locationOf(moduleLocation, token), message));
    }
  }
}