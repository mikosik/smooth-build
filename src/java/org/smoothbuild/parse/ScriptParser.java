package org.smoothbuild.parse;

import static org.smoothbuild.parse.Helpers.locationOf;

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
import org.smoothbuild.parse.err.CannotReadScriptError;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.problem.SourceLocation;

public class ScriptParser {
  public static ModuleContext parseScript(ProblemsListener problems, InputStream inputStream,
      Path scriptFile) {
    ErrorListener errorListener = new ErrorListener(problems);

    ANTLRInputStream antlrInputStream;
    try {
      antlrInputStream = new ANTLRInputStream(inputStream);
    } catch (IOException e) {
      problems.report(new CannotReadScriptError(scriptFile, e));
      return null;
    }

    SmoothLexer lexer = new SmoothLexer(antlrInputStream);
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);

    SmoothParser parser = new SmoothParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    return parser.module();
  }

  public static class ErrorListener implements ANTLRErrorListener {
    private final ProblemsListener problems;

    public ErrorListener(ProblemsListener problems) {
      this.problems = problems;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol,
        int line, int charPositionInLine, String msg, @Nullable RecognitionException e) {
      SourceLocation location = createLocation(offendingSymbol, line, charPositionInLine);
      problems.report(new SyntaxError(location, msg));
    }

    private SourceLocation createLocation(Object offendingSymbol, int line, int charPositionInLine) {
      if (offendingSymbol == null) {
        int start = charPositionInLine;
        int stop = charPositionInLine;
        return new SourceLocation(line, start, stop);
      } else {
        return locationOf((Token) offendingSymbol);
      }
    }

    @Override
    public void reportAmbiguity(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex,
        int stopIndex, boolean exact, @NotNull BitSet ambigAlts, @NotNull ATNConfigSet configs) {
      reportProblem(recognizer, startIndex, "Ambiguity in grammar");
    }

    @Override
    public void reportAttemptingFullContext(@NotNull Parser recognizer, @NotNull DFA dfa,
        int startIndex, int stopIndex, @Nullable BitSet conflictingAlts,
        @NotNull ATNConfigSet configs) {
      reportProblem(recognizer, startIndex, "Attempting full context");
    }

    @Override
    public void reportContextSensitivity(@NotNull Parser recognizer, @NotNull DFA dfa,
        int startIndex, int stopIndex, int prediction, @NotNull ATNConfigSet configs) {
      reportProblem(recognizer, startIndex, "Context sensitivity");
    }

    private void reportProblem(Parser recognizer, int startIndex, String message) {
      Token token = recognizer.getTokenStream().get(startIndex);
      problems.report(new CodeError(locationOf(token), message));
    }
  }
}
