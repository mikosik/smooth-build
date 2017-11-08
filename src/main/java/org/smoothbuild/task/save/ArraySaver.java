package org.smoothbuild.task.save;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

public class ArraySaver implements Saver<Array> {
  private final FileSystem smoothFileSystem;

  public ArraySaver(FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  @Override
  public void save(Name name, Array array) {
    Path artifactPath = artifactPath(name);

    smoothFileSystem.delete(artifactPath);

    // Create dir explicitly. When fileArray is empty for loop below won't
    // create empty dir for us.
    smoothFileSystem.createDir(artifactPath);

    int i = 0;
    for (Value value : array.asIterable(Value.class)) {
      Path filePath = path(Integer.valueOf(i).toString());
      Path sourcePath = artifactPath.append(filePath);
      Path targetPath = targetPath(value);
      smoothFileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }
}
