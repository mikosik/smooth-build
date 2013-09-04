package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.PathIsNotAFileError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.StoredFile;
import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.SmoothFunction;

public class FileFunction {

  public interface Parameters {
    public String path();
  }

  @SmoothFunction("file")
  public static File execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public File execute() {
      Path filePath = validatedPath("path", params.path(), sandbox);
      if (filePath == null) {
        return null;
      }
      return createFile(filePath);
    }

    private File createFile(Path filePath) {
      FileSystem fileSystem = sandbox.projectFileSystem();
      if (!fileSystem.pathExists(filePath)) {
        sandbox.report(new NoSuchPathError("path", filePath));
        return null;
      }

      if (fileSystem.pathExistsAndIsDirectory(filePath)) {
        sandbox.report(new PathIsNotAFileError("path", filePath));
        return null;
      } else {
        return new StoredFile(fileSystem, Path.rootPath(), filePath);
      }
    }
  }
}
