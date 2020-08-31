package org.smoothbuild.util;

import static com.google.common.base.Strings.repeat;
import static java.lang.Math.max;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class Antlr {
  public static String markingLine(Token offendingSymbol, int charNumber) {
    String spaces = repeat(" ", charNumber);
    if (offendingSymbol == null) {
      return spaces + "^";
    } else {
      int start = offendingSymbol.getStartIndex();
      int stop = offendingSymbol.getStopIndex();
      return spaces + repeat("^", max(1, stop - start + 1));
    }
  }

  public static String errorLine(Recognizer<?, ?> recognizer, int lineNumber) {
    return extractSourceCode(recognizer).split("(\r\n|\r|\n)", -1)[lineNumber - 1];
  }

  public static String extractSourceCode(Recognizer<?, ?> recognizer) {
    IntStream inputStream = recognizer.getInputStream();
    if (inputStream instanceof CharStream) {
      return inputStream.toString();
    } else {
      CommonTokenStream tokens = (CommonTokenStream) inputStream;
      return tokens.getTokenSource().getInputStream().toString();
    }
  }
}
