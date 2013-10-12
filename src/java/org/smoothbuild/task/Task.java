package org.smoothbuild.task;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.api.Sandbox;

import com.google.common.collect.ImmutableCollection;
import com.google.common.hash.HashCode;

public interface Task {
  public CallLocation location();

  public boolean isResultCalculated();

  public Object result();

  public void execute(Sandbox sandbox);

  public ImmutableCollection<Task> dependencies();

  public HashCode hash();
}
