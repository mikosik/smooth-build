package org.smoothbuild.run;

import static org.smoothbuild.common.step.Step.tryStep;
import static org.smoothbuild.run.EvaluateStep.evaluateStep;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepFactory;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.run.eval.SaveArtifacts;

public class BuildStepFactory implements StepFactory<List<String>, String> {
  @Override
  public Step<Tuple0, String> create(List<String> names) {
    return tryStep(RemoveArtifacts.class)
        .then(evaluateStep(names))
        .then(tryStep(SaveArtifacts.class).named("Saving artifact(s)"));
  }
}
