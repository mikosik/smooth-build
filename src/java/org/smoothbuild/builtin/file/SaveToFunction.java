package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.plugin.internal.StoredFile;

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
      FileSystem fileSystem = sandbox.projectFileSystem();
      if (!canPathBeUsedAsDir(fileSystem, dirPath)) {
        return;
      }
      Path destination = dirPath.append(params.file().path());
      if (!canPathBeUsedAsDir(fileSystem, destination.parent())) {
        return;
      }

      Path source = ((StoredFile) params.file()).fullPath();
      fileSystem.copy(source, destination);
    }

    private boolean canPathBeUsedAsDir(FileSystem fileSystem, Path dirPath) {
      if (fileSystem.pathExists(dirPath) && !fileSystem.pathExistsAndIsDirectory(dirPath)) {
        sandbox.report(new PathIsNotADirError("dir", dirPath));
        return false;
      }
      return true;
    }
  }
}
