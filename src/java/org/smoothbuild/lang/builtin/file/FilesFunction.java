package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.Constants.SMOOTH_DIR;
import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.exc.NoSuchDirError;
import org.smoothbuild.lang.builtin.file.err.CannotListRootDirError;
import org.smoothbuild.lang.builtin.file.err.ReadFromSmoothDirError;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;

public class FilesFunction {
  public interface Parameters {
    @Required
    public SString dir();
  }

  @SmoothFunction(name = "files", cacheable = false)
  public static SArray<SFile> execute(NativeApiImpl nativeApi, Parameters params) {
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
