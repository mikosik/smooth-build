package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.fs.FileSystemModule.SMOOTH_DIR;
import static org.smoothbuild.message.message.MessageType.FATAL;
import static org.smoothbuild.util.Streams.copy;

import org.smoothbuild.builtin.file.err.FileParamIsADirError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileBuilder;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.exec.SandboxImpl;

public class FileFunction {

  public interface Parameters {
    @Required
    public StringValue path();
  }

  @SmoothFunction(name = "file", cacheable = false)
  public static File execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public File execute() {
      return createFile(validatedPath("path", params.path()));
    }

    private File createFile(Path path) {
      if (!path.isRoot() && path.firstPart().equals(SMOOTH_DIR)) {
        throw new ErrorMessageException(new ReadFromSmoothDirError(path));
      }

      FileSystem fileSystem = sandbox.projectFileSystem();
      switch (fileSystem.pathState(path)) {
        case FILE:
          FileBuilder fileBuilder = sandbox.fileBuilder();
          fileBuilder.setPath(path);
          copy(fileSystem.openInputStream(path), fileBuilder.openOutputStream());
          return fileBuilder.build();
        case DIR:
          throw new ErrorMessageException(new FileParamIsADirError("path", path));
        case NOTHING:
          throw new ErrorMessageException(new NoSuchPathError("path", path));
        default:
          throw new ErrorMessageException(new Message(FATAL,
              "Broken 'file' function implementation: unreachable case"));
      }
    }
  }
}
