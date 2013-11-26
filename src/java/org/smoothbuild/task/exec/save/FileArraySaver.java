package org.smoothbuild.task.exec.save;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.save.Savers.sourcePath;
import static org.smoothbuild.task.exec.save.Savers.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;

public class FileArraySaver implements Saver<SArray<SFile>> {
  private final FileSystem smoothFileSystem;

  public FileArraySaver(FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  @Override
  public void save(Name name, SArray<SFile> fileArray) {
    Path sourcePath = sourcePath(path(name.value()));
    smoothFileSystem.delete(sourcePath);

    for (SFile file : fileArray) {
      Path linkPath = sourcePath.append(file.path());
      Path targetPath = targetPath(file.content());
      smoothFileSystem.createLink(linkPath, targetPath);
    }
  }
}
