package org.smoothbuild.compile.sb;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.load.FileLoader;

public class SbTranslatorProv {
  private final BytecodeF bytecodeF;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public SbTranslatorProv(BytecodeF bytecodeF, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
  }

  public SbTranslator get() {
    return new SbTranslator(bytecodeF, fileLoader, bytecodeLoader);
  }
}
