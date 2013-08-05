package org.smoothbuild.builtin.file;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.PathIsNotADirException;
import org.smoothbuild.lang.internal.FileRoImpl;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.Path;

@FunctionName(name = "saveTo")
public class SaveToFunction implements Function {
  private final FileSystem fileSystem;

  private final Param<FileRo> file = Param.fileParam("file");
  private final Param<String> dir = Param.stringParam("dir");
  private final Params params = new Params(file, dir);

  public SaveToFunction(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Params params() {
    return params;
  }

  @Override
  public Void result() {
    return null;
  }

  @Override
  public void execute() throws FunctionException {
    Path dirPath = PathArgValidator.validatedPath(dir);
    saveTo(dirPath);
  }

  private void saveTo(Path dirPath) throws PathIsNotADirException {
    assertPathCanBeUsedAsDir(dirPath);
    Path destination = dirPath.append(file.get().path());
    assertPathCanBeUsedAsDir(destination.parent());

    /*
     * This cast wouldn't be needed if FileRo had fullPath() method. We do not
     * want to expose it as it would mislead developers who use FileRo interface
     * for implementing plugin functions.
     */
    Path source = ((FileRoImpl) file.get()).fullPath();

    fileSystem.copy(source.value(), destination.value());
  }

  private void assertPathCanBeUsedAsDir(Path dirPath) throws PathIsNotADirException {
    if (fileSystem.pathExists(dirPath.value()) && !fileSystem.isDirectory(dirPath.value())) {
      throw new PathIsNotADirException(dir, dirPath);
    }
  }
}
