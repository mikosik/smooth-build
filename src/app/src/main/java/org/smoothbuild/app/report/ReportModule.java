package org.smoothbuild.app.report;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.app.run.eval.report.TaskMatcher;
import org.smoothbuild.common.log.CountingReporter;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.LogCounters;
import org.smoothbuild.common.log.Reporter;

public class ReportModule extends AbstractModule {
  private final PrintWriter out;
  private final TaskMatcher taskMatcher;
  private final Level logLevel;

  public ReportModule(PrintWriter out, TaskMatcher taskMatcher, Level logLevel) {
    this.out = out;
    this.taskMatcher = taskMatcher;
    this.logLevel = logLevel;
  }

  @Override
  protected void configure() {
    bind(Level.class).toInstance(logLevel);
    bind(PrintWriter.class).toInstance(out);
    bind(TaskMatcher.class).toInstance(taskMatcher);
  }

  @Provides
  @Singleton
  public Reporter provideReporter(
      PrintWriterReporter printWriterReporter, LogCounters logCounters, TaskMatcher taskMatcher) {
    var filtering = new FilteringReporter(printWriterReporter, taskMatcher);
    return new CountingReporter(filtering, logCounters);
  }

  @Provides
  @Singleton
  public LogCounters provideLogCounters() {
    return new LogCounters();
  }
}
