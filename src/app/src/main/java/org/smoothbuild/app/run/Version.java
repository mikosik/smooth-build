package org.smoothbuild.app.run;

import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.app.layout.BuildVersion;
import org.smoothbuild.app.layout.HashNode;
import org.smoothbuild.app.layout.InstallationHashes;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dag.TryFunction0;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;

public class Version implements TryFunction0<Void> {
  private final InstallationHashes installationHashes;

  @Inject
  public Version(InstallationHashes installationHashes) {
    this.installationHashes = installationHashes;
  }

  @Override
  public Label label() {
    return Label.label("cli", "version");
  }

  @Override
  public Try<Void> apply() {
    try {
      return success(null, info(createVersionText(installationHashes.installationNode())));
    } catch (IOException e) {
      return failure(
          error("ERROR: IO error when calculating installation hash: " + e.getMessage()));
    }
  }

  private static String createVersionText(HashNode hashNode) {
    return "smooth build version " + BuildVersion.VERSION + "\n\n"
        + hashNodeTreeLines("", hashNode).toString("\n");
  }

  private static List<String> hashNodeTreeLines(String indent, HashNode hashNode) {
    var header = indent + hashNode.toPrettyString();
    var children = hashNodeChildrenToLines(indent + "  ", hashNode.children());
    return List.list(header).appendAll(children);
  }

  private static List<String> hashNodeChildrenToLines(String indent, List<HashNode> hashNodes) {
    return hashNodes.map(n -> hashNodeTreeLines(indent, n)).flatMap(strings -> strings);
  }
}
