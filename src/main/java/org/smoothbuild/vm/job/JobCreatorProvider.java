package org.smoothbuild.vm.job;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.type.TypeBF;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.vm.java.MethodLoader;

import com.google.common.collect.ImmutableMap;

public class JobCreatorProvider {
  private final MethodLoader methodLoader;
  private final TypeBF typeBF;
  private final TypingB typingB;

  @Inject
  public JobCreatorProvider(MethodLoader methodLoader, TypeBF typeBF, TypingB typingB) {
    this.methodLoader = methodLoader;
    this.typeBF = typeBF;
    this.typingB = typingB;
  }

  public JobCreator get(ImmutableMap<ObjB, Nal> nals) {
    return new JobCreator(methodLoader, typingB, nals);
  }
}
