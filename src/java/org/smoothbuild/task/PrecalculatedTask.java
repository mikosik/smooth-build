package org.smoothbuild.task;

import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.util.Empty;

public class PrecalculatedTask extends AbstractTask {

  public PrecalculatedTask(Object object) {
    super(object, Empty.stringTaskMap());
  }

  @Override
  public void calculateResult(Sandbox sandbox) {
    throw new UnsupportedOperationException(
        "No need to call calculatedResult on PrecalculatedTask.");
  }
}
