package org.smoothbuild.task;

import org.smoothbuild.plugin.Sandbox;

import com.google.common.collect.ImmutableCollection;

public interface Task {
  public boolean isResultCalculated();

  public Object result();

  public ImmutableCollection<Task> dependencies();

  public void execute(Sandbox sandbox);

}
