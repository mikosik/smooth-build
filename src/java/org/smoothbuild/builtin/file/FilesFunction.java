package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.NoSuchPathException;
import org.smoothbuild.lang.function.exc.PathIsNotADirException;
import org.smoothbuild.lang.internal.FilesRoImpl;
import org.smoothbuild.lang.type.FilesRo;
import org.smoothbuild.lang.type.Path;

// TODO forbid dir that points to temporary files created by smooth-build
// tool

@FunctionName("files")
public class FilesFunction implements FunctionDefinition {
  private final Param<String> dir = Param.stringParam("dir");
  private final Params params = new Params(dir);

  private final FileSystem fileSystem;

  public FilesFunction(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Params params() {
    return params;
  }

  @Override
  public FilesRo execute() throws FunctionException {
    Path dirPath = validatedPath(dir);
    return createFilesRo(dirPath);
  }

  private FilesRo createFilesRo(Path dirPath) throws FunctionException {
    if (!fileSystem.pathExists(dirPath)) {
      throw new NoSuchPathException(dir, dirPath);
    }

    if (fileSystem.isDirectory(dirPath)) {
      return new FilesRoImpl(fileSystem, dirPath);
    } else {
      throw new PathIsNotADirException(dir, dirPath);
    }
  }
}
