package org.smoothbuild.run;

import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.step.Step.constStep;
import static org.smoothbuild.common.step.Step.maybeStep;
import static org.smoothbuild.common.step.Step.stepFactory;
import static org.smoothbuild.common.step.Step.tryStep;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepFactory;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compile.backend.BackendCompile;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.run.eval.EvaluatorBFacade;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;

public class EvaluateStepFactory
    implements StepFactory<Tuple2<ScopeS, List<String>>, List<Tuple2<ExprS, ValueB>>> {
  @Override
  public Step<Tuple0, List<Tuple2<ExprS, ValueB>>> create(Tuple2<ScopeS, List<String>> argument) {
    return constStep(argument)
        .then(tryStep(FindValues.class))
        .append(argument.element1().evaluables())
        .then(stepFactory(arg -> constStep(arg)
            .then(tryStep(BackendCompile.class))
            .then(maybeStep(EvaluatorBFacade.class))
            .then(tryStep(valueBs -> success(arg.element1().zip(valueBs, Tuple2::new))))))
        .named("Evaluating");
  }
}
