package org.smoothbuild.evaluator;

import static org.smoothbuild.common.dag.Dag.apply2;
import static org.smoothbuild.common.dag.Dag.applyMaybeFunction;
import static org.smoothbuild.common.dag.Dag.evaluate;
import static org.smoothbuild.common.dag.Dag.prefix;
import static org.smoothbuild.common.dag.Dag.value;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.ModuleFrontendCompilationDag.frontendCompilationDag;
import static org.smoothbuild.evaluator.EvaluateConstants.EVALUATE;

import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dag.Dag;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;

public class SmoothEvaluationDag {
  public static Dag<List<Tuple2<ExprS, ValueB>>> smoothEvaluationDag(
      List<FullPath> modules, List<String> names) {
    Dag<ScopeS> scopeS = frontendCompilationDag(modules);
    return evaluate(apply2(SmoothEvaluationDag::subDag1, scopeS, value(names)));
  }

  public static Try<Dag<List<Tuple2<ExprS, ValueB>>>> subDag1(
      ScopeS scopeS, List<String> valueNames) {
    var scopeNode = value(scopeS);
    var valuesS = apply2(FindValues.class, scopeNode, value(valueNames));
    var evaluationGraph = evaluate(apply2(SmoothEvaluationDag::subDag2, scopeNode, valuesS));
    return success(prefix(EVALUATE, evaluationGraph));
  }

  public static Try<Dag<List<Tuple2<ExprS, ValueB>>>> subDag2(ScopeS scopeS, List<ExprS> values) {
    var valuesS = value(values);
    var compiledToBytecode = apply2(BackendCompile.class, valuesS, value(scopeS.evaluables()));
    var valueBs = applyMaybeFunction(EvaluatorBFacade.class, compiledToBytecode);
    var zipped = apply2(SmoothEvaluationDag::zip, valuesS, valueBs);
    return success(zipped);
  }

  private static Try<List<Tuple2<ExprS, ValueB>>> zip(List<ExprS> exprs, List<ValueB> valuesB) {
    return success(exprs.zip(valuesB, Tuple2::new));
  }
}
