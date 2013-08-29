package org.smoothbuild.testing;

/**
 * Builder for easy creation of sample script in tests. All single quote
 * characters (') are replaced by double quote characters ("). This way there's
 * no need to use escape symbol when you need to place smooth string inside java
 * string (Just use single quotes)..
 */
public class ScriptBuilder {
  private final StringBuilder builder = new StringBuilder();

  public static String script(String string) {
    return fromSingleToDoubleQuotes(string);
  }

  public ScriptBuilder addLine(String string) {
    builder.append(fromSingleToDoubleQuotes(string));
    builder.append("\n");
    return this;
  }

  private static String fromSingleToDoubleQuotes(String string) {
    return string.replace('\'', '"');
  }

  public String build() {
    return builder.toString();
  }
}
