package org.smoothbuild.cli.layout;

import static java.util.Arrays.asList;
import static org.smoothbuild.cli.layout.Layout.STD_LIB_MODULES;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.Filesystem;
import org.smoothbuild.common.filesystem.base.FullPath;

public class InstallationHashes {
  private final Filesystem filesystem;

  @Inject
  public InstallationHashes(Filesystem filesystem) {
    this.filesystem = filesystem;
  }

  public HashNode installationNode() throws IOException {
    return new HashNode("installation", list(smoothJarNode(), standardLibrariesNode()));
  }

  public HashNode sandboxNode() throws IOException {
    return new HashNode("sandbox", list(smoothJarNode(), javaPlatformNode()));
  }

  private HashNode smoothJarNode() throws IOException {
    var fullPath = Layout.SMOOTH_JAR_FILE_PATH;
    return new HashNode(fullPath.path().lastPart().toString(), hashOf(fullPath));
  }

  private static HashNode javaPlatformNode() {
    return new HashNode("java platform", calculateJavaPlatformHash(System.getProperties()));
  }

  // visible for testing
  static Hash calculateJavaPlatformHash(Properties properties) {
    return Hash.of(asList(
        hash(properties, "java.vendor"),
        hash(properties, "java.version"),
        hash(properties, "java.runtime.name"),
        hash(properties, "java.runtime.version"),
        hash(properties, "java.vm.name"),
        hash(properties, "java.vm.version")));
  }

  private static Hash hash(Properties properties, String name) {
    return Hash.of(properties.getProperty(name));
  }

  private HashNode standardLibrariesNode() throws IOException {
    var builder = new ArrayList<HashNode>();
    for (var fullPath : STD_LIB_MODULES) {
      builder.add(moduleNode(fullPath));
    }
    return new HashNode("standard libraries", listOfAll(builder));
  }

  private HashNode moduleNode(FullPath fullPath) throws IOException {
    var smoothNode = nodeFor(fullPath);
    var nativeNode = nodeForNativeJarFor(fullPath);
    var nodes = nativeNode.isSome() ? list(smoothNode, nativeNode.get()) : list(smoothNode);
    return new HashNode(fullPath.withExtension("") + " module", nodes);
  }

  private Maybe<HashNode> nodeForNativeJarFor(FullPath fullPath) throws IOException {
    FullPath nativeFileFullPath = fullPath.withExtension("jar");
    return switch (filesystem.pathState(nativeFileFullPath)) {
      case FILE -> some(nodeFor(nativeFileFullPath));
      case DIR, NOTHING -> none();
    };
  }

  private HashNode nodeFor(FullPath fullPath) throws IOException {
    return new HashNode(fullPath.toString(), hashOf(fullPath));
  }

  private Hash hashOf(FullPath fullPath) throws IOException {
    try (var source = filesystem.source(fullPath)) {
      return Hash.of(source);
    }
  }
}
