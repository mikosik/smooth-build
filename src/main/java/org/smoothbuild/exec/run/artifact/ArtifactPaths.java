package org.smoothbuild.exec.run.artifact;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.HASHED_DB_PATH;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.SObject;

public class ArtifactPaths {
  public static Path targetPath(SObject object) {
    return HashedDb.dataFullPath(HASHED_DB_PATH, object.dataHash());
  }

  public static Path artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
