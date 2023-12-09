package org.smoothbuild.run;

import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.ARTIFACTS_PATH;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Try.failure;
import static org.smoothbuild.out.log.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.function.Function;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.filesystem.space.ForSpace;
import org.smoothbuild.out.log.Try;

public class RemoveArtifacts implements Function<Tuple0, Try<Tuple0>> {
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
