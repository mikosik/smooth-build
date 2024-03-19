package org.smoothbuild.app.run;

import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.app.layout.Layout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.app.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.app.layout.SmoothBucketId.PROJECT;
import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.app.layout.ForBucket;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.dag.TryFunction0;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;

public class Clean implements TryFunction0<String> {
  private final Bucket bucket;

  @Inject
  public Clean(@ForBucket(PROJECT) Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Try<String> apply() {
    var logger = new Logger();
    deleteDir(logger, HASHED_DB_PATH);
    deleteDir(logger, COMPUTATION_CACHE_PATH);
    deleteDir(logger, ARTIFACTS_PATH);
    return success("Cache and artifacts removed.");
  }

  private void deleteDir(Logger logger, Path path) {
    try {
      bucket.delete(path);
    } catch (IOException e) {
      logger.error("Unable to delete " + path + ".");
    }
  }
}
