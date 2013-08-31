package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.exc.NoSuchPathException;
import org.smoothbuild.builtin.file.exc.PathIsNotADirException;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.FileListImpl;
import org.smoothbuild.plugin.ExecuteMethod;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

// TODO forbid dir that points to temporary files created by smooth-build
// tool

public class FilesFunction {
  public interface Parameters {
    public String dir();
  }

  private final FileSystem fileSystem;

  public FilesFunction(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @ExecuteMethod("files")
  public FileList execute(Parameters params) throws FunctionException {
    Path dirPath = validatedPath("dir", params.dir());
    return createFiles(dirPath);
  }

  private FileList createFiles(Path dirPath) throws FunctionException {
    if (!fileSystem.pathExists(dirPath)) {
      throw new NoSuchPathException("dir", dirPath);
    }

    if (fileSystem.pathExistsAndisDirectory(dirPath)) {
      return new FileListImpl(fileSystem, dirPath);
    } else {
      throw new PathIsNotADirException("dir", dirPath);
    }
  }
}
