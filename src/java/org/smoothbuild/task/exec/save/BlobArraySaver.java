package org.smoothbuild.task.exec.save;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.save.Savers.artifactPath;
import static org.smoothbuild.task.exec.save.Savers.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;

public class BlobArraySaver implements Saver<SArray<SBlob>> {
  private final FileSystem smoothFileSystem;

  public BlobArraySaver(FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  @Override
  public void save(Name name, SArray<SBlob> blobArray) {
    Path artifactPath = artifactPath(name);

    smoothFileSystem.delete(artifactPath);
    int i = 0;
    for (SBlob blob : blobArray) {
      Path filePath = path(Integer.valueOf(i).toString());
      Path sourcePath = artifactPath.append(filePath);
      Path targetPath = targetPath(blob);
      smoothFileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }
}
