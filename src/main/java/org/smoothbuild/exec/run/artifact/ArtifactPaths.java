package org.smoothbuild.exec.run.artifact;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.HASHED_DB_PATH;
import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.SObject;

public class ArtifactPaths {
  public static Path targetPath(SObject object) {
    return HashedDb.dataFullPath(HASHED_DB_PATH, object.dataHash());
  }

  public static Path artifactPath(String name) {
    return artifactPath(path(name));
  }

  public static Path artifactPath(Path path) {
    return ARTIFACTS_PATH.append(path);
  }
}
