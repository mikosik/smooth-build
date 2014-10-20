package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;

public class FileFunction {
  public interface FileParameters {
    @Required
    public SString path();
  }

  @SmoothFunction(name = "file", cacheable = false)
  public static SFile file(NativeApiImpl nativeApi, FileParameters params) {
    Path path = validatedPath("path", params.path());
    if (!path.isRoot() && path.firstPart().equals(SMOOTH_DIR)) {
      throw new IllegalReadFromSmoothDirError(path);
    }

    FileSystem fileSystem = nativeApi.projectFileSystem();
    switch (fileSystem.pathState(path)) {
      case FILE:
        FileReader reader = new FileReader(nativeApi);
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
