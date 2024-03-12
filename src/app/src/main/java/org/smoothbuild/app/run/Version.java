package org.smoothbuild.app.run;

import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.app.layout.BuildVersion;
import org.smoothbuild.app.layout.HashNode;
import org.smoothbuild.app.layout.InstallationHashes;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;

public class Version implements TryFunction<Tuple0, String> {
  private final InstallationHashes installationHashes;

  @Inject
  public Version(InstallationHashes installationHashes) {
    this.installationHashes = installationHashes;
  }

  @Override
  public Try<String> apply(Tuple0 tuple0) {
    try {
      return success(createVersionText(installationHashes.installationNode()));
    } catch (IOException e) {
      return failure(
          error("ERROR: IO error when calculating installation hash: " + e.getMessage()));
    }
  }

  private static String createVersionText(HashNode hashNode) {
    return "smooth build version " + BuildVersion.VERSION + "\n\n"
        + hashNodeTreeToString("", hashNode);
  }

  private static String hashNodeTreeToString(String indent, HashNode hashNode) {
    return indent + hashNode.toPrettyString() + "\n"
        + hashNodeTreeToString(indent + "  ", hashNode.children());
  }

  private static String hashNodeTreeToString(String indent, List<HashNode> hashNodes) {
    return hashNodes.map(n -> hashNodeTreeToString(indent, n)).toString("\n");
  }
}
