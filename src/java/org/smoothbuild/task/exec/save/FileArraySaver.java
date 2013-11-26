package org.smoothbuild.task.exec.save;

import static org.smoothbuild.task.exec.save.Savers.artifactPath;
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
    Path artifactPath = artifactPath(name);
    smoothFileSystem.delete(artifactPath);

    for (SFile file : fileArray) {
      Path sourcePath = artifactPath.append(file.path());
      Path targetPath = targetPath(file.content());
      smoothFileSystem.createLink(sourcePath, targetPath);
    }
  }
}
