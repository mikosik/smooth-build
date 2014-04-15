package org.smoothbuild.task.exec.save;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.save.Savers.artifactPath;
import static org.smoothbuild.task.exec.save.Savers.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Name;

public class ArraySaver<T extends SValue> implements Saver<SArray<T>> {
  private final FileSystem smoothFileSystem;

  public ArraySaver(FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  @Override
  public void save(Name name, SArray<T> array) {
    Path artifactPath = artifactPath(name);

    smoothFileSystem.delete(artifactPath);

    // Create directory explicitly. When fileArray is empty for loop below won't
    // create empty dir for us.
    smoothFileSystem.createDir(artifactPath);

    int i = 0;
    for (T value : array) {
      Path filePath = path(Integer.valueOf(i).toString());
      Path sourcePath = artifactPath.append(filePath);
      Path targetPath = targetPath(value);
      smoothFileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }
}
