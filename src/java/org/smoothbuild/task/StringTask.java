package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.hash.HashTask;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;
import com.google.common.hash.HashCode;

public class StringTask implements Task {
  private final String result;
  private final HashCode hash;

  public StringTask(String object) {
    this.result = checkNotNull(object);
    this.hash = HashTask.string(object);
  }

  @Override
  public CallLocation location() {
    throw new UnsupportedOperationException(StringTask.class.getSimpleName()
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
    throw new UnsupportedOperationException(
        "No need to call calculatedResult on PrecalculatedTask.");
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
