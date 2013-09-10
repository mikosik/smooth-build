package org.smoothbuild.plugin.internal;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.ProblemsListener;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final MutableStoredFileSet resultFileSet;
  private final ProblemsListener problems;

  public SandboxImpl(FileSystem fileSystem, Path root, ProblemsListener problems) {
    this(fileSystem, new SubFileSystem(fileSystem, root), problems);
  }

  public SandboxImpl(FileSystem fileSystem, FileSystem sandboxFileSystem, ProblemsListener problems) {
    this.projectFileSystem = fileSystem;
    this.resultFileSet = new MutableStoredFileSet(sandboxFileSystem);
    this.problems = problems;
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
  public void report(Problem problem) {
    // TODO Smooth StackTrace (list of CodeLocations) should be added here. This
    // will be possible when each Task will have parent field pointing in
    // direction to nearest root node (build run can have more than one
    // task-to-run [soon]).
    problems.report(problem);
  }
}
