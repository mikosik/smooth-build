package org.smoothbuild.task.exec;

import org.smoothbuild.db.ValueDb;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WrappedCodeMessage;
import org.smoothbuild.plugin.FileBuilder;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringSetBuilder;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final MessageGroup messageGroup;
  private final CallLocation callLocation;
  private final ValueDb valueDb;

  public SandboxImpl(FileSystem fileSystem, ValueDb valueDb, CallLocation callLocation) {
    this(fileSystem, valueDb, callLocation, createMessages(callLocation));
  }

  public SandboxImpl(FileSystem fileSystem, ValueDb valueDb, CallLocation callLocation,
      MessageGroup messageGroup) {
    this.projectFileSystem = fileSystem;
    this.valueDb = valueDb;
    this.messageGroup = messageGroup;
    this.callLocation = callLocation;
  }

  @Override
  public FileSetBuilder fileSetBuilder() {
    return new FileSetBuilder(valueDb);
  }

  @Override
  public StringSetBuilder stringSetBuilder() {
    return new StringSetBuilder(valueDb);
  }

  @Override
  public FileBuilder fileBuilder() {
    return new FileBuilder(valueDb);
  }

  public FileSystem projectFileSystem() {
    return projectFileSystem;
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
    return new MessageGroup(callLocation.name());
  }
}
