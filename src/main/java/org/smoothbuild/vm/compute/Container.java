package org.smoothbuild.vm.compute;

import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.bytecode.ByteCodeFactory;
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
  private final ByteCodeFactory byteCodeFactory;
  private final TypingB typing;
  private final MessageLoggerImpl messageLogger;

  @Inject
  public Container(@ForSpace(PRJ) FileSystem fileSystem, ByteCodeFactory byteCodeFactory,
      TypingB typing) {
    this.fileSystem = fileSystem;
    this.byteCodeFactory = byteCodeFactory;
    this.messageLogger = new MessageLoggerImpl(byteCodeFactory);
    this.typing = typing;
  }

  @Override
  public ByteCodeFactory factory() {
    return byteCodeFactory;
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
    return byteCodeFactory.arrayBuilderWithElems(byteCodeFactory.messageT())
        .addAll(messageLogger.messages)
        .build();
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<ValB> messages = new ArrayList<>();
    private final ByteCodeFactory byteCodeFactory;

    public MessageLoggerImpl(ByteCodeFactory byteCodeFactory) {
      this.byteCodeFactory = byteCodeFactory;
    }

    @Override
    public void error(String message) {
      messages.add(byteCodeFactory.errorMessage(message));
    }

    @Override
    public void warning(String message) {
      messages.add(byteCodeFactory.warningMessage(message));
    }

    @Override
    public void info(String message) {
      messages.add(byteCodeFactory.infoMessage(message));
    }
  }
}
