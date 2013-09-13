package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.plugin.internal.StoredFileSet;

// TODO forbid dir that points to temporary files created by smooth-build
// tool

public class FilesFunction {
  public interface Parameters {
    @Required
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
      FileSystem fileSystem = sandbox.projectFileSystem();
      if (!fileSystem.pathExists(dirPath)) {
        sandbox.report(new NoSuchPathError("dir", dirPath));
        return null;
      }

      if (fileSystem.pathExistsAndIsDirectory(dirPath)) {
        return new StoredFileSet(new SubFileSystem(fileSystem, dirPath));
      } else {
        sandbox.report(new PathIsNotADirError("dir", dirPath));
        return null;
      }
    }
  }
}
