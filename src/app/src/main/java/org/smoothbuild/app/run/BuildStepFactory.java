package org.smoothbuild.app.run;

import static org.smoothbuild.app.run.EvaluateStep.evaluateStep;
import static org.smoothbuild.common.step.Step.tryStep;

import org.smoothbuild.app.layout.Layout;
import org.smoothbuild.app.run.eval.SaveArtifacts;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepFactory;
import org.smoothbuild.common.tuple.Tuple0;

public class BuildStepFactory implements StepFactory<List<String>, String> {
  @Override
  public Step<Tuple0, String> create(List<String> names) {
    return tryStep(RemoveArtifacts.class)
        .then(evaluateStep(Layout.MODULES, names))
        .then(tryStep(SaveArtifacts.class).named("Saving artifact(s)"));
  }
}
