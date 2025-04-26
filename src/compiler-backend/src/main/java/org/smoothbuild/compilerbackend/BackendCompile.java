package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.base.Throwables.concatenateExceptionMessages;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerbackend.BackendCompilerConstants.COMPILER_BACK_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;

public class BackendCompile implements Task2<List<SExpr>, Bindings<SPolyEvaluable>, CompiledExprs> {
  private final SbTranslatorFactory sbTranslatorFactory;

  @Inject
  public BackendCompile(SbTranslatorFactory sbTranslatorFactory) {
    this.sbTranslatorFactory = sbTranslatorFactory;
  }

  @Override
  public Output<CompiledExprs> execute(List<SExpr> sExprs, Bindings<SPolyEvaluable> evaluables) {
    var label = COMPILER_BACK_LABEL.append(":generateBytecode");
    var sbTranslator = sbTranslatorFactory.create(evaluables);
    try {
      var bExprs = sExprs.map(sbTranslator::translateExpr);
      var bExprAttributes = sbTranslator.bExprAttributes();
      var result = new CompiledExprs(bExprs, bExprAttributes);
      return output(result, label, list());
    } catch (SbTranslatorException e) {
      return output(label, list(fatal(concatenateExceptionMessages(e))));
    }
  }
}
