package org.smoothbuild.task;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.ImmutableMap;

public class PrecalculatedTask extends AbstractTask {

  public PrecalculatedTask(Object object) {
    super(object, ImmutableMap.<String, Task> of());
  }

  @Override
  public void calculateResult(ProblemsListener problems, Path tempDir) {
    throw new UnsupportedOperationException(
        "No need to call calculatedResult on PrecalculatedTask.");
  }
}
