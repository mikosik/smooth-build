package org.smoothbuild.task.save;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.VALUES_DB_PATH;
import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Value;

public class ArtifactPaths {
  public static Path targetPath(Value value) {
    return VALUES_DB_PATH.append(Hash.toPath(value.hash()));
  }

  public static Path artifactPath(Name name) {
    return ARTIFACTS_PATH.append(path(name.value()));
  }
}
