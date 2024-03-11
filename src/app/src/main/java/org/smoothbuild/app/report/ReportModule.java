package org.smoothbuild.app.report;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.common.log.CountingReporter;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.LogCounters;
import org.smoothbuild.common.log.ReportMatcher;
import org.smoothbuild.common.log.Reporter;

public class ReportModule extends AbstractModule {
  private final PrintWriter out;
  private final ReportMatcher reportMatcher;
  private final Level logLevel;

  public ReportModule(PrintWriter out, ReportMatcher reportMatcher, Level logLevel) {
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
      LogCounters logCounters,
      ReportMatcher reportMatcher) {
    var filtering = new TaskFilteringReporter(printWriterReporter, reportMatcher);
    return new CountingReporter(filtering, logCounters);
  }

  @Provides
  @Singleton
  public LogCounters provideLogCounters() {
    return new LogCounters();
  }
}
