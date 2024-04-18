package org.smoothbuild.app.report;

import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.LogCounters;

public class StatusPrinter {
  private final PrintWriter printWriter;
  private final LogCounters logCounters;

  @Inject
  public StatusPrinter(PrintWriter printWriter, LogCounters logCounters) {
    this.printWriter = printWriter;
    this.logCounters = logCounters;
  }

  public void printSummary() {
    var stats = counterDescriptions();
    var hasFailures = 0 < logCounters.get(ERROR) || 0 < logCounters.get(FATAL);
    var status = hasFailures ? "FAILED: " : "SUCCESS: ";
    printWriter.println(status + String.join(", ", stats));
  }

  private List<String> counterDescriptions() {
    var stats = new ArrayList<String>();
    for (Level level : Level.values()) {
      int count = logCounters.get(level);
      if (count != 0) {
        stats.add(statText(level, logCounters.get(level)));
      }
    }
    return stats;
  }

  private String statText(Level level, int value) {
    var name = level.name().toLowerCase(Locale.ROOT);
    if (1 < value) {
      name = name + "s";
    }
    return value + " " + name;
  }
}
