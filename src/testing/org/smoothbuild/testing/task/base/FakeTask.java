package org.smoothbuild.testing.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;

public class FakeTask implements Task {
  private final Value result;

  public FakeTask(Value object) {
    this.result = checkNotNull(object);
  }

  @Override
  public CallLocation location() {
    throw new UnsupportedOperationException(FakeTask.class.getSimpleName()
        + " does not have location.");
  }

  @Override
  public boolean isResultCalculated() {
    return true;
  }

  @Override
  public Value result() {
    return result;
  }

  @Override
  public void execute(Sandbox sandbox) {
    throw new UnsupportedOperationException("No need to call calculatedResult on "
        + FakeTask.class.getSimpleName() + ".");
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return Empty.taskList();
  }
}
