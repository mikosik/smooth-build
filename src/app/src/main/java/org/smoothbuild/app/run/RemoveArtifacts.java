package org.smoothbuild.app.run;

import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.dag.TryFunction0;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.virtualmachine.wire.Project;

public class RemoveArtifacts implements TryFunction0<Void> {
  private final Bucket bucket;

  @Inject
  public RemoveArtifacts(@Project Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Label label() {
    return Label.label("artifacts", "removeAll");
  }

  @Override
  public Try<Void> apply() {
    try {
      bucket.delete(ARTIFACTS_PATH);
      return success(null);
    } catch (IOException e) {
      return failure(error(e.getMessage()));
    }
  }
}
