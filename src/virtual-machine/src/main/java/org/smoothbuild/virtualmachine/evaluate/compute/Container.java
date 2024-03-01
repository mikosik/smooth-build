package org.smoothbuild.virtualmachine.evaluate.compute;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.ProjectFs;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

/**
 * This class is NOT thread-safe.
 */
public class Container implements NativeApi {
  private final FileSystem fileSystem;
  private final BytecodeF bytecodeF;
  private final ContainerMessageLoggerImpl messageLogger;
  private final NativeMethodLoader nativeMethodLoader;

  @Inject
  public Container(
      @ProjectFs FileSystem fileSystem,
      BytecodeF bytecodeF,
      NativeMethodLoader nativeMethodLoader) {
    this.fileSystem = fileSystem;
    this.bytecodeF = bytecodeF;
    this.messageLogger = new ContainerMessageLoggerImpl(bytecodeF);
    this.nativeMethodLoader = nativeMethodLoader;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public BytecodeF factory() {
    return bytecodeF;
  }

  public NativeMethodLoader nativeMethodLoader() {
    return nativeMethodLoader;
  }

  @Override
  public ContainerMessageLogger log() {
    return messageLogger;
  }

  @Override
  public ArrayB messages() throws BytecodeException {
    return bytecodeF
        .arrayBuilderWithElements(bytecodeF.messageT())
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
    public void fatal(String message) throws BytecodeException {
      messages.add(bytecodeF.fatalMessage(message));
      containsErrorOrAbove = true;
    }

    @Override
    public void error(String message) throws BytecodeException {
      messages.add(bytecodeF.errorMessage(message));
      containsErrorOrAbove = true;
    }

    @Override
    public void warning(String message) throws BytecodeException {
      messages.add(bytecodeF.warningMessage(message));
    }

    @Override
    public void info(String message) throws BytecodeException {
      messages.add(bytecodeF.infoMessage(message));
    }
  }
}