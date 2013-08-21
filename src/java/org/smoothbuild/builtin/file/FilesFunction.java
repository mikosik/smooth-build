package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.FilesImpl;
import org.smoothbuild.plugin.ExecuteMethod;
import org.smoothbuild.plugin.Files;
import org.smoothbuild.plugin.FunctionName;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.plugin.exc.NoSuchPathException;
import org.smoothbuild.plugin.exc.PathIsNotADirException;

// TODO forbid dir that points to temporary files created by smooth-build
// tool

@FunctionName("files")
public class FilesFunction {
  public interface Parameters {
    public String dir();
  }

  private final FileSystem fileSystem;

  public FilesFunction(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @ExecuteMethod
  public Files execute(Parameters params) throws FunctionException {
    Path dirPath = validatedPath("dir", params.dir());
    return createFiles(dirPath);
  }

  private Files createFiles(Path dirPath) throws FunctionException {
    if (!fileSystem.pathExists(dirPath)) {
      throw new NoSuchPathException("dir", dirPath);
    }

    if (fileSystem.isDirectory(dirPath)) {
      return new FilesImpl(fileSystem, dirPath);
    } else {
      throw new PathIsNotADirException("dir", dirPath);
    }
  }
}
