package org.smoothbuild.app.report;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.LogCounters;

public class ReportModule extends AbstractModule {
  private final PrintWriter out;
  private final Level logLevel;

  public ReportModule(PrintWriter out, Level logLevel) {
    this.out = out;
    this.logLevel = logLevel;
  }

  @Override
  protected void configure() {
    bind(Level.class).toInstance(logLevel);
    bind(Reporter.class).to(PrintWriterReporter.class);
    bind(PrintWriter.class).toInstance(out);
  }

  @Provides
  @Singleton
  public LogCounters provideLogCounters() {
    return new LogCounters();
  }
}
