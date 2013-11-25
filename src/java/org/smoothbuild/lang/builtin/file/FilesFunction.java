package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.IoConstants.SMOOTH_DIR;
import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.util.Streams.copy;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.lang.builtin.file.err.NoSuchDirButFileError;
import org.smoothbuild.lang.builtin.file.err.NoSuchDirError;
import org.smoothbuild.lang.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.SandboxImpl;

public class FilesFunction {
  public interface Parameters {
    @Required
    public SString dir();
  }

  @SmoothFunction(name = "files", cacheable = false)
  public static SArray<SFile> execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public SArray<SFile> execute() {
      return createFiles(validatedPath("dir", params.dir()));
    }

    private SArray<SFile> createFiles(Path dirPath) {
      FileSystem fileSystem = sandbox.projectFileSystem();

      if (dirPath.isRoot()) {
        throw new ErrorMessageException(new CannotListRootDirError());
      }

      if (dirPath.firstPart().equals(SMOOTH_DIR)) {
        throw new ErrorMessageException(new ReadFromSmoothDirError(dirPath));
      }

      switch (fileSystem.pathState(dirPath)) {
        case DIR:
          ArrayBuilder<SFile> fileArrayBuilder = sandbox.fileArrayBuilder();
          for (Path filePath : fileSystem.filesFrom(dirPath)) {
            FileBuilder fileBuilder = sandbox.fileBuilder();
            fileBuilder.setPath(filePath);
            Path fullPath = dirPath.append(filePath);
            copy(fileSystem.openInputStream(fullPath), fileBuilder.openOutputStream());
            fileArrayBuilder.add(fileBuilder.build());
          }
          return fileArrayBuilder.build();
        case FILE:
          throw new ErrorMessageException(new NoSuchDirButFileError(dirPath));
        case NOTHING:
          throw new ErrorMessageException(new NoSuchDirError(dirPath));
        default:
          throw new ErrorMessageException(new Message(FATAL,
              "Broken 'files' function implementation: unreachable case"));
      }
    }
  }
}
