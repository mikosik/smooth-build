package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.hash.HashTask;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;
import com.google.common.hash.HashCode;

public class StringTask implements Task {
  private final String string;
  private final HashCode hash;

  public StringTask(String string) {
    this.string = checkNotNull(string);
    this.hash = HashTask.string(string);
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
    return string;
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
