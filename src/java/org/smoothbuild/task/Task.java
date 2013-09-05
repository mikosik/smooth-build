package org.smoothbuild.task;

import org.smoothbuild.plugin.api.Sandbox;

import com.google.common.collect.ImmutableCollection;

public interface Task {
  public boolean isResultCalculated();

  public Object result();

  public void execute(Sandbox sandbox);

  public ImmutableCollection<Task> dependencies();
}
