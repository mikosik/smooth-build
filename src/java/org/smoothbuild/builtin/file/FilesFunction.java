package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.io.fs.FileSystemModule.SMOOTH_DIR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.util.Streams.copy;

import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.DirParamIsAFileError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.plugin.FileBuilder;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.exec.SandboxImpl;

public class FilesFunction {
  public interface Parameters {
    @Required
    public StringValue dir();
  }

  @SmoothFunction(name = "files", cacheable = false)
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

      if (dirPath.firstPart().equals(SMOOTH_DIR)) {
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
          throw new ErrorMessageException(new Message(FATAL,
              "Broken 'files' function implementation: unreachable case"));
      }
    }
  }
}
