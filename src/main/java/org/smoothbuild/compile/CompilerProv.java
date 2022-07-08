package org.smoothbuild.compile;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.load.FileLoader;

public class CompilerProv {
  private final TypeSbConverter typeSbConverter;
  private final BytecodeF bytecodeF;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public CompilerProv(TypeSbConverter typeSbConverter, BytecodeF bytecodeF, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader) {
    this.typeSbConverter = typeSbConverter;
    this.bytecodeF = bytecodeF;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
  }

  public Compiler get(DefsS defsS) {
    return new Compiler(bytecodeF, defsS, typeSbConverter, fileLoader, bytecodeLoader);
  }
}
