package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import org.smoothbuild.builtin.file.err.DirParamIsAFileError;
import org.smoothbuild.builtin.file.err.DirParamSubdirIsAFileError;
import org.smoothbuild.builtin.file.err.EitherFileOrFilesMustBeProvidedError;
import org.smoothbuild.builtin.file.err.FileAndFilesSpecifiedError;
import org.smoothbuild.builtin.file.err.FileOutputIsADirError;
import org.smoothbuild.builtin.file.err.FileOutputSubdirIsAFileError;
import org.smoothbuild.builtin.file.err.WriteToSmoothDirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PluginException;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.plugin.internal.StoredFile;

public class SaveFunction {
  public interface Parameters {
    public File file();

    public FileSet files();

    @Required
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
      Path dirPath = validatedPath("dir", params.dir());
      checkDirParam(dirPath);

      File file = params.file();
      FileSet files = params.files();
      if (file == null && files == null) {
        throw new PluginException(new EitherFileOrFilesMustBeProvidedError());
      }
      if (file != null && files != null) {
        throw new PluginException(new FileAndFilesSpecifiedError());
      }

      if (file != null) {
        save(dirPath, file);
      } else {
        save(dirPath, files);
      }
    }

    private void save(Path dirPath, File file) {
      checkFilePath(dirPath, file.path());
      saveImpl(dirPath, file);
    }

    private void save(Path dirPath, FileSet files) {
      for (File file1 : files) {
        checkFilePath(dirPath, file1.path());
      }
      for (File file1 : files) {
        saveImpl(dirPath, file1);
      }
    }

    private void saveImpl(Path dirPath, File file) {
      Path fileSystemRoot = ((StoredFile) file).fileSystem().root();
      Path source = fileSystemRoot.append(file.path());
      Path destination = dirPath.append(file.path());
      sandbox.projectFileSystem().copy(source, destination);
    }

    private void checkDirParam(Path dirPath) {
      if (dirPath.isRoot()) {
        return;
      }
      if (dirPath.firstElement().equals(BUILD_DIR)) {
        throw new PluginException(new WriteToSmoothDirError(dirPath));
      }

      Path path = dirPath;
      FileSystem fileSystem = sandbox.projectFileSystem();
      while (!path.isRoot()) {
        switch (fileSystem.pathKind(path)) {
          case FILE:
            if (path.equals(dirPath)) {
              throw new PluginException(new DirParamIsAFileError("dir", path));
            } else {
              throw new PluginException(new DirParamSubdirIsAFileError("dir", dirPath, path));
            }
          case DIR:
            return;
          case NOTHING:
            path = path.parent();
            break;
          default:
            throw new RuntimeException("unreachable case");
        }
      }
    }

    private void checkFilePath(Path dirPath, Path filePath) {
      if (dirPath.isRoot() && filePath.firstElement().equals(BUILD_DIR)) {
        throw new PluginException(new WriteToSmoothDirError(filePath));
      }

      Path fullPath = dirPath.append(filePath);
      FileSystem fileSystem = sandbox.projectFileSystem();
      switch (fileSystem.pathKind(fullPath)) {
        case FILE:
          return;
        case DIR:
          throw new PluginException(new FileOutputIsADirError(dirPath, filePath));
        case NOTHING:
          break;
        default:
          throw new RuntimeException("unreachable case");
      }

      Path path = fullPath.parent();
      while (!path.equals(dirPath)) {
        switch (fileSystem.pathKind(path)) {
          case FILE:
            throw new PluginException(
                new FileOutputSubdirIsAFileError(dirPath, filePath, path));
          case DIR:
            return;
          case NOTHING:
            path = path.parent();
            break;
          default:
            throw new RuntimeException("unreachable case");
        }
      }
    }
  }
}
