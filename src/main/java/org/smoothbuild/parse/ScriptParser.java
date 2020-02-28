package org.smoothbuild.parse;

import static java.lang.String.join;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.maybe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

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
import org.smoothbuild.antlr.SmoothLexer;
import org.smoothbuild.antlr.SmoothParser;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.util.Maybe;

import okio.BufferedSource;

public class ScriptParser {
  public static Maybe<ModuleContext> parseScript(Path scriptFile) {
    CharStream charStream;
    try {
      charStream = charStream(scriptFile);
    } catch (IOException e) {
      return error("error: Cannot read build script file '" + scriptFile + "'.");
    }

    ErrorListener errorListener = new ErrorListener(scriptFile);
    SmoothLexer lexer = new SmoothLexer(charStream);
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);

    SmoothParser parser = new SmoothParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    return maybe(parser.module(), errorListener.foundErrors());
  }

  private static CharStream charStream(Path scriptFile) throws IOException {
    try (BufferedSource source = buffer(source(scriptFile))) {
      return CharStreams.fromStream(source.inputStream());
    }
  }

  public static class ErrorListener implements ANTLRErrorListener {
    private final List<ParseError> errors = new ArrayList<>();
    private final Path file;

    public ErrorListener(Path file) {
      this.file = file;
    }

    public List<ParseError> foundErrors() {
      return errors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,  Object offendingSymbol, int line,
        int charPositionInLine, String msg,  RecognitionException e) {
      Location location = createLocation(offendingSymbol, line);
      errors.add(new ParseError(location, msg));
    }

    private Location createLocation(Object offendingSymbol, int line) {
      if (offendingSymbol == null) {
        return location(file, line);
      } else {
        return locationOf(file, (Token) offendingSymbol);
      }
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
        int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
      String message = join("\n",
          "Found ambiguity in grammar.",
          "Report this as a bug together with file: " + file + ", details:",
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
      errors.add(new ParseError(locationOf(file, token), message));
    }
  }
}
