package org.smoothbuild.eval.compile;

import javax.inject.Inject;

import org.smoothbuild.bytecode.ByteCodeFactory;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.vm.java.FileLoader;

public class CompilerProv {
  private final TypeShConv typeShConv;
  private final ByteCodeFactory byteCodeFactory;
  private final TypingB typing;
  private final FileLoader fileLoader;

  @Inject
  public CompilerProv(TypeShConv typeShConv, ByteCodeFactory byteCodeFactory, TypingB typing,
      FileLoader fileLoader) {
    this.typeShConv = typeShConv;
    this.byteCodeFactory = byteCodeFactory;
    this.typing = typing;
    this.fileLoader = fileLoader;
  }

  public Compiler get(DefsS defsS) {
    return new Compiler(byteCodeFactory, typing, defsS, typeShConv, fileLoader);
  }
}
