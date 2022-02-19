package org.smoothbuild.compile;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.load.FileLoader;

public class CompilerProv {
  private final TypeSbConv typeSbConv;
  private final BytecodeF bytecodeF;
  private final TypingB typing;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public CompilerProv(TypeSbConv typeSbConv, BytecodeF bytecodeF, TypingB typing,
      FileLoader fileLoader, BytecodeLoader bytecodeLoader) {
    this.typeSbConv = typeSbConv;
    this.bytecodeF = bytecodeF;
    this.typing = typing;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
  }

  public Compiler get(DefsS defsS) {
    return new Compiler(bytecodeF, typing, defsS, typeSbConv, fileLoader, bytecodeLoader);
  }
}