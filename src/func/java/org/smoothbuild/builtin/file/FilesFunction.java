package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.builtin.file.err.ReadFromSmoothDirError;
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
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApiImpl nativeApi;
    private final BuiltinSmoothModule.FilesParameters params;
    private final FileReader reader;

    public Worker(NativeApiImpl nativeApi, BuiltinSmoothModule.FilesParameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
      this.reader = new FileReader(nativeApi);
    }

    public SArray<SFile> execute() {
      return createFiles(validatedPath("dir", params.dir()));
    }

    private SArray<SFile> createFiles(Path dirPath) {
      FileSystem fileSystem = nativeApi.projectFileSystem();

      if (dirPath.isRoot()) {
        throw new CannotListRootDirError();
      }

      if (dirPath.firstPart().equals(SMOOTH_DIR)) {
        throw new ReadFromSmoothDirError(dirPath);
      }

      switch (fileSystem.pathState(dirPath)) {
        case DIR:
          ArrayBuilder<SFile> fileArrayBuilder = nativeApi.arrayBuilder(FILE_ARRAY);
          for (Path filePath : fileSystem.filesFrom(dirPath)) {
            fileArrayBuilder.add(reader.createFile(filePath, dirPath.append(filePath)));
          }
          return fileArrayBuilder.build();
        case FILE:
          throw new NoSuchDirButFileError(dirPath);
        case NOTHING:
          throw new NoSuchDirError(dirPath);
        default:
          throw new Message(FATAL, "Broken 'files' function implementation: unreachable case");
      }
    }
  }
}
