package org.smoothbuild.run.eval;

import com.google.inject.AbstractModule;
import org.smoothbuild.run.eval.report.TaskMatcher;

public class EvaluatorSModule extends AbstractModule {
  private final TaskMatcher taskMatcher;

  public EvaluatorSModule(TaskMatcher taskMatcher) {
    this.taskMatcher = taskMatcher;
  }

  @Override
  protected void configure() {
    bind(TaskMatcher.class).toInstance(taskMatcher);
  }
}
