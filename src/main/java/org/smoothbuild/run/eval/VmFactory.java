package org.smoothbuild.run.eval;

import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.vm.Vm;

@FunctionalInterface
public interface VmFactory {
  public Vm newVm(BsMapping bsMapping);
}
