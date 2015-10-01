package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.message.listen.UserConsole;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Clean implements Command {
  @Inject
  @ProjectDir
  private FileSystem fileSystem;
  @Inject
  private UserConsole userConsole;

  @Override
  public int run(String... args) {
    List<String> unknownArgs = ImmutableList.copyOf(args).subList(1, args.length);
    if (!unknownArgs.isEmpty()) {
      LoggedMessages messages = new LoggedMessages();
      String message = "Unknown arguments: " + Iterables.toString(unknownArgs);
      messages.log(new Message(MessageType.ERROR, message));
      userConsole.print("CLEAN", messages);
    }
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
