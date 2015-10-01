package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.MainModule;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.message.listen.UserConsole;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;

public class CleanCommand implements Command {
  @Override
  public String shortDescription() {
    return "Remove all cached values and artifacts calculated during previous builds";
  }

  @Override
  public String longDescription() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth clean\n");
    builder.append("\n");
    builder.append(shortDescription());
    return builder.toString();
  }

  @Override
  public int execute(String[] args) {
    return Guice.createInjector(new MainModule()).getInstance(Clean.class).run(args);
  }

  public static class Clean {
    @Inject
    @ProjectDir
    private FileSystem fileSystem;
    @Inject
    private UserConsole userConsole;

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
}
