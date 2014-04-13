package org.smoothbuild.task.exec.save;

import static org.smoothbuild.db.DbModule.RESULTS_DIR;
import static org.smoothbuild.db.DbModule.VALUE_DB_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.hashed.HashCodes;
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
