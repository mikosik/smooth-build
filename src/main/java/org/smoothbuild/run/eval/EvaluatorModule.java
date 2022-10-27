package org.smoothbuild.run.eval;

import org.smoothbuild.run.eval.report.TaskMatcher;

import com.google.inject.AbstractModule;

public class EvaluatorModule extends AbstractModule {
  private final TaskMatcher taskMatcher;

  public EvaluatorModule(TaskMatcher taskMatcher) {
    this.taskMatcher = taskMatcher;
  }

  @Override
  protected void configure() {
    bind(TaskMatcher.class).toInstance(taskMatcher);
    bind(VmFactory.class).to(VmFactoryImpl.class);
  }
}
