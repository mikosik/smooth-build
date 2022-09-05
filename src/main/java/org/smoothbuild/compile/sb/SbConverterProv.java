package org.smoothbuild.compile.sb;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.load.FileLoader;

public class SbConverterProv {
  private final BytecodeF bytecodeF;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;

  @Inject
  public SbConverterProv(BytecodeF bytecodeF, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
  }

  public SbConverter get() {
    return new SbConverter(bytecodeF, fileLoader, bytecodeLoader);
  }
}
