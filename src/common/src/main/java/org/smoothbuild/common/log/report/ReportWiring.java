package org.smoothbuild.common.log.report;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import java.util.function.Predicate;
import org.smoothbuild.common.log.base.Level;

public class ReportWiring extends AbstractModule {
  private final PrintWriter out;
  private final Predicate<Report> filterTasks;
  private final Level logLevel;
  private final Predicate<Report> filterTraces;

  public ReportWiring(
      PrintWriter out,
      Level logLevel,
      Predicate<Report> filterTasks,
      Predicate<Report> filterTraces) {
    this.out = out;
    this.filterTasks = filterTasks;
    this.logLevel = logLevel;
    this.filterTraces = filterTraces;
  }

  @Override
  protected void configure() {
    bind(PrintWriter.class).toInstance(out);
    bind(Key.get(new TypeLiteral<Predicate<Report>>() {})).toInstance(filterTasks);
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
