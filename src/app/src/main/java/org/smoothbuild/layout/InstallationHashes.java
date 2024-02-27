package org.smoothbuild.layout;

import static java.util.Arrays.asList;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.io.Paths.removeExtension;
import static org.smoothbuild.layout.Layout.SMOOTH_JAR_FILE_PATH;
import static org.smoothbuild.layout.Layout.STANDARD_LIBRARY_MODULES;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.space.FilePath;
import org.smoothbuild.common.filesystem.space.FileResolver;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class InstallationHashes {
  private final FileResolver fileResolver;

  @Inject
  public InstallationHashes(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  public HashNode installationNode() throws IOException {
    return new HashNode("installation", list(smoothJarNode(), standardLibrariesNode()));
  }

  public HashNode sandboxNode() throws IOException {
    return new HashNode("sandbox", list(smoothJarNode(), javaPlatformNode()));
  }

  private HashNode smoothJarNode() throws IOException {
    var filePath = SMOOTH_JAR_FILE_PATH;
    return new HashNode(filePath.path().lastPart().toString(), hashOf(filePath));
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
    for (var filePath : STANDARD_LIBRARY_MODULES) {
      builder.add(moduleNode(filePath));
    }
    return new HashNode("standard libraries", listOfAll(builder));
  }

  private HashNode moduleNode(FilePath filePath) throws IOException {
    var smoothNode = nodeFor(filePath);
    var nativeNode = nodeForNativeJarFor(filePath);
    var nodes = nativeNode.isSome() ? list(smoothNode, nativeNode.get()) : list(smoothNode);
    var moduleName = removeExtension(filePath.toString());
    return new HashNode(moduleName + " module", nodes);
  }

  private Maybe<HashNode> nodeForNativeJarFor(FilePath filePath) throws IOException {
    FilePath nativeFilePath = filePath.withExtension("jar");
    return switch (fileResolver.pathState(nativeFilePath)) {
      case FILE -> some(nodeFor(nativeFilePath));
      case DIR, NOTHING -> none();
    };
  }

  private HashNode nodeFor(FilePath filePath) throws IOException {
    return new HashNode(filePath.toString(), hashOf(filePath));
  }

  private Hash hashOf(FilePath filePath) throws IOException {
    return Hash.of(fileResolver.source(filePath));
  }
}
