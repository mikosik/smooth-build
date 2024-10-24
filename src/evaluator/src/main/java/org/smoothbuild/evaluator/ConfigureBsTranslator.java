package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATE_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compilerbackend.CompiledExprs;

public class ConfigureBsTranslator implements Task1<CompiledExprs, Tuple0> {
  private final BsTranslator bsTranslator;

  @Inject
  public ConfigureBsTranslator(BsTranslator bsTranslator) {
    this.bsTranslator = bsTranslator;
  }

  @Override
  public Output<Tuple0> execute(CompiledExprs compiledExprs) {
    bsTranslator.setBsMapping(compiledExprs.bsMapping());
    return output(tuple(), EVALUATE_LABEL, list());
  }
}
