package org.smoothbuild.app.run;

import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.app.layout.SmoothSpace.PROJECT;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.app.layout.ForSpace;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;

public class RemoveArtifacts implements TryFunction<Tuple0, Tuple0> {
  private final FileSystem fileSystem;

  @Inject
  public RemoveArtifacts(@ForSpace(PROJECT) FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Try<Tuple0> apply(Tuple0 unused) {
    try {
      fileSystem.delete(ARTIFACTS_PATH);
      return success(tuple());
    } catch (IOException e) {
      return failure(error(e.getMessage()));
    }
  }
}
