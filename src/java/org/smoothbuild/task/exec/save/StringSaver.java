package org.smoothbuild.task.exec.save;

import static org.smoothbuild.task.exec.save.Savers.artifactPath;
import static org.smoothbuild.task.exec.save.Savers.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SString;

public class StringSaver implements Saver<SString> {
  private final FileSystem smoothFileSystem;

  public StringSaver(FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  @Override
  public void save(Name name, SString string) {
    Path artifactPath = artifactPath(name);
    Path targetPath = targetPath(string);

    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }
}
