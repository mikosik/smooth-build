package org.smoothbuild.task.save;

import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.function.base.Name;

public class BlobSaver implements Saver<SBlob> {
  private final FileSystem smoothFileSystem;

  public BlobSaver(FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  @Override
  public void save(Name name, SBlob blob) {
    Path artifactPath = artifactPath(name);
    Path targetPath = targetPath(blob);

    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }
}
