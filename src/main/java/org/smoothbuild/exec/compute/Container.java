package org.smoothbuild.exec.compute;

import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.plugin.MessageLogger;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is NOT thread-safe.
 */
public class Container implements NativeApi {
  private final FileSystem fileSystem;
  private final ObjFactory objFactory;
  private final MessageLoggerImpl messageLogger;

  @Inject
  public Container(@ForSpace(PRJ) FileSystem fileSystem, ObjFactory objFactory) {
    this.fileSystem = fileSystem;
    this.objFactory = objFactory;
    this.messageLogger = new MessageLoggerImpl(objFactory);
  }

  @Override
  public ObjFactory factory() {
    return objFactory;
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
    return objFactory.arrayBuilderWithElems(objFactory.messageT())
        .addAll(messageLogger.messages)
        .build();
  }

  private static class MessageLoggerImpl implements MessageLogger {
    private final List<ValB> messages = new ArrayList<>();
    private final ObjFactory objFactory;

    public MessageLoggerImpl(ObjFactory objFactory) {
      this.objFactory = objFactory;
    }

    @Override
    public void error(String message) {
      messages.add(objFactory.errorMessage(message));
    }

    @Override
    public void warning(String message) {
      messages.add(objFactory.warningMessage(message));
    }

    @Override
    public void info(String message) {
      messages.add(objFactory.infoMessage(message));
    }
  }
}
