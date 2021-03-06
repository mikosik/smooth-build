package org.smoothbuild.exec.artifact;

import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.RECORD_DB_PATH;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.io.fs.base.Path;

public class ArtifactPaths {
  public static Path targetPath(Record record) {
    return HashedDb.dataFullPath(RECORD_DB_PATH, record.dataHash());
  }

  public static Path artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
