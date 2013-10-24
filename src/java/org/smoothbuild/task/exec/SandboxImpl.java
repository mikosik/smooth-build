package org.smoothbuild.task.exec;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WrappedCodeMessage;
import org.smoothbuild.object.FileBuilder;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.object.ObjectDb;
import org.smoothbuild.plugin.api.Sandbox;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final MessageGroup messageGroup;
  private final CallLocation callLocation;
  private final ObjectDb objectDb;

  public SandboxImpl(FileSystem fileSystem, ObjectDb objectDb, CallLocation callLocation) {
    this(fileSystem, objectDb, callLocation, createMessages(callLocation));
  }

  public SandboxImpl(FileSystem fileSystem, ObjectDb objectDb, CallLocation callLocation,
      MessageGroup messageGroup) {
    this.projectFileSystem = fileSystem;
    this.objectDb = objectDb;
    this.messageGroup = messageGroup;
    this.callLocation = callLocation;
  }

  @Override
  public FileBuilder fileBuilder() {
    return new FileBuilder(objectDb);
  }

  @Override
  public FileSetBuilder fileSetBuilder() {
    return new FileSetBuilder(objectDb);
  }

  public FileSystem projectFileSystem() {
    return projectFileSystem;
  }

  public ObjectDb objectDb() {
    return objectDb;
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
