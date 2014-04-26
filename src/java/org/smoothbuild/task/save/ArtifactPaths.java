package org.smoothbuild.task.save;

import static org.smoothbuild.SmoothContants.ARTIFACTS_DIR;
import static org.smoothbuild.SmoothContants.OBJECTS_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.hashed.HashCodes;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Hashed;
import org.smoothbuild.lang.function.base.Name;

public class ArtifactPaths {
  public static Path targetPath(Hashed hashed) {
    return OBJECTS_DIR.append(HashCodes.toPath(hashed.hash()));
  }

  public static Path artifactPath(Name name) {
    return ARTIFACTS_DIR.append(path(name.value()));
  }
}
