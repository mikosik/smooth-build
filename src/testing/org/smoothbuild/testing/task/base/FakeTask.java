package org.smoothbuild.testing.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.HashedTasks;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;
import com.google.common.hash.HashCode;

public class FakeTask implements Task {
  private final Object result;
  private final HashCode hash;

  public FakeTask(Object object) {
    this.result = checkNotNull(object);
    this.hash = HashCode.fromInt(object.hashCode());
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
  public Object result() {
    return result;
  }

  @Override
  public void execute(Sandbox sandbox, HashedTasks hashedTasks) {
    throw new UnsupportedOperationException("No need to call calculatedResult on "
        + FakeTask.class.getSimpleName() + ".");
  }

  @Override
  public ImmutableCollection<HashCode> dependencies() {
    return Empty.hashCodeList();
  }

  @Override
  public HashCode hash() {
    return hash;
  }
}
