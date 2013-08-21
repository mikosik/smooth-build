package org.smoothbuild.builtin.file;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.PathIsNotADirException;
import org.smoothbuild.lang.type.ExecuteMethod;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.FunctionName;
import org.smoothbuild.lang.type.Path;

@FunctionName("saveTo")
public class SaveToFunction {
  public interface Parameters {
    public File file();

    public String dir();
  }

  private final FileSystem fileSystem;

  public SaveToFunction(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @ExecuteMethod
  public Void execute(Parameters params) throws FunctionException {
    Path dirPath = PathArgValidator.validatedPath("dir", params.dir());
    saveTo(dirPath, params);
    return null;
  }

  private void saveTo(Path dirPath, Parameters params) throws PathIsNotADirException {
    assertPathCanBeUsedAsDir(dirPath);
    Path destination = dirPath.append(params.file().path());
    assertPathCanBeUsedAsDir(destination.parent());

    Path source = params.file().fullPath();

    fileSystem.copy(source, destination);
  }

  private void assertPathCanBeUsedAsDir(Path dirPath) throws PathIsNotADirException {
    if (fileSystem.pathExists(dirPath) && !fileSystem.isDirectory(dirPath)) {
      throw new PathIsNotADirException("dir", dirPath);
    }
  }
}
