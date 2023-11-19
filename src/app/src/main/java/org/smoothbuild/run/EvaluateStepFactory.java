package org.smoothbuild.run;

import static org.smoothbuild.out.log.Maybe.success;
import static org.smoothbuild.run.step.Step.constStep;
import static org.smoothbuild.run.step.Step.optionStep;
import static org.smoothbuild.run.step.Step.step;
import static org.smoothbuild.run.step.Step.stepFactory;

import io.vavr.Tuple0;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import org.smoothbuild.compile.backend.BackendCompile;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.run.eval.EvaluatorBFacade;
import org.smoothbuild.run.step.Step;
import org.smoothbuild.run.step.StepFactory;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

public class EvaluateStepFactory
    implements StepFactory<Tuple2<ScopeS, Array<String>>, Array<Tuple2<ExprS, ValueB>>> {
  @Override
  public Step<Tuple0, Array<Tuple2<ExprS, ValueB>>> create(Tuple2<ScopeS, Array<String>> argument) {
    return constStep(argument)
        .then(step(FindValues.class))
        .append(argument._1().evaluables())
        .then(stepFactory(arg -> constStep(arg)
            .then(step(BackendCompile.class))
            .then(optionStep(EvaluatorBFacade.class))
            .then(step(valueBs -> success(arg._1().zip(valueBs))))))
        .named("Evaluating");
  }
}
