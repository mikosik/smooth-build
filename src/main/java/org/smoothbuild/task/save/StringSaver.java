package org.smoothbuild.task.save;

import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.function.base.Name;

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
