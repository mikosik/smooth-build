package org.smoothbuild.parse;

import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.smoothbuild.antlr.SmoothLexer;
import org.smoothbuild.antlr.SmoothParser;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.UserConsole;

public class ScriptParser {
  public static ModuleContext parseScript(UserConsole console, InputStream inputStream,
      Path scriptFile) {
    ErrorListener errorListener = new ErrorListener(console);

    ANTLRInputStream antlrInputStream;
    try {
      antlrInputStream = new ANTLRInputStream(inputStream);
    } catch (IOException e) {
      throw new ParsingException(
          "error: Cannot read build script " + scriptFile + "\n" + e.getMessage());
    }

    SmoothLexer lexer = new SmoothLexer(antlrInputStream);
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);

    SmoothParser parser = new SmoothParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    ModuleContext result = parser.module();
    if (console.isProblemReported()) {
      throw new ParsingException();
    }
    return result;
  }

  public static class ErrorListener implements ANTLRErrorListener {
    private final UserConsole console;

    public ErrorListener(UserConsole console) {
      this.console = console;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol, int line,
        int charPositionInLine, String msg, @Nullable RecognitionException e) {
      CodeLocation location = createLocation(offendingSymbol, line);
      console.error(location, msg);
    }

    private CodeLocation createLocation(Object offendingSymbol, int line) {
      if (offendingSymbol == null) {
        return codeLocation(line);
      } else {
        return locationOf((Token) offendingSymbol);
      }
    }

    @Override
    public void reportAmbiguity(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex,
        int stopIndex, boolean exact, @NotNull BitSet ambigAlts, @NotNull ATNConfigSet configs) {
      reportError(recognizer, startIndex, "Ambiguity in grammar");
    }

    @Override
    public void reportAttemptingFullContext(@NotNull Parser recognizer, @NotNull DFA dfa,
        int startIndex, int stopIndex, @Nullable BitSet conflictingAlts,
        @NotNull ATNConfigSet configs) {
      reportError(recognizer, startIndex, "Attempting full context");
    }

    @Override
    public void reportContextSensitivity(@NotNull Parser recognizer, @NotNull DFA dfa,
        int startIndex, int stopIndex, int prediction, @NotNull ATNConfigSet configs) {
      reportError(recognizer, startIndex, "Context sensitivity");
    }

    private void reportError(Parser recognizer, int startIndex, String message) {
      Token token = recognizer.getTokenStream().get(startIndex);
      console.error(locationOf(token), message);
    }
  }
}
