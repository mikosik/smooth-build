package org.smoothbuild.run.eval;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.db.HashedDb;
import org.smoothbuild.fs.base.PathS;

public class ArtifactPaths {
  public static PathS targetPath(ObjB obj) {
    return HashedDb.dataFullPath(HASHED_DB_PATH, obj.dataHash());
  }

  public static PathS artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
