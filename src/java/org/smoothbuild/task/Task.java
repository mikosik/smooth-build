package org.smoothbuild.task;

import org.smoothbuild.message.message.TaskLocation;
import org.smoothbuild.plugin.api.Sandbox;

import com.google.common.collect.ImmutableCollection;

public interface Task {
  public TaskLocation location();

  public boolean isResultCalculated();

  public Object result();

  public void execute(Sandbox sandbox);

  public ImmutableCollection<Task> dependencies();
}
