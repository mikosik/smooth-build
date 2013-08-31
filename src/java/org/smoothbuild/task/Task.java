package org.smoothbuild.task;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.ImmutableMap;

public interface Task {
  public boolean isResultCalculated();

  public Object result();

  public ImmutableMap<String, Task> dependencies();

  public void calculateResult(ProblemsListener problems, Path tempDir);

}
