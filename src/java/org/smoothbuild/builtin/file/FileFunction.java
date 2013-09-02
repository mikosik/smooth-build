package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.exc.NoSuchPathException;
import org.smoothbuild.builtin.file.exc.PathIsNotAFileException;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.FileImpl;
import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.exc.FunctionException;

public class FileFunction {

  public interface Parameters {
    public String path();
  }

  @SmoothFunction("file")
  public static File execute(SandboxImpl sandbox, Parameters params) throws FunctionException {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public File execute() throws FunctionException {
      Path filePath = validatedPath("path", params.path());
      return createFile(filePath);
    }

    private File createFile(Path filePath) throws FunctionException {
      FileSystem fileSystem = sandbox.fileSystem();
      if (!fileSystem.pathExists(filePath)) {
        throw new NoSuchPathException("path", filePath);
      }

      if (fileSystem.pathExistsAndisDirectory(filePath)) {
        throw new PathIsNotAFileException("path", filePath);
      } else {
        return new FileImpl(fileSystem, Path.rootPath(), filePath);
      }
    }
  }
}
