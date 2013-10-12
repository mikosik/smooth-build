package org.smoothbuild.testing.task;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.Task;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;
import com.google.common.hash.HashCode;

public class TestTask implements Task {
  private final Object result;
  private final HashCode hash;

  public TestTask(Object object) {
    this.result = checkNotNull(object);
    this.hash = HashCode.fromInt(object.hashCode());
  }

  @Override
  public CallLocation location() {
    throw new UnsupportedOperationException(TestTask.class.getSimpleName()
        + " does not have location.");
  }

  @Override
  public boolean isResultCalculated() {
    return true;
  }

  @Override
  public Object result() {
    return result;
  }

  @Override
  public void execute(Sandbox sandbox) {
    throw new UnsupportedOperationException(
        "No need to call calculatedResult on PrecalculatedTask.");
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return Empty.taskList();
  }

  @Override
  public HashCode hash() {
    return hash;
  }
}
