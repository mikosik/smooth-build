package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.exec.HashedTasks;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;
import com.google.common.hash.HashCode;

public class StringTask implements Task {
  private final StringValue string;

  public StringTask(StringValue string) {
    this.string = checkNotNull(string);
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
    return string.hash();
  }
}
