package org.smoothbuild.cli.run;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.task.Output.output;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.layout.BuildVersion;
import org.smoothbuild.cli.layout.HashNode;
import org.smoothbuild.cli.layout.InstallationHashes;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;

public class Version implements Task0<Void> {
  private final InstallationHashes installationHashes;

  @Inject
  public Version(InstallationHashes installationHashes) {
    this.installationHashes = installationHashes;
  }

  @Override
  public Output<Void> execute() {
    var label = label("cli", "version");
    try {
      var info = info(createVersionText(installationHashes.installationNode()));
      return output(label, list(info));
    } catch (IOException e) {
      var fatal = fatal("ERROR: IO error when calculating installation hash: " + e.getMessage());
      return output(label, list(fatal));
    }
  }

  private static String createVersionText(HashNode hashNode) {
    return "smooth build version " + BuildVersion.VERSION + "\n\n"
        + hashNodeTreeLines("", hashNode).toString("\n");
  }

  private static List<String> hashNodeTreeLines(String indent, HashNode hashNode) {
    var header = indent + hashNode.toPrettyString();
    var children = hashNodeChildrenToLines(indent + "  ", hashNode.children());
    return list(header).appendAll(children);
  }

  private static List<String> hashNodeChildrenToLines(String indent, List<HashNode> hashNodes) {
    return hashNodes.map(n -> hashNodeTreeLines(indent, n)).flatMap(strings -> strings);
  }
}
