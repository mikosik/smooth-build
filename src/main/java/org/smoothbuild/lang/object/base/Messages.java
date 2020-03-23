package org.smoothbuild.lang.object.base;

import static com.google.common.collect.Streams.stream;

import com.google.common.collect.ImmutableSet;

public class Messages {
  public static final String SEVERITY = "severity";
  public static final String TEXT = "text";

  public static final String INFO = "INFO";
  public static final String WARNING = "WARNING";
  public static final String ERROR = "ERROR";
  private static final ImmutableSet<String> SEVERITIES = ImmutableSet.of(ERROR, WARNING, INFO);

  public static boolean containsErrors(Array messages) {
    return stream(messages.asIterable(Struct.class))
        .anyMatch(m -> severity(m).equals(ERROR));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(Array messages) {
    return !messages.asIterable(Struct.class).iterator().hasNext();
  }

  public static String severity(SObject message) {
    return stringField((Struct) message, SEVERITY);
  }

  public static String text(SObject message) {
    return stringField((Struct) message, TEXT);
  }

  private static String stringField(Struct message, String fieldName) {
    return ((SString) message.get(fieldName)).jValue();
  }
}
