package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.err.EitherFileOrFilesMustBeProvidedError;
import org.smoothbuild.builtin.file.err.FileAndFilesSpecifiedError;
import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.plugin.internal.StoredFile;

public class SaveFunction {
  public interface Parameters {
    public File file();

    public FileSet files();

    // TODO should be marked as @Required
    public String dir();
  }

  @SmoothFunction("save")
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
      File file = params.file();
      FileSet files = params.files();
      if (file == null && files == null) {
        sandbox.report(new EitherFileOrFilesMustBeProvidedError());
        return;
      }
      if (file != null && files != null) {
        sandbox.report(new FileAndFilesSpecifiedError());
        return;
      }
      if (!canPathBeUsedAsDir(dirPath)) {
        return;
      }
      if (file != null) {
        save(dirPath, file);
      } else {
        save(dirPath, files);
      }
    }

    private void save(Path dirPath, FileSet files) {
      for (File file : files) {
        save(dirPath, file);
      }
    }

    private void save(Path dirPath, File file) {
      Path destination = dirPath.append(file.path());

      if (!canPathBeUsedAsDir(destination.parent())) {
        return;
      }

      Path fileSystemRoot = ((StoredFile) file).fileSystem().root();
      Path source = fileSystemRoot.append(file.path());
      sandbox.projectFileSystem().copy(source, destination);
    }

    private boolean canPathBeUsedAsDir(Path dirPath) {
      FileSystem fileSystem = sandbox.projectFileSystem();
      if (fileSystem.pathExists(dirPath) && !fileSystem.pathExistsAndIsDirectory(dirPath)) {
        sandbox.report(new PathIsNotADirError("dir", dirPath));
        return false;
      }
      return true;
    }
  }
}
