package org.smoothbuild.cli.run;

import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.TryFunction0;
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
      bucket.delete(Layout.ARTIFACTS_PATH);
      return success(null);
    } catch (IOException e) {
      return failure(error(e.getMessage()));
    }
  }
}
