package org.smoothbuild.vm.job;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.vm.algorithm.NativeMethodLoader;

import com.google.common.collect.ImmutableMap;

public class JobCreatorProv {
  private final NativeMethodLoader nativeMethodLoader;
  private final TypingB typingB;
  private final BytecodeF bytecodeF;

  @Inject
  public JobCreatorProv(NativeMethodLoader nativeMethodLoader, TypingB typingB,
      BytecodeF bytecodeF) {
    this.nativeMethodLoader = nativeMethodLoader;
    this.typingB = typingB;
    this.bytecodeF = bytecodeF;
  }

  public JobCreator get(ImmutableMap<ObjB, Nal> nals) {
    return new JobCreator(nativeMethodLoader, typingB, bytecodeF, nals);
  }
}
