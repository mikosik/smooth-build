package org.smoothbuild.fs.plugin;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.ProblemsListener;

public class SandboxImpl implements Sandbox {
  private final FileSystem fileSystem;
  private final Path root;
  private final ProblemsListener problems;

  private FileSet resultFileSet;
  private File resultFile;

  public SandboxImpl(FileSystem fileSystem, Path root, ProblemsListener problems) {
    this.fileSystem = fileSystem;
    this.root = root;
    this.problems = problems;
  }

  @Override
  public FileSet resultFileSet() {
    checkState(resultFile == null, "Cannot call resultFileSet() when resultFile() has been called.");

    if (resultFileSet == null) {
      resultFileSet = new StoredFileSet(fileSystem, root);
    }

    return resultFileSet;
  }

  @Override
  public File resultFile(Path path) {
    checkState(resultFile == null, "Cannot call resultFile() twice.");
    checkState(resultFileSet == null,
        "Cannot call resultFile() when resultFileSet() has been called.");

    resultFile = new FileImpl(fileSystem, root, path);

    return resultFile;
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
