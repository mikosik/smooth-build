package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.exec.java.FileLoader;
import org.smoothbuild.lang.base.define.DefsS;

public class ShConvProv {
  private final TypeShConv typeShConv;
  private final ObjFactory objFactory;
  private final FileLoader fileLoader;

  @Inject
  public ShConvProv(TypeShConv typeShConv, ObjFactory objFactory, FileLoader fileLoader) {
    this.typeShConv = typeShConv;
    this.objFactory = objFactory;
    this.fileLoader = fileLoader;
  }

  public ShConv get(DefsS defsS) {
    return new ShConv(objFactory, defsS, typeShConv, fileLoader);
  }
}
