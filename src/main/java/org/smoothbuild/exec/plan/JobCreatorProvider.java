package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.type.TypeFactoryB;
import org.smoothbuild.db.object.type.TypingB;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Nal;

import com.google.common.collect.ImmutableMap;

public class JobCreatorProvider {
  private final MethodLoader methodLoader;
  private final TypeFactoryB typeFactoryB;
  private final TypingB typingB;

  @Inject
  public JobCreatorProvider(MethodLoader methodLoader, TypeFactoryB typeFactoryB, TypingB typingB) {
    this.methodLoader = methodLoader;
    this.typeFactoryB = typeFactoryB;
    this.typingB = typingB;
  }

  public JobCreator get(ImmutableMap<ObjB, Nal> nals) {
    return new JobCreator(methodLoader, typingB, nals);
  }
}
