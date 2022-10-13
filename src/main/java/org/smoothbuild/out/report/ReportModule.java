package org.smoothbuild.out.report;

import org.smoothbuild.out.log.Level;

import com.google.inject.AbstractModule;

public class ReportModule extends AbstractModule {
  private final Level logLevel;

  public ReportModule(Level logLevel) {
    this.logLevel = logLevel;
  }

  @Override
  protected void configure() {
    bind(Level.class).toInstance(logLevel);
    bind(Reporter.class).to(ConsoleReporter.class);
  }
}
