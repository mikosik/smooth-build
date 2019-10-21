package org.smoothbuild.task.exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.plugin.MessageLogger;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;
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
      TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.valueFactory = valueFactory;
    this.types = types;
    this.tempManager = tempManager;
    this.tempDirs = new ArrayList<>();
    this.messageLogger = new MessageLoggerImpl(valueFactory);
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

  public Array messages() {
    return valueFactory.arrayBuilder(types.message()).addAll(messageLogger.messages).build();
  }

  @Override
  public TempDir createTempDir() throws IOException {
    TempDir tempDir = tempManager.tempDir(this);
    tempDirs.add(tempDir);
    return tempDir;
  }

  public void destroy() throws IOException {
    for (TempDir tempDir : tempDirs) {
      tempDir.destroy();
    }
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<Value> messages = new ArrayList<>();
    private final ValueFactory valueFactory;

    public MessageLoggerImpl(ValueFactory valueFactory) {
      this.valueFactory = valueFactory;
    }

    @Override
    public void error(String message) {
      messages.add(valueFactory.errorMessage(message));
    }

    @Override
    public void warning(String message) {
      messages.add(valueFactory.warningMessage(message));
    }

    @Override
    public void info(String message) {
      messages.add(valueFactory.infoMessage(message));
    }
  }
}
