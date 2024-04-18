package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerbackend.BackendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerbackend.CompiledExprs.compilationResult;

import jakarta.inject.Inject;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.TryFunction2;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;

public class BackendCompile
    implements TryFunction2<List<SExpr>, ImmutableBindings<SNamedEvaluable>, CompiledExprs> {
  private final SbTranslatorFactory sbTranslatorFactory;

  @Inject
  public BackendCompile(SbTranslatorFactory sbTranslatorFactory) {
    this.sbTranslatorFactory = sbTranslatorFactory;
  }

  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "generateBytecode");
  }

  @Override
  public Try<CompiledExprs> apply(
      List<SExpr> sExprs, ImmutableBindings<SNamedEvaluable> evaluables) {
    var sbTranslator = sbTranslatorFactory.create(evaluables);
    try {
      var bExprs = sExprs.map(sbTranslator::translateExpr);
      var bsMapping = sbTranslator.bsMapping();
      return success(compilationResult(sExprs, bExprs, bsMapping));
    } catch (SbTranslatorException e) {
      return failure(fatal(e.getMessage()));
    }
  }
}
