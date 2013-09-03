package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.FileImpl;
import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.SmoothFunction;

public class SaveToFunction {
  public interface Parameters {
    public File file();

    public String dir();
  }

  @SmoothFunction("saveTo")
  public static void execute(SandboxImpl sandbox, Parameters params) {
    new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public void execute() {
      Path dirPath = validatedPath("dir", params.dir(), sandbox);
      if (dirPath == null) {
        return;
      }
      saveTo(dirPath, params);
    }

    private void saveTo(Path dirPath, Parameters params) {
      FileSystem fileSystem = sandbox.fileSystem();
      if (!canPathBeUsedAsDir(fileSystem, dirPath)) {
        return;
      }
      Path destination = dirPath.append(params.file().path());
      if (!canPathBeUsedAsDir(fileSystem, destination.parent())) {
        return;
      }

      Path source = ((FileImpl) params.file()).fullPath();
      fileSystem.copy(source, destination);
    }

    private boolean canPathBeUsedAsDir(FileSystem fileSystem, Path dirPath) {
      if (fileSystem.pathExists(dirPath) && !fileSystem.pathExistsAndisDirectory(dirPath)) {
        sandbox.report(new PathIsNotADirError("dir", dirPath));
        return false;
      }
      return true;
    }
  }
}
