package org.smoothbuild.task.exec.save;

import static org.smoothbuild.task.exec.save.Savers.artifactPath;
import static org.smoothbuild.task.exec.save.Savers.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SFile;

public class FileSaver implements Saver<SFile> {
  private final FileSystem smoothFileSystem;

  public FileSaver(FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  @Override
  public void save(Name name, SFile value) {
    Path artifactPath = artifactPath(name);
    Path targetPath = targetPath(value.content());

    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }
}
