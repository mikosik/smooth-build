package org.smoothbuild.task.exec.save;

import static org.smoothbuild.io.cache.CacheModule.RESULTS_DIR;
import static org.smoothbuild.io.cache.CacheModule.VALUE_DB_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.io.cache.hash.HashCodes;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Hashed;
import org.smoothbuild.lang.function.base.Name;

public class Savers {
  public static Path targetPath(Hashed hashed) {
    return VALUE_DB_DIR.append(HashCodes.toPath(hashed.hash()));
  }

  public static Path artifactPath(Name name) {
    return RESULTS_DIR.append(path(name.value()));
  }
}
