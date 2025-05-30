package org.smoothbuild.common.log.report;

import dagger.Module;
import dagger.Provides;
import java.io.PrintWriter;
import java.util.function.Predicate;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.log.base.Level;

@Module
public interface ReportModule {
  @Provides
  @PerCommand
  static Reporter provideReporter(
      LogCounters logCounters,
      PrintWriter out,
      Level logLevel,
      @TaskFilter Predicate<Report> filterTasks,
      @TraceFilter Predicate<Report> filterTraces) {
    var reportPrinter = new ReportPrinter(out);
    var filtering = new FilteringReporter(reportPrinter, filterTasks, filterTraces, logLevel);
    return new CountingReporter(filtering, logCounters);
  }
}
