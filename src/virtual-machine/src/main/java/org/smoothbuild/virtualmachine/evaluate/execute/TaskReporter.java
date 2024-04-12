package org.smoothbuild.virtualmachine.evaluate.execute;

public interface TaskReporter {
  public void report(TaskReport taskReport);

  public void reportEvaluationException(Throwable throwable);
}
