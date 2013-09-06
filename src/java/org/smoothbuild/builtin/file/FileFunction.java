package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.PathIsNotAFileError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.plugin.internal.StoredFile;

public class FileFunction {

  public interface Parameters {
    // TODO should be marked as @Required
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
        return new StoredFile(fileSystem, filePath);
      }
    }
  }
}
