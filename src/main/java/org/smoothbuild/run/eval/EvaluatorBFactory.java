package org.smoothbuild.run.eval;

import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.vm.EvaluatorB;

@FunctionalInterface
public interface EvaluatorBFactory {
  public EvaluatorB newEvaluatorB(BsMapping bsMapping);
}
