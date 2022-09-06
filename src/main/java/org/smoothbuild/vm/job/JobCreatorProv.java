package org.smoothbuild.vm.job;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.vm.algorithm.NativeMethodLoader;

import com.google.common.collect.ImmutableMap;

public class JobCreatorProv {
  private final NativeMethodLoader nativeMethodLoader;
  private final BytecodeF bytecodeF;

  @Inject
  public JobCreatorProv(NativeMethodLoader nativeMethodLoader, BytecodeF bytecodeF) {
    this.nativeMethodLoader = nativeMethodLoader;
    this.bytecodeF = bytecodeF;
  }

  public JobCreator get(ImmutableMap<ExprB, ExprInfo> descriptions) {
    return new JobCreator(nativeMethodLoader, bytecodeF, descriptions);
  }
}
