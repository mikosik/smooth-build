package org.smoothbuild.vm.evaluate.compute;

import static org.smoothbuild.fs.space.Space.PRJ;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

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

  public boolean containsErrorOrAbove() {
    return messageLogger.containsErrorOrAbove;
  }

  private static class ContainerMessageLoggerImpl implements ContainerMessageLogger {
    private final List<ValueB> messages = new ArrayList<>();
    private final BytecodeF bytecodeF;
    private boolean containsErrorOrAbove = false;

    public ContainerMessageLoggerImpl(BytecodeF bytecodeF) {
      this.bytecodeF = bytecodeF;
    }

    @Override
    public void fatal(String message) {
      messages.add(bytecodeF.fatalMessage(message));
      containsErrorOrAbove = true;
    }

    @Override
    public void error(String message) {
      messages.add(bytecodeF.errorMessage(message));
      containsErrorOrAbove = true;
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
