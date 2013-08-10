package org.smoothbuild.builtin.file;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.PathIsNotADirException;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Path;

@FunctionName("saveTo")
public class SaveToFunction implements FunctionDefinition {
  private final FileSystem fileSystem;

  private final Param<File> file = Param.fileParam("file");
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
  public Void execute() throws FunctionException {
    Path dirPath = PathArgValidator.validatedPath(dir);
    saveTo(dirPath);
    return null;
  }

  private void saveTo(Path dirPath) throws PathIsNotADirException {
    assertPathCanBeUsedAsDir(dirPath);
    Path destination = dirPath.append(file.get().path());
    assertPathCanBeUsedAsDir(destination.parent());

    Path source = file.get().fullPath();

    fileSystem.copy(source, destination);
  }

  private void assertPathCanBeUsedAsDir(Path dirPath) throws PathIsNotADirException {
    if (fileSystem.pathExists(dirPath) && !fileSystem.isDirectory(dirPath)) {
      throw new PathIsNotADirException(dir, dirPath);
    }
  }
}
