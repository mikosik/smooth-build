package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.StoredFileSet;
import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.SmoothFunction;

// TODO forbid dir that points to temporary files created by smooth-build
// tool

public class FilesFunction {
  public interface Parameters {
    public String dir();
  }

  @SmoothFunction("files")
  public static FileSet execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public FileSet execute() {
      Path dirPath = validatedPath("dir", params.dir(), sandbox);
      if (dirPath == null) {
        return null;
      }
      return createFiles(dirPath);
    }

    private FileSet createFiles(Path dirPath) {
      FileSystem fileSystem = sandbox.fileSystem();
      if (!fileSystem.pathExists(dirPath)) {
        sandbox.report(new NoSuchPathError("dir", dirPath));
        return null;
      }

      if (fileSystem.pathExistsAndIsDirectory(dirPath)) {
        return new StoredFileSet(fileSystem, dirPath);
      } else {
        sandbox.report(new PathIsNotADirError("dir", dirPath));
        return null;
      }
    }
  }
}
