package org.smoothbuild.vm.compute;

import static org.smoothbuild.fs.space.Space.PRJ;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is NOT thread-safe.
 */
public class Container implements NativeApi {
  private final FileSystem fileSystem;
  private final BytecodeF bytecodeF;
  private final ContainerMessageLoggerImpl messageLogger;

  @Inject
  public Container(@ForSpace(PRJ) FileSystem fileSystem, BytecodeF bytecodeF) {
    this.fileSystem = fileSystem;
    this.bytecodeF = bytecodeF;
    this.messageLogger = new ContainerMessageLoggerImpl(bytecodeF);
  }

  @Override
  public BytecodeF factory() {
    return bytecodeF;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public ContainerMessageLogger log() {
    return messageLogger;
  }

  @Override
  public ArrayB messages() {
    return bytecodeF.arrayBuilderWithElems(bytecodeF.messageT())
        .addAll(messageLogger.messages)
        .build();
  }

  private static class ContainerMessageLoggerImpl implements ContainerMessageLogger {
    private final List<InstB> messages = new ArrayList<>();
    private final BytecodeF bytecodeF;

    public ContainerMessageLoggerImpl(BytecodeF bytecodeF) {
      this.bytecodeF = bytecodeF;
    }

    @Override
    public void fatal(String message) {
      messages.add(bytecodeF.fatalMessage(message));
    }

    @Override
    public void error(String message) {
      messages.add(bytecodeF.errorMessage(message));
    }

    @Override
    public void warning(String message) {
      messages.add(bytecodeF.warningMessage(message));
    }

    @Override
    public void info(String message) {
      messages.add(bytecodeF.infoMessage(message));
    }
  }
}
