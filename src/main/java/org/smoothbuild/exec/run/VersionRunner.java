package org.smoothbuild.exec.run;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.install.BuildVersion;
import org.smoothbuild.install.HashNode;
import org.smoothbuild.install.InstallationHashes;

public class VersionRunner {
  private final Console console;

  @Inject
  public VersionRunner(Console console) {
    this.console = console;
  }

  public Integer run() {
    try {
      HashNode hashNode = InstallationHashes.installationNode();
      console.print("smooth build version " + BuildVersion.VERSION + "\n\n");
      printHashNode("", hashNode);
      return EXIT_CODE_SUCCESS;
    } catch (IOException e) {
      console.println("ERROR: IO error when calculating installation hash: " + e.getMessage());
      return EXIT_CODE_ERROR;
    }
  }

  private void printHashNode(String indent, HashNode hashNode) {
    String name = indent + hashNode.name();
    String hash = hashNode.hash().toString();
    console.println(name + padStart(hash, 80 - name.length(), ' '));
    hashNode.children().forEach(ch -> printHashNode(indent + "  ", ch));
  }
}
