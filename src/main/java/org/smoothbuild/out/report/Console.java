package org.smoothbuild.out.report;

import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is thread-save.
 */
@Singleton
public class Console {
  private final PrintWriter printWriter;

  @Inject
  public Console(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  public void error(String message) {
    printErrorToWriter(printWriter, message);
  }

  public static void printErrorToWriter(PrintWriter printWriter, String message) {
    printWriter.println("smooth: error: " + message);
  }

  public void println(String line) {
    printWriter.println(line);
  }
}
