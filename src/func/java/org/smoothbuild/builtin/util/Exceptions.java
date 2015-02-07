package org.smoothbuild.builtin.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exceptions {
  public static String stackTraceToString(Exception e) {
    StringWriter stringWriter = new StringWriter();
    e.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
}
