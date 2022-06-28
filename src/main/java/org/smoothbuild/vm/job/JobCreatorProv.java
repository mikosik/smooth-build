package org.smoothbuild.vm.job;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.lang.base.Nal;
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

  public JobCreator get(ImmutableMap<ObjB, Nal> nals) {
    return new JobCreator(nativeMethodLoader, bytecodeF, nals);
  }
}
