package org.smoothbuild.util;

import static org.smoothbuild.util.UnescapingFailedExc.illegalEscapeSeqException;
import static org.smoothbuild.util.UnescapingFailedExc.missingEscapeCodeException;

import java.util.Optional;

public class Strings {
  private static final char TAB = '\t';
  private static final char BACKSPACE = '\b';
  private static final char NEW_LINE = '\n';
  private static final char CARRIAGE_RETURN = '\r';
  private static final char FORM_FEED = '\f';
  private static final char DOUBLE_QUOTE = '\"';
  private static final char BACKSLASH = '\\';

  private static final String BACKSLASH_STRING = "\\";

  public static String q(String string) {
    return "`" + string + "`";
  }

  public static String qq(String string) {
    return "\"" + string + "\"";
  }

  public static String indent(String string) {
    // JDK's String.indent() adds new-line at the end of string and it uses internally
    // String.lines() which doesn't handle correctly multiple new-lines at the end of string.
    var stringBuilder = new StringBuilder();
    var empty = true;
    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      if (c == '\n') {
        stringBuilder.append(c);
        empty = true;
      } else if (empty) {
        stringBuilder.append("  ");
        stringBuilder.append(c);
        empty = false;
      } else {
        stringBuilder.append(c);
      }
    }
    return stringBuilder.toString();
  }

  public static String unlines(String... lines) {
    return String.join("\n", lines);
  }

  /**
   * Unescapes Smooth string. Replaces all escaped characters according to
   * following rules:
   *
   * <pre>
   * \t is replaced with a tab
   * \b is replaced with a backspace
   * \n is replaced with a newline
   * \r is replaced with a carriage return
   * \f is replaced with a formfeed
   * \" is replaced with a double quote character
   * \\ is replaced with a backslash character
   * </pre>
   */
  public static String unescaped(String string) {
    if (string.contains(BACKSLASH_STRING)) {
      return unescapedImpl(string);
    } else {
      return string;
    }
  }

  public static String limitedWithEllipsis(String string, int limit) {
    if (string.length() <= limit) {
      return string;
    } else {
      return string.substring(0, limit - 3) + "...";
    }
  }

  /**
   * Escapes string so it can be used as String literal in smooth code.
   * Replaces all escaped characters according to following rules:
   *
   * <pre>
   * tab is replaced with \t
   * backspace is replaced with \b
   * newline is replaced with \n
   * carriage return is replaced with \r
   * formfeed is replaced with \f
   * double quotes character is replaced with a \"
   * backslash character is replaced with \\
   * </pre>
   */
  public static String escaped(String string) {
    StringBuilder stringBuilder = new StringBuilder(string.length());
    for (int i = 0; i < string.length(); i++) {
      char currentChar = string.charAt(i);
      switch (currentChar) {
        case TAB -> {
          stringBuilder.append('\\');
          stringBuilder.append('t');
        }
        case BACKSPACE -> {
          stringBuilder.append('\\');
          stringBuilder.append('b');
        }
        case NEW_LINE -> {
          stringBuilder.append('\\');
          stringBuilder.append('n');
        }
        case CARRIAGE_RETURN -> {
          stringBuilder.append('\\');
          stringBuilder.append('r');
        }
        case FORM_FEED -> {
          stringBuilder.append('\\');
          stringBuilder.append('f');
        }
        case DOUBLE_QUOTE -> {
          stringBuilder.append('\\');
          stringBuilder.append('"');
        }
        case BACKSLASH -> {
          stringBuilder.append('\\');
          stringBuilder.append('\\');
        }
        default -> stringBuilder.append(currentChar);
      }
    }
    return stringBuilder.toString();
  }

  private static String unescapedImpl(String string) {
    char[] result = new char[string.length()];
    int stringIndex = 0;
    int resultIndex = 0;

    while (stringIndex < string.length()) {
      char current = string.charAt(stringIndex);
      if (current == BACKSLASH) {
        stringIndex++;
        if (stringIndex == string.length()) {
          throw missingEscapeCodeException(stringIndex - 1);
        }
        result[resultIndex] = convertEscapeCodeToChar(string.charAt(stringIndex), stringIndex);
      } else {
        result[resultIndex] = current;
      }
      stringIndex++;
      resultIndex++;
    }
    return new String(result, 0, resultIndex);
  }

  private static char convertEscapeCodeToChar(char code, int charIndex) {
    return switch (code) {
      case 't' -> TAB;
      case 'b' -> BACKSPACE;
      case 'n' -> NEW_LINE;
      case 'r' -> CARRIAGE_RETURN;
      case 'f' -> FORM_FEED;
      case '"' -> DOUBLE_QUOTE;
      case '\\' -> BACKSLASH;
      default -> throw illegalEscapeSeqException(charIndex);
    };
  }

  public static Optional<String> stringToOptionalString(String string) {
    return string.isEmpty() ?  Optional.empty() : Optional.of(string);
  }
}
