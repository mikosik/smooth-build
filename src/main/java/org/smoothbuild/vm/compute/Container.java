package org.smoothbuild.vm.compute;

import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.plugin.MessageLogger;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is NOT thread-safe.
 */
public class Container implements NativeApi {
  private final FileSystem fileSystem;
  private final BytecodeF bytecodeF;
  private final TypingB typing;
  private final MessageLoggerImpl messageLogger;

  @Inject
  public Container(@ForSpace(PRJ) FileSystem fileSystem, BytecodeF bytecodeF, TypingB typing) {
    this.fileSystem = fileSystem;
    this.bytecodeF = bytecodeF;
    this.messageLogger = new MessageLoggerImpl(bytecodeF);
    this.typing = typing;
  }

  @Override
  public BytecodeF factory() {
    return bytecodeF;
  }

  @Override
  public TypingB typing() {
    return typing;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public MessageLogger log() {
    return messageLogger;
  }

  @Override
  public ArrayB messages() {
    return bytecodeF.arrayBuilderWithElems(bytecodeF.messageT())
        .addAll(messageLogger.messages)
        .build();
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<ValB> messages = new ArrayList<>();
    private final BytecodeF bytecodeF;

    public MessageLoggerImpl(BytecodeF bytecodeF) {
      this.bytecodeF = bytecodeF;
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
