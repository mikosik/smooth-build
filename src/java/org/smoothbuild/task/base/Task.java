package org.smoothbuild.task.base;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.Sandbox;

import com.google.common.collect.ImmutableCollection;

public interface Task {
  public CallLocation location();

  public boolean isResultCalculated();

  public Object result();

  public void execute(Sandbox sandbox);

  public ImmutableCollection<Task> dependencies();
}
