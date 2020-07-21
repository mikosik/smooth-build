package org.smoothbuild.exec.task.artifact;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.record.base.Record;

public class ArtifactPaths {
  public static Path targetPath(Record record) {
    return HashedDb.dataFullPath(HASHED_DB_PATH, record.dataHash());
  }

  public static Path artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
