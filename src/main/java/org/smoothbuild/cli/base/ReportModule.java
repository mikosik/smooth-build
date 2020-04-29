package org.smoothbuild.cli.base;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.cli.taskmatcher.TaskMatcher;
import org.smoothbuild.cli.taskmatcher.TaskMatchers;

import com.google.inject.AbstractModule;

public class ReportModule extends AbstractModule {
  private final TaskMatcher taskMatcher;
  private final Level logLevel;

  public ReportModule() {
    this(TaskMatchers.ALL, Level.INFO);
  }

  public ReportModule(TaskMatcher taskMatcher, Level logLevel) {
    this.taskMatcher = taskMatcher;
    this.logLevel = logLevel;
  }

  @Override
  protected void configure() {
    bind(TaskMatcher.class).toInstance(taskMatcher);
    bind(Level.class).toInstance(logLevel);
  }
}
