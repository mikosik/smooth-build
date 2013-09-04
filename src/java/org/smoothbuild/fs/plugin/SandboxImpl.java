package org.smoothbuild.fs.plugin;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.ProblemsListener;

public class SandboxImpl implements Sandbox {
  private final FileSystem fileSystem;
  private final Path root;
  private final ProblemsListener problems;

  public SandboxImpl(FileSystem fileSystem, Path root, ProblemsListener problems) {
    this.fileSystem = fileSystem;
    this.root = root;
    this.problems = problems;
  }

  public File createFile(Path path) {
    return new FileImpl(fileSystem, root, path);
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public void report(Problem problem) {
    // TODO Smooth StackTrace (list of CodeLocations) should be added here. This
    // will be possible when each Task will have parent field pointing in
    // direction to nearest root node (build run can have more than one
    // task-to-run [soon]).
    problems.report(problem);
  }
}
