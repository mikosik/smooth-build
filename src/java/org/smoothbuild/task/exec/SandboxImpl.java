package org.smoothbuild.task.exec;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WrappedCodeMessage;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;
import org.smoothbuild.type.impl.MutableStoredFileSet;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final MutableStoredFileSet resultFileSet;
  private final MessageGroup messageGroup;
  private final CallLocation callLocation;

  public SandboxImpl(FileSystem fileSystem, Path root, CallLocation callLocation) {
    this(fileSystem, new SubFileSystem(fileSystem, root), callLocation,
        createMessages(callLocation));
  }

  public SandboxImpl(FileSystem fileSystem, FileSystem sandboxFileSystem,
      CallLocation callLocation, MessageGroup messageGroup) {
    this.projectFileSystem = fileSystem;
    this.resultFileSet = new MutableStoredFileSet(sandboxFileSystem);
    this.messageGroup = messageGroup;
    this.callLocation = callLocation;
  }

  @Override
  public MutableFileSet resultFileSet() {
    return resultFileSet;
  }

  @Override
  public MutableFile createFile(Path path) {
    return resultFileSet.createFile(path);
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
    return new MessageGroup(callLocation.name().simple());
  }
}
