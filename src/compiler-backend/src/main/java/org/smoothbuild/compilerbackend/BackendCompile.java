package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.compilerbackend.BackendCompilerConstants.COMPILE_BACK_LABEL;
import static org.smoothbuild.compilerbackend.CompiledExprs.compilationResult;

import jakarta.inject.Inject;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task2;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;

public class BackendCompile
    implements Task2<CompiledExprs, List<SExpr>, ImmutableBindings<SNamedEvaluable>> {
  private final SbTranslatorFactory sbTranslatorFactory;

  @Inject
  public BackendCompile(SbTranslatorFactory sbTranslatorFactory) {
    this.sbTranslatorFactory = sbTranslatorFactory;
  }

  @Override
  public Output<CompiledExprs> execute(
      List<SExpr> sExprs, ImmutableBindings<SNamedEvaluable> evaluables) {
    var label = COMPILE_BACK_LABEL.append("generateBytecode");
    var sbTranslator = sbTranslatorFactory.create(evaluables);
    try {
      var bExprs = sExprs.map(sbTranslator::translateExpr);
      var bsMapping = sbTranslator.bsMapping();
      var result = compilationResult(sExprs, bExprs, bsMapping);
      return output(result, label, list());
    } catch (SbTranslatorException e) {
      return output(label, list(fatal(e.getMessage())));
    }
  }
}
