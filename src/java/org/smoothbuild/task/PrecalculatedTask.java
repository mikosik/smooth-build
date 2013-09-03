package org.smoothbuild.task;

import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;

public class PrecalculatedTask extends AbstractTask {

  public PrecalculatedTask(Object object) {
    super(object);
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
