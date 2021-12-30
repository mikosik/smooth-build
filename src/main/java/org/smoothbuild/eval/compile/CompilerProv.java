package org.smoothbuild.eval.compile;

import javax.inject.Inject;

import org.smoothbuild.db.bytecode.ObjFactory;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.vm.java.FileLoader;

public class CompilerProv {
  private final TypeShConv typeShConv;
  private final ObjFactory objFactory;
  private final FileLoader fileLoader;

  @Inject
  public CompilerProv(TypeShConv typeShConv, ObjFactory objFactory, FileLoader fileLoader) {
    this.typeShConv = typeShConv;
    this.objFactory = objFactory;
    this.fileLoader = fileLoader;
  }

  public Compiler get(DefsS defsS) {
    return new Compiler(objFactory, defsS, typeShConv, fileLoader);
  }
}
