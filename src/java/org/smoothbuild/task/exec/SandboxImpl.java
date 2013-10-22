package org.smoothbuild.task.exec;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WrappedCodeMessage;
import org.smoothbuild.object.FileBuilder;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.object.ObjectsDb;
import org.smoothbuild.plugin.api.Sandbox;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final FileSystem sandboxFileSystem;
  private final MessageGroup messageGroup;
  private final CallLocation callLocation;
  private final ObjectsDb objectsDb;

  public SandboxImpl(FileSystem fileSystem, ObjectsDb objectsDb, Path root,
      CallLocation callLocation) {
    this(fileSystem, new SubFileSystem(fileSystem, root), objectsDb, callLocation,
        createMessages(callLocation));
  }

  public SandboxImpl(FileSystem fileSystem, FileSystem sandboxFileSystem, ObjectsDb objectsDb,
      CallLocation callLocation, MessageGroup messageGroup) {
    this.projectFileSystem = fileSystem;
    this.sandboxFileSystem = sandboxFileSystem;
    this.objectsDb = objectsDb;
    this.messageGroup = messageGroup;
    this.callLocation = callLocation;
  }

  @Override
  public FileBuilder fileBuilder() {
    return new FileBuilder(objectsDb);
  }

  @Override
  public FileSetBuilder fileSetBuilder() {
    return new FileSetBuilder(sandboxFileSystem);
  }

  public FileSystem projectFileSystem() {
    return projectFileSystem;
  }

  public ObjectsDb objectsDb() {
    return objectsDb;
  }

  @Override
  public void report(Message message) {
    // TODO Smooth StackTrace (list of CodeLocations) should be added here. This
    // will be possible when each Task will have parent field pointing in
    // direction to nearest root node (build run can have more than one
    // task-to-run [soon]).
    messageGroup.report(new WrappedCodeMessage(message, callLocation.location()));
  }

  public MessageGroup messageGroup() {
    return messageGroup;
  }

  private static MessageGroup createMessages(CallLocation callLocation) {
    return new MessageGroup(callLocation.name().simple());
  }
}
