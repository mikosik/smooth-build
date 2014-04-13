package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.SmoothContants.SMOOTH_DIR;
import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;

public class FileFunction {

  public interface Parameters {
    @Required
    public SString path();
  }

  @SmoothFunction(name = "file", cacheable = false)
  public static SFile execute(NativeApiImpl nativeApi, Parameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApiImpl nativeApi;
    private final Parameters params;
    private final FileReader reader;

    public Worker(NativeApiImpl nativeApi, Parameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
      this.reader = new FileReader(nativeApi);
    }

    public SFile execute() {
      return createFile(validatedPath("path", params.path()));
    }

    private SFile createFile(Path path) {
      if (!path.isRoot() && path.firstPart().equals(SMOOTH_DIR)) {
        throw new ReadFromSmoothDirError(path);
      }

      FileSystem fileSystem = nativeApi.projectFileSystem();
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
