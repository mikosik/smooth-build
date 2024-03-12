package org.smoothbuild.app.report;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.LogCounters;

public class LogSummaryPrinterTest {
  @Test
  public void contains_all_stats() {
    doTestSummary(INFO);
  }

  @Test
  public void contains_stats_for_logs_with_level_below_threshold() {
    doTestSummary(ERROR);
  }

  @Test
  public void skips_levels_with_zero_logs() {
    var systemOut = mock(PrintWriter.class);
    var logCounters = new LogCounters();
    logCounters.increment(FATAL);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    var reporter = new LogSummaryPrinter(systemOut, logCounters);

    reporter.printSummary();

    var inOrder = inOrder(systemOut);
    inOrder.verify(systemOut).println(":Summary");
    inOrder.verify(systemOut).println("  1 fatal");
    inOrder.verify(systemOut).println("  4 infos");
  }

  private void doTestSummary(Level logLevel) {
    var systemOut = mock(PrintWriter.class);
    var logCounters = new LogCounters();
    logCounters.increment(FATAL);
    logCounters.increment(ERROR);
    logCounters.increment(ERROR);
    logCounters.increment(WARNING);
    logCounters.increment(WARNING);
    logCounters.increment(WARNING);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    var reporter = new LogSummaryPrinter(systemOut, logCounters);

    reporter.printSummary();

    var inOrder = inOrder(systemOut);
    inOrder.verify(systemOut).println(":Summary");
    inOrder.verify(systemOut).println("  1 fatal");
    inOrder.verify(systemOut).println("  2 errors");
    inOrder.verify(systemOut).println("  3 warnings");
    inOrder.verify(systemOut).println("  4 infos");
  }
}
