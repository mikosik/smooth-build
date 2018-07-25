package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

@FunctionalInterface
public interface EvaluatorTypeChooser {

  public abstract ConcreteType choose(List<Dag<Evaluator>> childrenEvaluators);

  public static EvaluatorTypeChooser fixedTypeChooser(ConcreteType type) {
    return (List<Dag<Evaluator>> childrenEvaluators) -> type;
  }

  public static EvaluatorTypeChooser arrayOfFirstChildType(RuntimeTypes types) {
    return (childrenEvaluators) -> types.array(
        childrenEvaluators.iterator().next().elem().resultType());
  }
}
