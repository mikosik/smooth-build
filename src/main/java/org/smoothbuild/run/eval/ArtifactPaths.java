package org.smoothbuild.run.eval;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;

import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.fs.base.PathS;

public class ArtifactPaths {
  public static PathS targetPath(ValueB valueB) {
    return HashedDb.dataFullPath(HASHED_DB_PATH, valueB.dataHash());
  }

  public static PathS artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
