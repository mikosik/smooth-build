package org.smoothbuild.task.save;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_DIR;
import static org.smoothbuild.SmoothConstants.OBJECTS_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.hashed.HashCodes;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Name;

public class ArtifactPaths {
  public static Path targetPath(Value value) {
    return OBJECTS_DIR.append(HashCodes.toPath(value.hash()));
  }

  public static Path artifactPath(Name name) {
    return ARTIFACTS_DIR.append(path(name.value()));
  }
}
