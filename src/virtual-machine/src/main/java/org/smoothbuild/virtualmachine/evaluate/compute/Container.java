package org.smoothbuild.virtualmachine.evaluate.compute;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.load.FileContentReader;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.dagger.Project;
import org.smoothbuild.virtualmachine.evaluate.plugin.MessageLogger;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

/**
 * This class is NOT thread-safe.
 */
public class Container implements NativeApi {
  private final FileSystem<FullPath> fileSystem;
  private final FullPath projectPath;
  private final FileContentReader fileContentReader;
  private final BytecodeFactory bytecodeFactory;
  private final MessageLoggerImpl messageLogger;
  private final NativeMethodLoader nativeMethodLoader;

  @Inject
  public Container(
      FileSystem<FullPath> fileSystem,
      @Project FullPath projectPath,
      FileContentReader fileContentReader,
      BytecodeFactory bytecodeFactory,
      NativeMethodLoader nativeMethodLoader) {
    this.fileSystem = fileSystem;
    this.projectPath = projectPath;
    this.fileContentReader = fileContentReader;
    this.bytecodeFactory = bytecodeFactory;
    this.messageLogger = new MessageLoggerImpl(bytecodeFactory);
    this.nativeMethodLoader = nativeMethodLoader;
  }

  public BFileCreator bFileCreator() {
    return new BFileCreator(fileSystem, projectPath, fileContentReader, bytecodeFactory);
  }

  @Override
  public BytecodeFactory factory() {
    return bytecodeFactory;
  }

  public NativeMethodLoader nativeMethodLoader() {
    return nativeMethodLoader;
  }

  @Override
  public MessageLogger log() {
    return messageLogger;
  }

  @Override
  public BArray messages() throws BytecodeException {
    return bytecodeFactory
        .arrayBuilderWithElements(bytecodeFactory.storedLogType())
        .addAll(messageLogger.messages)
        .build();
  }

  public boolean containsErrorOrAbove() {
    return messageLogger.containsErrorOrAbove;
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<BValue> messages = new ArrayList<>();
    private final BytecodeFactory bytecodeFactory;
    private boolean containsErrorOrAbove = false;

    public MessageLoggerImpl(BytecodeFactory bytecodeFactory) {
      this.bytecodeFactory = bytecodeFactory;
    }

    @Override
    public void fatal(String message) throws BytecodeException {
      messages.add(bytecodeFactory.fatalLog(message));
      containsErrorOrAbove = true;
    }

    @Override
    public void error(String message) throws BytecodeException {
      messages.add(bytecodeFactory.errorLog(message));
      containsErrorOrAbove = true;
    }

    @Override
    public void warning(String message) throws BytecodeException {
      messages.add(bytecodeFactory.warningLog(message));
    }

    @Override
    public void info(String message) throws BytecodeException {
      messages.add(bytecodeFactory.infoLog(message));
    }
  }
}
