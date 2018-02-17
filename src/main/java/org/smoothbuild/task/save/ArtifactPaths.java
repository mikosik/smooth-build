package org.smoothbuild.task.save;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.VALUES_DB_PATH;
import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Value;

public class ArtifactPaths {
  public static Path targetPath(Value value) {
    return VALUES_DB_PATH.append(Hash.toPath(value.dataHash()));
  }

  public static Path artifactPath(String name) {
    return artifactPath(path(toFileName(name)));
  }

  public static Path artifactPath(Path path) {
    return ARTIFACTS_PATH.append(path);
  }

  public static String toFileName(String name) {
    int index = name.lastIndexOf('_');
    if (index == -1) {
      return name;
    } else {
      return new StringBuilder(name).replace(index, index + 1, ".").toString();
    }
  }
}
