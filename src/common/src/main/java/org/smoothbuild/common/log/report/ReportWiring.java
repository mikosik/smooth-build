package org.smoothbuild.common.log.report;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.common.log.base.Level;

public class ReportWiring extends AbstractModule {
  private final PrintWriter out;
  private final ReportMatcher reportMatcher;
  private final Level logLevel;

  public ReportWiring(PrintWriter out, ReportMatcher reportMatcher, Level logLevel) {
    this.out = out;
    this.reportMatcher = reportMatcher;
    this.logLevel = logLevel;
  }

  @Override
  protected void configure() {
    bind(Level.class).toInstance(logLevel);
    bind(PrintWriter.class).toInstance(out);
    bind(ReportMatcher.class).toInstance(reportMatcher);
  }

  @Provides
  @Singleton
  public Reporter provideReporter(
      PrintWriterReporter printWriterReporter,
      Level level,
      LogCounters logCounters,
      ReportMatcher reportMatcher) {
    var logFiltering = new LogFilteringReporter(printWriterReporter, level);
    var taskFiltering = new ReportFilteringReporter(logFiltering, reportMatcher);
    return new CountingReporter(taskFiltering, logCounters);
  }

  @Provides
  @Singleton
  public LogCounters provideLogCounters() {
    return new LogCounters();
  }
}
