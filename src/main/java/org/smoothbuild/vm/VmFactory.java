package org.smoothbuild.vm;

import org.smoothbuild.compile.sb.BsMapping;

@FunctionalInterface
public interface VmFactory {
  public Vm newVm(BsMapping bsMapping);
}
