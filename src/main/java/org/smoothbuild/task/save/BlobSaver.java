package org.smoothbuild.task.save;

import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Blob;

public class BlobSaver implements Saver<Blob> {
  private final FileSystem smoothFileSystem;

  public BlobSaver(FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  @Override
  public void save(Name name, Blob blob) {
    Path artifactPath = artifactPath(name);
    Path targetPath = targetPath(blob);

    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }
}
