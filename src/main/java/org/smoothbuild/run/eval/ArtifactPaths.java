package org.smoothbuild.run.eval;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;

import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.HashedDb;

public class ArtifactPaths {
  public static PathS targetPath(ValueB valueB) {
    return HashedDb.dataFullPath(HASHED_DB_PATH, valueB.dataHash());
  }

  public static PathS artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
