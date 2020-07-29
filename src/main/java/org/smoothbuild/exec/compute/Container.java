package org.smoothbuild.exec.compute;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.db.RecordFactory;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.plugin.MessageLogger;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is NOT thread-safe.
 */
public class Container implements NativeApi, Closeable {
  private final FileSystem fileSystem;
  private final RecordFactory recordFactory;
  private final TempManager tempManager;
  private final List<TempDir> tempDirs;
  private final MessageLoggerImpl messageLogger;

  @Inject
  public Container(FileSystem fileSystem, RecordFactory recordFactory, TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.recordFactory = recordFactory;
    this.tempManager = tempManager;
    this.tempDirs = new ArrayList<>();
    this.messageLogger = new MessageLoggerImpl(recordFactory);
  }

  @Override
  public RecordFactory factory() {
    return recordFactory;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public MessageLogger log() {
    return messageLogger;
  }

  @Override
  public Array messages() {
    return recordFactory.arrayBuilder(recordFactory.messageSpec())
        .addAll(messageLogger.messages)
        .build();
  }

  @Override
  public TempDir createTempDir() throws IOException {
    TempDir tempDir = tempManager.tempDir(this);
    tempDirs.add(tempDir);
    return tempDir;
  }

  @Override
  public void close() throws IOException {
    for (TempDir tempDir : tempDirs) {
      tempDir.destroy();
    }
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<Record> messages = new ArrayList<>();
    private final RecordFactory recordFactory;

    public MessageLoggerImpl(RecordFactory recordFactory) {
      this.recordFactory = recordFactory;
    }

    @Override
    public void error(String message) {
      messages.add(recordFactory.errorMessage(message));
    }

    @Override
    public void warning(String message) {
      messages.add(recordFactory.warningMessage(message));
    }

    @Override
    public void info(String message) {
      messages.add(recordFactory.infoMessage(message));
    }
  }
}
