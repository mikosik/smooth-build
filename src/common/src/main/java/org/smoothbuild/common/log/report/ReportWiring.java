package org.smoothbuild.common.log.report;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.common.log.base.Level;

public class ReportWiring extends AbstractModule {
  private final PrintWriter out;
  private final ReportMatcher filterTasks;
  private final Level logLevel;
  private final ReportMatcher filterTraces;

  public ReportWiring(
      PrintWriter out, Level logLevel, ReportMatcher filterTasks, ReportMatcher filterTraces) {
    this.out = out;
    this.filterTasks = filterTasks;
    this.logLevel = logLevel;
    this.filterTraces = filterTraces;
  }

  @Override
  protected void configure() {
    bind(PrintWriter.class).toInstance(out);
    bind(ReportMatcher.class).toInstance(filterTasks);
  }

  @Provides
  @Singleton
  public Reporter provideReporter(LogCounters logCounters) {
    var reportPrinter = new ReportPrinter(out);
    var filtering = new FilteringReporter(reportPrinter, filterTasks, filterTraces, logLevel);
    return new CountingReporter(filtering, logCounters);
  }

  @Provides
  @Singleton
  public LogCounters provideLogCounters() {
    return new LogCounters();
  }
}
