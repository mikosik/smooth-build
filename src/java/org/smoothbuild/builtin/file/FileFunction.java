package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.file.exc.NoSuchPathException;
import org.smoothbuild.builtin.file.exc.PathIsNotAFileException;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.FileImpl;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

public class FileFunction {

  public interface Parameters {
    public String path();
  }

  private final FileSystem fileSystem;

  public FileFunction(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @SmoothFunction("file")
  public File execute(Parameters params) throws FunctionException {
    Path filePath = validatedPath("path", params.path());
    return createFile(filePath);
  }

  private File createFile(Path filePath) throws FunctionException {
    if (!fileSystem.pathExists(filePath)) {
      throw new NoSuchPathException("path", filePath);
    }

    if (fileSystem.pathExistsAndisDirectory(filePath)) {
      throw new PathIsNotAFileException("path", filePath);
    } else {
      return new FileImpl(fileSystem, Path.rootPath(), filePath);
    }
  }
}
