package org.smoothbuild.app.run;

import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.app.layout.SmoothBucketId.PROJECT;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.app.layout.ForBucket;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;

public class RemoveArtifacts implements TryFunction<Tuple0, Tuple0> {
  private final Bucket bucket;

  @Inject
  public RemoveArtifacts(@ForBucket(PROJECT) Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Try<Tuple0> apply(Tuple0 unused) {
    try {
      bucket.delete(ARTIFACTS_PATH);
      return success(tuple());
    } catch (IOException e) {
      return failure(error(e.getMessage()));
    }
  }
}
