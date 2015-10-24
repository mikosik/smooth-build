package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;

import java.io.PrintStream;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.message.base.Console;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Clean implements Command {
  @Inject
  @ProjectDir
  private FileSystem fileSystem;
  @Inject
  @Console
  private PrintStream console;

  @Override
  public int run(String... args) {
    List<String> unknownArgs = ImmutableList.copyOf(args).subList(1, args.length);
    if (!unknownArgs.isEmpty()) {
      console.println("error: Unknown arguments: " + Iterables.toString(unknownArgs));
      return EXIT_CODE_ERROR;
    }
    try {
      fileSystem.delete(SMOOTH_DIR);
    } catch (FileSystemError e) {
      console.println("error: " + e.getMessage());
      return EXIT_CODE_ERROR;
    }
    return EXIT_CODE_SUCCESS;
  }
}
