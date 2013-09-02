package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.file.exc.PathIsNotADirException;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.exc.FunctionException;

public class SaveToFunction {
  public interface Parameters {
    public File file();

    public String dir();
  }

  @SmoothFunction("saveTo")
  public static void execute(SandboxImpl sandbox, Parameters params) throws FunctionException {
    new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public void execute() throws FunctionException {
      Path dirPath = PathArgValidator.validatedPath("dir", params.dir());
      saveTo(dirPath, params);
    }

    private void saveTo(Path dirPath, Parameters params) throws PathIsNotADirException {
      FileSystem fileSystem = sandbox.fileSystem();
      assertPathCanBeUsedAsDir(fileSystem, dirPath);
      Path destination = dirPath.append(params.file().path());
      assertPathCanBeUsedAsDir(fileSystem, destination.parent());

      Path source = params.file().fullPath();

      fileSystem.copy(source, destination);
    }

    private void assertPathCanBeUsedAsDir(FileSystem fileSystem, Path dirPath)
        throws PathIsNotADirException {
      if (fileSystem.pathExists(dirPath) && !fileSystem.pathExistsAndisDirectory(dirPath)) {
        throw new PathIsNotADirException("dir", dirPath);
      }
    }
  }
}
