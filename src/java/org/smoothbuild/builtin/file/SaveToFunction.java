package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.file.exc.PathIsNotADirException;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.exc.FunctionException;

public class SaveToFunction {
  public interface Parameters {
    public File file();

    public String dir();
  }

  private final SandboxImpl sandbox;

  public SaveToFunction(SandboxImpl sandbox) {
    this.sandbox = sandbox;
  }

  @SmoothFunction("saveTo")
  public void execute(Parameters params) throws FunctionException {
    Path dirPath = PathArgValidator.validatedPath("dir", params.dir());
    saveTo(dirPath, params);
  }

  private void saveTo(Path dirPath, Parameters params) throws PathIsNotADirException {
    FileSystem fileSystem = sandbox.fileSystem();
    assertPathCanBeUsedAsDir(fileSystem, dirPath);
    Path destination = dirPath.append(params.file().path());
    assertPathCanBeUsedAsDir(fileSystem, destination.parent());

    Path source = params.file().fullPath();

    fileSystem.copy(source, destination);
  }

  private void assertPathCanBeUsedAsDir(FileSystem fileSystem, Path dirPath)
      throws PathIsNotADirException {
    if (fileSystem.pathExists(dirPath) && !fileSystem.pathExistsAndisDirectory(dirPath)) {
      throw new PathIsNotADirException("dir", dirPath);
    }
  }
}
