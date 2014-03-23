package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.Constants.SMOOTH_DIR;
import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.NoSuchFileButDirError;
import org.smoothbuild.io.fs.base.exc.NoSuchFileError;
import org.smoothbuild.lang.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.PluginApiImpl;

public class FileFunction {

  public interface Parameters {
    @Required
    public SString path();
  }

  @SmoothFunction(name = "file", cacheable = false)
  public static SFile execute(PluginApiImpl pluginApi, Parameters params) {
    return new Worker(pluginApi, params).execute();
  }

  private static class Worker {
    private final PluginApiImpl pluginApi;
    private final Parameters params;
    private final FileReader reader;

    public Worker(PluginApiImpl pluginApi, Parameters params) {
      this.pluginApi = pluginApi;
      this.params = params;
      this.reader = new FileReader(pluginApi);
    }

    public SFile execute() {
      return createFile(validatedPath("path", params.path()));
    }

    private SFile createFile(Path path) {
      if (!path.isRoot() && path.firstPart().equals(SMOOTH_DIR)) {
        throw new ReadFromSmoothDirError(path);
      }

      FileSystem fileSystem = pluginApi.projectFileSystem();
      switch (fileSystem.pathState(path)) {
        case FILE:
          return reader.createFile(path, path);
        case DIR:
          throw new NoSuchFileButDirError(path);
        case NOTHING:
          throw new NoSuchFileError(path);
        default:
          throw new Message(FATAL, "Broken 'file' function implementation: unreachable case");
      }
    }
  }
}
