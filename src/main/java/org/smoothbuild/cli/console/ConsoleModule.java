package org.smoothbuild.cli.console;

import java.io.PrintWriter;

import org.smoothbuild.cli.taskmatcher.TaskMatcher;

import com.google.inject.AbstractModule;

public class ConsoleModule extends AbstractModule {
  private final PrintWriter out;
  private final Level logLevel;
  private final TaskMatcher taskMatcher;

  public ConsoleModule(PrintWriter out, Level logLevel, TaskMatcher taskMatcher) {
    this.out = out;
    this.logLevel = logLevel;
    this.taskMatcher = taskMatcher;
  }

  @Override
  protected void configure() {
    bind(PrintWriter.class).toInstance(out);
    bind(Level.class).toInstance(logLevel);
    bind(TaskMatcher.class).toInstance(taskMatcher);
  }
}
