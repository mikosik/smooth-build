package org.smoothbuild.exec.artifact;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;

import org.smoothbuild.db.bytecode.obj.base.ObjB;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.Path;

public class ArtifactPaths {
  public static Path targetPath(ObjB obj) {
    return HashedDb.dataFullPath(OBJECT_DB_PATH, obj.dataHash());
  }

  public static Path artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
