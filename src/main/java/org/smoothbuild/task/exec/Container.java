package org.smoothbuild.task.exec;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.MessagesDb;
import org.smoothbuild.lang.plugin.MessageLogger;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.value.ValueFactory;

public class Container implements NativeApi {
  private final FileSystem fileSystem;
  private final ValueFactory valueFactory;
  private final Types types;
  private final TempManager tempManager;
  private final List<TempDir> tempDirs;
  private final MessageLoggerImpl messageLogger;

  @Inject
  public Container(FileSystem fileSystem, ValueFactory valueFactory, Types types,
      MessagesDb messagesDb, TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.valueFactory = valueFactory;
    this.types = types;
    this.tempManager = tempManager;
    this.tempDirs = new ArrayList<>();
    this.messageLogger = new MessageLoggerImpl(messagesDb);
  }

  @Override
  public ValueFactory create() {
    return valueFactory;
  }

  @Override
  public Types types() {
    return types;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public MessageLogger log() {
    return messageLogger;
  }

  public List<Message> messages() {
    return messageLogger.messages;
  }

  @Override
  public TempDir createTempDir() {
    TempDir tempDir = tempManager.tempDir(this);
    tempDirs.add(tempDir);
    return tempDir;
  }

  public void destroy() {
    tempDirs.stream().forEach(TempDir::destroy);
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<Message> messages = new ArrayList<>();
    private final MessagesDb messagesDb;

    public MessageLoggerImpl(MessagesDb messagesDb) {
      this.messagesDb = messagesDb;
    }

    @Override
    public void error(String message) {
      messages.add(messagesDb.error(message));
    }

    @Override
    public void warning(String message) {
      messages.add(messagesDb.warning(message));
    }

    @Override
    public void info(String message) {
      messages.add(messagesDb.info(message));
    }
  }
}
