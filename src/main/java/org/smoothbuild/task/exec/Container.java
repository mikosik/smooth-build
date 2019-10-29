package org.smoothbuild.task.exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.plugin.MessageLogger;
import org.smoothbuild.lang.plugin.NativeApi;

public class Container implements NativeApi {
  private final FileSystem fileSystem;
  private final ObjectFactory objectFactory;
  private final TempManager tempManager;
  private final List<TempDir> tempDirs;
  private final MessageLoggerImpl messageLogger;

  @Inject
  public Container(FileSystem fileSystem, ObjectFactory objectFactory, TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.objectFactory = objectFactory;
    this.tempManager = tempManager;
    this.tempDirs = new ArrayList<>();
    this.messageLogger = new MessageLoggerImpl(objectFactory);
  }

  @Override
  public ObjectFactory factory() {
    return objectFactory;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public MessageLogger log() {
    return messageLogger;
  }

  public Array messages() {
    return objectFactory.arrayBuilder(objectFactory.messageType())
        .addAll(messageLogger.messages)
        .build();
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
    private final List<SObject> messages = new ArrayList<>();
    private final ObjectFactory objectFactory;

    public MessageLoggerImpl(ObjectFactory objectFactory) {
      this.objectFactory = objectFactory;
    }

    @Override
    public void error(String message) {
      messages.add(objectFactory.errorMessage(message));
    }

    @Override
    public void warning(String message) {
      messages.add(objectFactory.warningMessage(message));
    }

    @Override
    public void info(String message) {
      messages.add(objectFactory.infoMessage(message));
    }
  }
}
