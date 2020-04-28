package org.smoothbuild.util;

import static com.google.common.base.Strings.repeat;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Math.max;

import java.util.List;

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
    List<String> lines = extractSourceCode(recognizer).lines().collect(toImmutableList());
    if (lines.isEmpty()) {
      return "";
    } else {
      return lines.get(lineNumber - 1);
    }
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
