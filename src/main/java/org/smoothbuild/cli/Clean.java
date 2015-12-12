package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Clean implements Command {
  @Inject
  @ProjectDir
  private FileSystem fileSystem;
  @Inject
  private Console console;

  @Override
  public int run(String... args) {
    List<String> unknownArgs = ImmutableList.copyOf(args).subList(1, args.length);
    if (!unknownArgs.isEmpty()) {
      console.error("Unknown arguments: " + Iterables.toString(unknownArgs));
      return EXIT_CODE_ERROR;
    }
    try {
      fileSystem.delete(SMOOTH_DIR);
    } catch (FileSystemException e) {
      console.error(e.getMessage());
      return EXIT_CODE_ERROR;
    }
    return EXIT_CODE_SUCCESS;
  }
}
