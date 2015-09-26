package org.smoothbuild.cli.work;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;

import javax.inject.Inject;

import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.message.listen.UserConsole;

public class CleanWorker {
  private final FileSystem fileSystem;
  private final UserConsole userConsole;

  @Inject
  public CleanWorker(@ProjectDir FileSystem fileSystem, UserConsole userConsole) {
    this.fileSystem = fileSystem;
    this.userConsole = userConsole;
  }

  public int run() {
    try {
      fileSystem.delete(SMOOTH_DIR);
    } catch (Message e) {
      LoggedMessages messages = new LoggedMessages();
      messages.log(e);
      userConsole.print("CLEAN", messages);
    }
    userConsole.printFinalSummary();
    return userConsole.isProblemReported() ? 1 : 0;
  }
}
