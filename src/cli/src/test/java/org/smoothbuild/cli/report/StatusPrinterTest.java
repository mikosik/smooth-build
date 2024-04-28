package org.smoothbuild.cli.report;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.report.LogCounters;

public class StatusPrinterTest {
  @Test
  void when_all_levels_are_reported() {
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

    new StatusPrinter(systemOut, logCounters).printSummary();

    var inOrder = inOrder(systemOut);
    inOrder.verify(systemOut).println("FAILED: 1 fatal, 2 errors, 3 warnings, 4 infos");
  }

  @Test
  void when_no_failed_level_is_reported() {
    var systemOut = mock(PrintWriter.class);
    var logCounters = new LogCounters();
    logCounters.increment(WARNING);
    logCounters.increment(INFO);
    logCounters.increment(INFO);

    new StatusPrinter(systemOut, logCounters).printSummary();

    var inOrder = inOrder(systemOut);
    inOrder.verify(systemOut).println("SUCCESS: 1 warning, 2 infos");
  }

  @Test
  void skips_levels_with_zero_logs() {
    var systemOut = mock(PrintWriter.class);
    var logCounters = new LogCounters();
    logCounters.increment(FATAL);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    logCounters.increment(INFO);
    var reporter = new StatusPrinter(systemOut, logCounters);

    reporter.printSummary();

    var inOrder = inOrder(systemOut);
    inOrder.verify(systemOut).println("FAILED: 1 fatal, 4 infos");
  }
}
