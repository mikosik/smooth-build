package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Nal;

import com.google.common.collect.ImmutableMap;

public class JobCreatorProvider {
  private final MethodLoader methodLoader;
  private final TypeFactoryH typeFactoryH;
  private final TypingH typingH;

  @Inject
  public JobCreatorProvider(MethodLoader methodLoader, TypeFactoryH typeFactoryH, TypingH typingH) {
    this.methodLoader = methodLoader;
    this.typeFactoryH = typeFactoryH;
    this.typingH = typingH;
  }

  public JobCreator get(ImmutableMap<ObjH, Nal> nals) {
    return new JobCreator(methodLoader, typingH, nals);
  }
}
