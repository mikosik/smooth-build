package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.object.Hashed;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;

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
  public Hashed result() {
    return string;
  }

  @Override
  public void execute(Sandbox sandbox) {
    throw new UnsupportedOperationException("No need to call calculatedResult on "
        + StringTask.class.getSimpleName() + ".");
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return Empty.taskList();
  }
}
