package org.smoothbuild.fs.plugin;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.MutableFile;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.ProblemsListener;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final SubFileSystem sandboxFileSystem;
  private final ProblemsListener problems;

  public SandboxImpl(FileSystem fileSystem, Path root, ProblemsListener problems) {
    this.projectFileSystem = fileSystem;
    this.sandboxFileSystem = new SubFileSystem(fileSystem, root);
    this.problems = problems;
  }

  public MutableFile createFile(Path path) {
    return new StoredFile(sandboxFileSystem, path);
  }

  public FileSystem projectFileSystem() {
    return projectFileSystem;
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
