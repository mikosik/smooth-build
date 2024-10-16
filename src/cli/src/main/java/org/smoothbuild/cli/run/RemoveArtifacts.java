package org.smoothbuild.cli.run;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.task.Output.output;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.virtualmachine.wire.Project;

public class RemoveArtifacts implements Task0<Tuple0> {
  private final Bucket bucket;

  @Inject
  public RemoveArtifacts(@Project Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Output<Tuple0> execute() {
    var label = label("artifacts", "removeAll");
    try {
      bucket.delete(Layout.ARTIFACTS_PATH);
      return output(label, list());
    } catch (IOException e) {
      return output(label, list(error(e.getMessage())));
    }
  }
}
