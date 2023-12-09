package org.smoothbuild.run;

import static org.smoothbuild.out.log.Try.success;
import static org.smoothbuild.run.step.Step.constStep;
import static org.smoothbuild.run.step.Step.maybeStep;
import static org.smoothbuild.run.step.Step.step;
import static org.smoothbuild.run.step.Step.stepFactory;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compile.backend.BackendCompile;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.run.eval.EvaluatorBFacade;
import org.smoothbuild.run.step.Step;
import org.smoothbuild.run.step.StepFactory;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

public class EvaluateStepFactory
    implements StepFactory<Tuple2<ScopeS, List<String>>, List<Tuple2<ExprS, ValueB>>> {
  @Override
  public Step<Tuple0, List<Tuple2<ExprS, ValueB>>> create(Tuple2<ScopeS, List<String>> argument) {
    return constStep(argument)
        .then(step(FindValues.class))
        .append(argument.element1().evaluables())
        .then(stepFactory(arg -> constStep(arg)
            .then(step(BackendCompile.class))
            .then(maybeStep(EvaluatorBFacade.class))
            .then(step(valueBs -> success(arg.element1().zip(valueBs, Tuple2::new))))))
        .named("Evaluating");
  }
}
