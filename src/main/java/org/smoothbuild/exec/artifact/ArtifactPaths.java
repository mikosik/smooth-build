package org.smoothbuild.exec.artifact;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.io.fs.base.Path;

public class ArtifactPaths {
  public static Path targetPath(ObjH obj) {
    return HashedDb.dataFullPath(OBJECT_DB_PATH, obj.dataHash());
  }

  public static Path artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
