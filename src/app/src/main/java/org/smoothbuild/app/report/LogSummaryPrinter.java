package org.smoothbuild.app.report;

import static org.smoothbuild.common.base.Strings.indent;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.Locale;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.LogCounters;

public class LogSummaryPrinter {
  private final PrintWriter printWriter;
  private final LogCounters logCounters;

  @Inject
  public LogSummaryPrinter(PrintWriter printWriter, LogCounters logCounters) {
    this.printWriter = printWriter;
    this.logCounters = logCounters;
  }

  public void printSummary() {
    printWriter.println(":Summary");
    int total = 0;
    for (Level level : Level.values()) {
      int count = logCounters.get(level);
      if (count != 0) {
        int value = logCounters.get(level);
        printWriter.println(indent(statText(level, value)));
      }
      total += count;
    }
    if (total == 0) {
      printWriter.println("No logs reported");
    }
  }

  private String statText(Level level, int value) {
    var name = level.name().toLowerCase(Locale.ROOT);
    if (1 < value) {
      name = name + "s";
    }
    return value + " " + name;
  }
}
