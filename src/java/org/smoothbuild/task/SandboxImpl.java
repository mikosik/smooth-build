package org.smoothbuild.task;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.message.listen.CollectingMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WrappedCodeMessage;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.err.TaskCompletedInfo;
import org.smoothbuild.task.err.TaskFailedError;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;
import org.smoothbuild.type.impl.MutableStoredFileSet;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final MutableStoredFileSet resultFileSet;
  private final CollectingMessageListener messages;
  private final CallLocation callLocation;

  public SandboxImpl(FileSystem fileSystem, Path root, CallLocation callLocation) {
    this(fileSystem, new SubFileSystem(fileSystem, root), callLocation,
        new CollectingMessageListener());
  }

  public SandboxImpl(FileSystem fileSystem, FileSystem sandboxFileSystem,
      CallLocation callLocation, CollectingMessageListener messages) {
    this.projectFileSystem = fileSystem;
    this.resultFileSet = new MutableStoredFileSet(sandboxFileSystem);
    this.messages = messages;
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
    messages.report(new WrappedCodeMessage(message, callLocation.location()));
  }

  public void reportCollectedMessagesTo(MessageListener listener) {
    if (messages.isErrorReported()) {
      listener.report(new TaskFailedError(callLocation));
    } else {
      listener.report(new TaskCompletedInfo(callLocation));
    }
    messages.reportCollectedMessagesTo(listener);
  }
}
