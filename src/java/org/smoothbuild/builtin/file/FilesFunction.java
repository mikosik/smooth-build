package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.fs.base.Streams.copy;

import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.DirParamIsAFileError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.FileBuilder;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.type.api.FileSet;

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

    private FileSet createFiles(Path dirPath) {
      FileSystem fileSystem = sandbox.projectFileSystem();

      if (dirPath.isRoot()) {
        throw new ErrorMessageException(new CannotListRootDirError());
      }

      if (dirPath.firstPart().equals(BUILD_DIR)) {
        throw new ErrorMessageException(new ReadFromSmoothDirError(dirPath));
      }

      switch (fileSystem.pathState(dirPath)) {
        case FILE:
          throw new ErrorMessageException(new DirParamIsAFileError("dir", dirPath));
        case DIR:
          FileSetBuilder fileSetBuilder = sandbox.fileSetBuilder();
          for (Path filePath : fileSystem.filesFrom(dirPath)) {
            FileBuilder fileBuilder = sandbox.fileBuilder();
            fileBuilder.setPath(filePath);
            Path fullPath = dirPath.append(filePath);
            copy(fileSystem.openInputStream(fullPath), fileBuilder.openOutputStream());
            fileSetBuilder.add(fileBuilder.build());
          }
          return fileSetBuilder.build();
        case NOTHING:
          throw new ErrorMessageException(new NoSuchPathError("dir", dirPath));
        default:
          throw new RuntimeException("unreachable case");
      }
    }
  }
}
