package org.smoothbuild.task;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.message.listen.CollectingMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.TaskLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WrappedCodeMessage;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.internal.MutableStoredFileSet;
import org.smoothbuild.task.err.TaskCompletedInfo;
import org.smoothbuild.task.err.TaskFailedError;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final MutableStoredFileSet resultFileSet;
  private final CollectingMessageListener messages;
  private final TaskLocation taskLocation;

  public SandboxImpl(FileSystem fileSystem, Path root, TaskLocation taskLocation) {
    this(fileSystem, new SubFileSystem(fileSystem, root), taskLocation,
        new CollectingMessageListener());
  }

  public SandboxImpl(FileSystem fileSystem, FileSystem sandboxFileSystem,
      TaskLocation taskLocation, CollectingMessageListener messages) {
    this.projectFileSystem = fileSystem;
    this.resultFileSet = new MutableStoredFileSet(sandboxFileSystem);
    this.messages = messages;
    this.taskLocation = taskLocation;
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
    messages.report(new WrappedCodeMessage(message, taskLocation.location()));
  }

  public void reportCollectedMessagesTo(MessageListener listener) {
    if (messages.isErrorReported()) {
      listener.report(new TaskFailedError(taskLocation));
    } else {
      listener.report(new TaskCompletedInfo(taskLocation));
    }
    messages.reportCollectedMessagesTo(listener);
  }
}
