package org.smoothbuild.run;

import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.io.IOException;
import java.util.function.Function;

import org.smoothbuild.filesystem.install.BuildVersion;
import org.smoothbuild.filesystem.install.HashNode;
import org.smoothbuild.filesystem.install.InstallationHashes;
import org.smoothbuild.out.log.Maybe;

import io.vavr.Tuple0;
import io.vavr.collection.Array;
import jakarta.inject.Inject;

public class Version implements Function<Tuple0, Maybe<String>> {
  private final InstallationHashes installationHashes;

  @Inject
  public Version(InstallationHashes installationHashes) {
    this.installationHashes = installationHashes;
  }

  @Override
  public Maybe<String> apply(Tuple0 tuple0) {
    try {
      return maybe(createVersionText(installationHashes.installationNode()));
    } catch (IOException e) {
      return maybeLogs(error(
          "ERROR: IO error when calculating installation hash: " + e.getMessage()));
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

  private static String hashNodeTreeToString(String indent, Array<HashNode> hashNodes) {
    return hashNodes.map(n -> hashNodeTreeToString(indent, n)).mkString("\n");
  }
}
