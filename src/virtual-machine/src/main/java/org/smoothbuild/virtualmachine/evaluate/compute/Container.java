package org.smoothbuild.virtualmachine.evaluate.compute;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.wire.Project;

/**
 * This class is NOT thread-safe.
 */
public class Container implements NativeApi {
  private final Bucket bucket;
  private final BytecodeFactory bytecodeFactory;
  private final ContainerMessageLoggerImpl messageLogger;
  private final NativeMethodLoader nativeMethodLoader;

  @Inject
  public Container(
      @Project Bucket bucket,
      BytecodeFactory bytecodeFactory,
      NativeMethodLoader nativeMethodLoader) {
    this.bucket = bucket;
    this.bytecodeFactory = bytecodeFactory;
    this.messageLogger = new ContainerMessageLoggerImpl(bytecodeFactory);
    this.nativeMethodLoader = nativeMethodLoader;
  }

  public Bucket bucket() {
    return bucket;
  }

  @Override
  public BytecodeFactory factory() {
    return bytecodeFactory;
  }

  public NativeMethodLoader nativeMethodLoader() {
    return nativeMethodLoader;
  }

  @Override
  public ContainerMessageLogger log() {
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

  private static class ContainerMessageLoggerImpl implements ContainerMessageLogger {
    private final List<BValue> messages = new ArrayList<>();
    private final BytecodeFactory bytecodeFactory;
    private boolean containsErrorOrAbove = false;

    public ContainerMessageLoggerImpl(BytecodeFactory bytecodeFactory) {
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
