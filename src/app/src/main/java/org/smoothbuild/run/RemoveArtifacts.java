package org.smoothbuild.run;

import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.ARTIFACTS_PATH;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.io.IOException;
import java.util.function.Function;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.filesystem.space.ForSpace;
import org.smoothbuild.out.log.Maybe;

import io.vavr.Tuple;
import io.vavr.Tuple0;
import jakarta.inject.Inject;

public class RemoveArtifacts implements Function<Tuple0, Maybe<Tuple0>> {
  private final FileSystem fileSystem;

  @Inject
  public RemoveArtifacts(@ForSpace(PROJECT) FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Maybe<Tuple0> apply(Tuple0 unused) {
    try {
      fileSystem.delete(ARTIFACTS_PATH);
      return maybe(Tuple.empty());
    } catch (IOException e) {
      return maybeLogs(error(e.getMessage()));
    }
  }
}
