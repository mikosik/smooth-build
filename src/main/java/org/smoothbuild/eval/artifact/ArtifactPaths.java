package org.smoothbuild.eval.artifact;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.db.HashedDb;
import org.smoothbuild.io.fs.base.Path;

public class ArtifactPaths {
  public static Path targetPath(ObjB obj) {
    return HashedDb.dataFullPath(OBJECT_DB_PATH, obj.dataHash());
  }

  public static Path artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
