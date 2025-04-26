package org.smoothbuild.common.base;

public class Throwables {
  public static RuntimeException unexpectedCaseException(Object object) {
    return new RuntimeException(messageFor(object));
  }

  private static String messageFor(Object object) {
    return switch (object) {
      case null -> "Unexpected case. object=null.";
      case Object o ->
        "Unexpected case.\nobject=" + o + "\nclass=" + o.getClass().getCanonicalName();
    };
  }

  public static String concatenateExceptionMessages(Exception e) {
    StringBuilder messageBuilder = new StringBuilder();
    Throwable current = e;
    while (current != null) {
      if (!messageBuilder.isEmpty()) {
        messageBuilder.append("\n");
      }
      messageBuilder.append(current.getMessage());
      current = current.getCause();
    }
    return messageBuilder.toString();
  }
}
