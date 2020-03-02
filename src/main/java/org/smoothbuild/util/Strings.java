package org.smoothbuild.util;

public class Strings {

  private static final char TAB = '\t';
  private static final char BACKSPACE = '\b';
  private static final char NEW_LINE = '\n';
  private static final char CARRIAGE_RETURN = '\r';
  private static final char FORM_FEED = '\f';
  private static final char DOUBLE_QUOTE = '\"';
  private static final char BACKSLASH = '\\';

  private static final String BACKSLASH_STRING = "\\";

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

  private static String unescapedImpl(String string) {
    char[] result = new char[string.length()];
    int stringIndex = 0;
    int resultIndex = 0;

    while (stringIndex < string.length()) {
      char current = string.charAt(stringIndex);
      if (current == BACKSLASH) {
        stringIndex++;
        if (stringIndex == string.length()) {
          throw new UnescapingFailedException(stringIndex - 1,
              "Missing escape code after backslash \\");
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
    switch (code) {
      case 't':
        return TAB;
      case 'b':
        return BACKSPACE;
      case 'n':
        return NEW_LINE;
      case 'r':
        return CARRIAGE_RETURN;
      case 'f':
        return FORM_FEED;
      case '"':
        return DOUBLE_QUOTE;
      case '\\':
        return BACKSLASH;
      default:
        throw new UnescapingFailedException(charIndex,
            "Illegal escape sequence. Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
    }
  }
}
