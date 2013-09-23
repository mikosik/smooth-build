package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import org.smoothbuild.builtin.file.err.FileParamIsADirError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PluginErrorException;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.plugin.internal.StoredFile;

public class FileFunction {

  public interface Parameters {
    @Required
    public String path();
  }

  @SmoothFunction("file")
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
      FileSystem fileSystem = sandbox.projectFileSystem();

      if (!path.isRoot() && path.firstElement().equals(BUILD_DIR)) {
        throw new PluginErrorException(new ReadFromSmoothDirError(path));
      }

      switch (fileSystem.pathKind(path)) {
        case FILE:
          return new StoredFile(fileSystem, path);
        case DIR:
          throw new PluginErrorException(new FileParamIsADirError("path", path));
        case NOTHING:
          throw new PluginErrorException(new NoSuchPathError("path", path));
        default:
          throw new RuntimeException("unreachable case");
      }
    }
  }
}
