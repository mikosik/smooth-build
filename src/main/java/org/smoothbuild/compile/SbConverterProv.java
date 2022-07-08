package org.smoothbuild.compile;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.load.FileLoader;

public class SbConverterProv {
  private final TypeSbConverter typeSbConverter;
  private final BytecodeF bytecodeF;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public SbConverterProv(TypeSbConverter typeSbConverter, BytecodeF bytecodeF,
      FileLoader fileLoader, BytecodeLoader bytecodeLoader) {
    this.typeSbConverter = typeSbConverter;
    this.bytecodeF = bytecodeF;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
  }

  public SbConverter get(DefsS defsS) {
    return new SbConverter(bytecodeF, defsS, typeSbConverter, fileLoader, bytecodeLoader);
  }
}
