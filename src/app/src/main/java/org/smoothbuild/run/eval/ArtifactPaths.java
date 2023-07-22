package org.smoothbuild.run.eval;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.vm.bytecode.hashed.HashedDb.projectPathToHashedFile;

import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

public class ArtifactPaths {
  public static PathS targetPath(ValueB valueB) {
    return projectPathToHashedFile(valueB.dataHash());
  }

  public static PathS artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
