package org.smoothbuild.eval.compile;

import javax.inject.Inject;

import org.smoothbuild.bytecode.ByteCodeFactory;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.vm.java.FileLoader;

public class CompilerProv {
  private final TypeShConv typeShConv;
  private final ByteCodeFactory byteCodeFactory;
  private final FileLoader fileLoader;

  @Inject
  public CompilerProv(TypeShConv typeShConv, ByteCodeFactory byteCodeFactory, FileLoader fileLoader) {
    this.typeShConv = typeShConv;
    this.byteCodeFactory = byteCodeFactory;
    this.fileLoader = fileLoader;
  }

  public Compiler get(DefsS defsS) {
    return new Compiler(byteCodeFactory, defsS, typeShConv, fileLoader);
  }
}
