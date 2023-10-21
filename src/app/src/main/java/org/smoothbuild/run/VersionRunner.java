package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;

import java.io.IOException;

import org.smoothbuild.filesystem.install.BuildVersion;
import org.smoothbuild.filesystem.install.HashNode;
import org.smoothbuild.filesystem.install.InstallationHashes;
import org.smoothbuild.out.report.Console;

import jakarta.inject.Inject;

public class VersionRunner {
  private final Console console;
  private final InstallationHashes installationHashes;

  @Inject
  public VersionRunner(Console console, InstallationHashes installationHashes) {
    this.console = console;
    this.installationHashes = installationHashes;
  }

  public int run() {
    try {
      HashNode hashNode = installationHashes.installationNode();
      console.println("smooth build version " + BuildVersion.VERSION);
      console.println("");
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
    console.println(name + " " + hash);
    hashNode.children().forEach(ch -> printHashNode(indent + "  ", ch));
  }
}
