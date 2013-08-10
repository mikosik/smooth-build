package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.NoSuchPathException;
import org.smoothbuild.lang.function.exc.PathIsNotAFileException;
import org.smoothbuild.lang.internal.FileImpl;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Path;

@FunctionName("file")
public class FileFunction implements FunctionDefinition {
  private final Param<String> path = Param.stringParam("path");
  private final Params params = new Params(path);

  private final FileSystem fileSystem;

  public FileFunction(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Params params() {
    return params;
  }

  @Override
  public File execute() throws FunctionException {
    Path filePath = validatedPath(path);
    return createFile(filePath);
  }

  private File createFile(Path filePath) throws FunctionException {
    if (!fileSystem.pathExists(filePath)) {
      throw new NoSuchPathException(path, filePath);
    }

    if (fileSystem.isDirectory(filePath)) {
      throw new PathIsNotAFileException(path, filePath);
    } else {
      return new FileImpl(fileSystem, Path.rootPath(), filePath);
    }
  }
}
