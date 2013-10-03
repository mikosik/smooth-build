package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.DirParamIsAFileError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.SandboxImpl;
import org.smoothbuild.type.api.FileSet;
import org.smoothbuild.type.api.Path;
import org.smoothbuild.type.impl.StoredFileSet;

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
      return createFiles(validatedPath("dir", params.dir()));
    }

    private FileSet createFiles(Path path) {
      FileSystem fileSystem = sandbox.projectFileSystem();

      if (path.isRoot()) {
        throw new ErrorMessageException(new CannotListRootDirError());
      }

      if (path.firstElement().equals(BUILD_DIR)) {
        throw new ErrorMessageException(new ReadFromSmoothDirError(path));
      }

      switch (fileSystem.pathKind(path)) {
        case FILE:
          throw new ErrorMessageException(new DirParamIsAFileError("dir", path));
        case DIR:
          return new StoredFileSet(new SubFileSystem(fileSystem, path));
        case NOTHING:
          throw new ErrorMessageException(new NoSuchPathError("dir", path));
        default:
          throw new RuntimeException("unreachable case");
      }
    }
  }
}
