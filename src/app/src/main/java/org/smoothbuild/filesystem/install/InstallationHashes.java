package org.smoothbuild.filesystem.install;

import static java.util.Arrays.asList;
import static org.smoothbuild.common.io.Paths.removeExtension;
import static org.smoothbuild.filesystem.install.InstallationLayout.SMOOTH_JAR_FILE_PATH;
import static org.smoothbuild.filesystem.install.InstallationLayout.STD_LIB_MODS;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.FileResolver;
import org.smoothbuild.vm.bytecode.hashed.Hash;

import com.google.common.collect.ImmutableList;

import io.vavr.collection.Array;
import jakarta.inject.Inject;

public class InstallationHashes {
  private final FileResolver fileResolver;

  @Inject
  public InstallationHashes(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  public HashNode installationNode() throws IOException {
    return new HashNode("installation", Array.of(smoothJarNode(), standardLibsNode()));
  }

  public HashNode sandboxNode() throws IOException {
    return new HashNode("sandbox", Array.of(smoothJarNode(), javaPlatformNode()));
  }

  private HashNode smoothJarNode() throws IOException {
    return new HashNode("smooth.jar", fileResolver.hashOf(SMOOTH_JAR_FILE_PATH));
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
        hash(properties, "java.vm.version")
    ));
  }

  private static Hash hash(Properties properties, String name) {
    return Hash.of(properties.getProperty(name));
  }

  private HashNode standardLibsNode() throws IOException {
    ImmutableList.Builder<HashNode> builder = ImmutableList.builder();
    for (var filePath : STD_LIB_MODS) {
      builder.add(moduleNode(filePath));
    }
    return new HashNode("standard libraries", Array.ofAll(builder.build()));
  }

  private HashNode moduleNode(FilePath filePath) throws IOException {
    var smoothNode = nodeFor(filePath);
    var nativeNode = nodeForNativeJarFor(filePath);
    var nodes =
        nativeNode.isPresent() ? Array.of(smoothNode, nativeNode.get()) : Array.of(smoothNode);
    var moduleName = removeExtension(filePath.toString());
    return new HashNode(moduleName + " module", nodes);
  }

  private Optional<HashNode> nodeForNativeJarFor(FilePath filePath) throws IOException {
    FilePath nativeFilePath = filePath.withExtension("jar");
    return switch (fileResolver.pathState(nativeFilePath)) {
      case FILE -> Optional.of(nodeFor(nativeFilePath));
      case DIR, NOTHING -> Optional.empty();
    };
  }

  private HashNode nodeFor(FilePath filePath) throws IOException {
    return new HashNode(filePath.toString(), fileResolver.hashOf(filePath));
  }
}
