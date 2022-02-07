package org.smoothbuild.out.report;

import org.smoothbuild.out.log.Level;
import org.smoothbuild.vm.parallel.TaskReporter;

import com.google.inject.AbstractModule;

public class ReportModule extends AbstractModule {
  private final Level logLevel;
  private final TaskMatcher taskMatcher;

  public ReportModule(Level logLevel, TaskMatcher taskMatcher) {
    this.logLevel = logLevel;
    this.taskMatcher = taskMatcher;
  }

  @Override
  protected void configure() {
    bind(Level.class).toInstance(logLevel);
    bind(TaskMatcher.class).toInstance(taskMatcher);
    bind(Reporter.class).to(ConsoleReporter.class);
    bind(TaskReporter.class).to(ConsoleReporter.class);
  }
}
