package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;

public class FilesFunction {
  public static SArray<SFile> execute(NativeApiImpl nativeApi,
      BuiltinSmoothModule.FilesParameters params) {
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

  private static SArray<SFile> readFiles(NativeApiImpl nativeApi, FileSystem fileSystem, Path path) {
    ArrayBuilder<SFile> fileArrayBuilder = nativeApi.arrayBuilder(FILE_ARRAY);
    FileReader reader = new FileReader(nativeApi);
    for (Path filePath : fileSystem.filesFrom(path)) {
      fileArrayBuilder.add(reader.createFile(filePath, path.append(filePath)));
    }
    return fileArrayBuilder.build();
  }
}
