package org.smoothbuild.cli.console;

import org.smoothbuild.cli.taskmatcher.TaskMatcher;

import com.google.inject.AbstractModule;

public class LoggerModule extends AbstractModule {
  private final Level logLevel;
  private final TaskMatcher taskMatcher;

  public LoggerModule(Level logLevel, TaskMatcher taskMatcher) {
    this.logLevel = logLevel;
    this.taskMatcher = taskMatcher;
  }

  @Override
  protected void configure() {
    bind(Level.class).toInstance(logLevel);
    bind(TaskMatcher.class).toInstance(taskMatcher);
    bind(Reporter.class).to(ConsoleReporter.class);
  }
}
