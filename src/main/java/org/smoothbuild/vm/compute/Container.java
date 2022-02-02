package org.smoothbuild.vm.compute;

import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.bytecode.ByteCodeF;
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
  private final ByteCodeF byteCodeF;
  private final TypingB typing;
  private final MessageLoggerImpl messageLogger;

  @Inject
  public Container(@ForSpace(PRJ) FileSystem fileSystem, ByteCodeF byteCodeF, TypingB typing) {
    this.fileSystem = fileSystem;
    this.byteCodeF = byteCodeF;
    this.messageLogger = new MessageLoggerImpl(byteCodeF);
    this.typing = typing;
  }

  @Override
  public ByteCodeF factory() {
    return byteCodeF;
  }

  @Override
  public TypingB typing() {
    return typing;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public Unzipper unzipper() {
    return new Unzipper(this);
  }

  @Override
  public MessageLogger log() {
    return messageLogger;
  }

  @Override
  public ArrayB messages() {
    return byteCodeF.arrayBuilderWithElems(byteCodeF.messageT())
        .addAll(messageLogger.messages)
        .build();
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<ValB> messages = new ArrayList<>();
    private final ByteCodeF byteCodeF;

    public MessageLoggerImpl(ByteCodeF byteCodeF) {
      this.byteCodeF = byteCodeF;
    }

    @Override
    public void error(String message) {
      messages.add(byteCodeF.errorMessage(message));
    }

    @Override
    public void warning(String message) {
      messages.add(byteCodeF.warningMessage(message));
    }

    @Override
    public void info(String message) {
      messages.add(byteCodeF.infoMessage(message));
    }
  }
}
