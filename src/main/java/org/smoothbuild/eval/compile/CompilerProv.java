package org.smoothbuild.eval.compile;

import javax.inject.Inject;

import org.smoothbuild.bytecode.ByteCodeF;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.vm.java.FileLoader;

public class CompilerProv {
  private final TypeSbConv typeSbConv;
  private final ByteCodeF byteCodeF;
  private final TypingB typing;
  private final FileLoader fileLoader;

  @Inject
  public CompilerProv(TypeSbConv typeSbConv, ByteCodeF byteCodeF, TypingB typing,
      FileLoader fileLoader) {
    this.typeSbConv = typeSbConv;
    this.byteCodeF = byteCodeF;
    this.typing = typing;
    this.fileLoader = fileLoader;
  }

  public Compiler get(DefsS defsS) {
    return new Compiler(byteCodeF, typing, defsS, typeSbConv, fileLoader);
  }
}
