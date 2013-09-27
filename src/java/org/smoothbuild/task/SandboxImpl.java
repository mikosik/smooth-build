package org.smoothbuild.task;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.message.CollectingMessageListener;
import org.smoothbuild.message.Message;
import org.smoothbuild.message.MessageListener;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.internal.MutableStoredFileSet;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final MutableStoredFileSet resultFileSet;
  private final CollectingMessageListener messages;

  public SandboxImpl(FileSystem fileSystem, Path root) {
    this(fileSystem, new SubFileSystem(fileSystem, root), new CollectingMessageListener());
  }

  public SandboxImpl(FileSystem fileSystem, FileSystem sandboxFileSystem,
      CollectingMessageListener messages) {
    this.projectFileSystem = fileSystem;
    this.resultFileSet = new MutableStoredFileSet(sandboxFileSystem);
    this.messages = messages;
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
    messages.report(message);
  }

  public void reportCollectedMessagesTo(String taskName, MessageListener listener) {
    if (taskName != null) {
      if (messages.isErrorReported()) {
        listener.report(new TaskFailedError(taskName));
      } else {
        listener.report(new TaskCompletedInfo(taskName));
      }
    }
    messages.reportCollectedMessagesTo(listener);
  }
}
