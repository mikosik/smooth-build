package org.smoothbuild.lang.message;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.message.MessagesDb.SEVERITY;
import static org.smoothbuild.lang.message.MessagesDb.TEXT;

import java.util.Objects;
import java.util.Set;

import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableSet;

public class Message {
  public static final String INFO = "INFO";
  public static final String WARNING = "WARNING";
  public static final String ERROR = "ERROR";
  private static final Set<String> SEVERITIES = ImmutableSet.of(ERROR, WARNING, INFO);

  private final String text;
  private final String severity;
  private final Value value;

  public Message(Struct struct) {
    this(((SString) struct.get(TEXT)).data(),
        ((SString) struct.get(SEVERITY)).data(),
        struct);
  }

  protected Message(String text, String severity, Value value) {
    checkArgument(isValidSeverity(checkNotNull(severity)));
    this.text = checkNotNull(text);
    this.severity = checkNotNull(severity);
    this.value = value;
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public String text() {
    return text;
  }

  public String severity() {
    return severity;
  }

  public Value value() {
    return value;
  }

  public boolean isError() {
    return severity.equals(ERROR);
  }

  public boolean isWarning() {
    return severity.equals(WARNING);
  }

  public boolean isInfo() {
    return severity.equals(INFO);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Message) {
      Message that = (Message) object;
      return this.severity.equals(that.severity) && text.equals(that.text);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(severity, text);
  }

  @Override
  public String toString() {
    return severity + ": " + text;
  }
}
