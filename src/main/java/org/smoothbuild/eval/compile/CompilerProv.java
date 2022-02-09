package org.smoothbuild.eval.compile;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.vm.java.FileLoader;

public class CompilerProv {
  private final TypeSbConv typeSbConv;
  private final BytecodeF bytecodeF;
  private final TypingB typing;
  private final FileLoader fileLoader;

  @Inject
  public CompilerProv(TypeSbConv typeSbConv, BytecodeF bytecodeF, TypingB typing,
      FileLoader fileLoader) {
    this.typeSbConv = typeSbConv;
    this.bytecodeF = bytecodeF;
    this.typing = typing;
    this.fileLoader = fileLoader;
  }

  public Compiler get(DefsS defsS) {
    return new Compiler(bytecodeF, typing, defsS, typeSbConv, fileLoader);
  }
}
