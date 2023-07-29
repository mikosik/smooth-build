package org.smoothbuild.common;

public class Throwables {
  public static RuntimeException unexpectedCaseExc(Object object) {
    return new RuntimeException(messageFor(object));
  }

  private static String messageFor(Object object) {
    return switch (object) {
      case null -> "Unexpected case. object=null.";
      case Object o -> "Unexpected case.\nobject=" + o + "\nclass=" + o.getClass().getCanonicalName();
    };
  }
}
