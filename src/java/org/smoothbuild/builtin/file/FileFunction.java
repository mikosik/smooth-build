package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.NoSuchPathException;
import org.smoothbuild.lang.function.exc.PathIsNotAFileException;
import org.smoothbuild.lang.internal.FileRoImpl;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.Path;

@FunctionName(name = "file")
public class FileFunction implements Function {
  private final Param<String> path = Param.stringParam("path");
  private final Params params = new Params(path);

  private final FileSystem fileSystem;

  private FileRo result;

  public FileFunction(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Params params() {
    return params;
  }

  @Override
  public FileRo result() {
    return result;
  }

  @Override
  public void execute() throws FunctionException {
    Path filePath = validatedPath(path);
    result = createFileRo(filePath);
  }

  private FileRo createFileRo(Path filePath) throws FunctionException {
    if (!fileSystem.pathExists(filePath.value())) {
      throw new NoSuchPathException(path, filePath);
    }

    if (fileSystem.isDirectory(filePath.value())) {
      throw new PathIsNotAFileException(path, filePath);
    } else {
      return new FileRoImpl(fileSystem, Path.path("."), filePath);
    }
  }
}
