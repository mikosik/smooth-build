package org.smoothbuild.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Throwables {
  public static RuntimeException unexpectedCaseExc(Object object) {
    return new RuntimeException(messageFor(object));
  }

  private static String messageFor(Object object) {
    return switch (object) {
      case null -> "Unexpected case. object=null.";
      case Object o -> "Unexpected case. object=" + o + " class=" + o.getClass().getCanonicalName();
    };
  }

  public static String stackTraceToString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
}
