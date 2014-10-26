package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;

public class FilesFunction {

  public interface FilesParameters {
    @Required
    public SString dir();
  }

  @SmoothFunction
  @NotCacheable
  public static Array<SFile> files(NativeApiImpl nativeApi, FilesParameters params) {
    Path path = validatedPath("dir", params.dir());
    FileSystem fileSystem = nativeApi.projectFileSystem();

    if (path.isRoot()) {
      throw new CannotListRootDirError();
    }

    if (path.firstPart().equals(SMOOTH_DIR)) {
      throw new IllegalReadFromSmoothDirError(path);
    }

    switch (fileSystem.pathState(path)) {
      case DIR:
        return readFiles(nativeApi, fileSystem, path);
      case FILE:
        throw new NoSuchDirButFileError(path);
      case NOTHING:
        throw new NoSuchDirError(path);
      default:
        throw new Message(FATAL, "Broken 'files' function implementation: unreachable case");
    }
  }

  private static Array<SFile> readFiles(NativeApiImpl nativeApi, FileSystem fileSystem,
      Path path) {
    ArrayBuilder<SFile> fileArrayBuilder = nativeApi.arrayBuilder(FILE_ARRAY);
    FileReader reader = new FileReader(nativeApi);
    for (Path filePath : fileSystem.filesFromRecursive(path)) {
      fileArrayBuilder.add(reader.createFile(filePath, path.append(filePath)));
    }
    return fileArrayBuilder.build();
  }
}
