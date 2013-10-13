package org.smoothbuild.task.base;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.exec.HashedTasks;

import com.google.common.collect.ImmutableCollection;
import com.google.common.hash.HashCode;

public interface Task {
  public CallLocation location();

  public boolean isResultCalculated();

  public Object result();

  public void execute(Sandbox sandbox, HashedTasks hashedTasks);

  public ImmutableCollection<HashCode> dependencies();

  public HashCode hash();
}
