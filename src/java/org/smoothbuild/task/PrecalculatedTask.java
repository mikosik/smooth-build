package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;

public class PrecalculatedTask implements Task {
  private final Object result;

  public PrecalculatedTask(Object object) {
    this.result = checkNotNull(object);
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
  public void calculateResult(Sandbox sandbox) {
    throw new UnsupportedOperationException(
        "No need to call calculatedResult on PrecalculatedTask.");
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return Empty.taskList();
  }
}
