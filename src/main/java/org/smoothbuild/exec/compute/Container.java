package org.smoothbuild.exec.compute;

import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.plugin.MessageLogger;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is NOT thread-safe.
 */
public class Container implements NativeApi {
  private final FileSystem fileSystem;
  private final ObjectFactory objectFactory;
  private final MessageLoggerImpl messageLogger;

  @Inject
  public Container(@ForSpace(PRJ) FileSystem fileSystem, ObjectFactory objectFactory) {
    this.fileSystem = fileSystem;
    this.objectFactory = objectFactory;
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

  @Override
  public Array messages() {
    return objectFactory.arrayBuilder(objectFactory.messageType())
        .addAll(messageLogger.messages)
        .build();
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<Obj> messages = new ArrayList<>();
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
