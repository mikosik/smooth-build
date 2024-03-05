package org.smoothbuild.out.report;

import com.google.inject.AbstractModule;
import java.io.PrintWriter;
import org.smoothbuild.common.log.Level;

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
}
