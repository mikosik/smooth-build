package org.smoothbuild.install;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.define.FileLocation;
import org.smoothbuild.lang.base.define.SModule;

import com.google.common.collect.ImmutableList;

public class InstallationHashes {
  private final InstallationPaths installationPaths;
  private final FullPathResolver fullPathResolver;

  @Inject
  public InstallationHashes(InstallationPaths installationPaths, FullPathResolver fullPathResolver) {
    this.installationPaths = installationPaths;
    this.fullPathResolver = fullPathResolver;
  }

  public HashNode installationNode() throws IOException {
    return new HashNode("installation", ImmutableList.of(sandboxNode(), standardLibsNode()));
  }

  public HashNode sandboxNode() throws IOException {
    return new HashNode("sandbox", ImmutableList.of(smoothJarNode(), javaPlatformNode()));
  }

  private HashNode smoothJarNode() throws IOException {
    return new HashNode("smooth.jar", Hash.of(installationPaths.smoothJar()));
  }

  private static HashNode javaPlatformNode() {
    return new HashNode("java platform", calculateJavaPlatformHash(System.getProperties()));
  }

  // visible for testing
  static Hash calculateJavaPlatformHash(Properties properties) {
    return Hash.of(
        hash(properties, "java.vendor"),
        hash(properties, "java.version"),
        hash(properties, "java.runtime.name"),
        hash(properties, "java.runtime.version"),
        hash(properties, "java.vm.name"),
        hash(properties, "java.vm.version"));
  }

  private static Hash hash(Properties properties, String name) {
    return Hash.of(properties.getProperty(name));
  }

  private HashNode standardLibsNode() throws IOException {
    ImmutableList.Builder<HashNode> builder = ImmutableList.builder();
    for (SModule module : InstallationPaths.STANDARD_LIBRARY_MODULES) {
      builder.add(moduleNode(module));
    }
    return new HashNode("standard libraries", builder.build());
  }

  private HashNode moduleNode(SModule module) throws IOException {
    Optional<HashNode> smoothFileNode = nodeFor(module.smoothFile());
    Optional<HashNode> nativeFileNode = nodeFor(module.nativeFile());
    var nodes = Stream.of(smoothFileNode, nativeFileNode)
        .flatMap(Optional::stream)
        .collect(toImmutableList());
    return new HashNode(module.name() + " module", nodes);
  }

  private Optional<HashNode> nodeFor(FileLocation fileLocation) throws IOException {
    Path resolvedPath = fullPathResolver.resolve(fileLocation);
    if (Files.exists(resolvedPath)) {
      return Optional.of(new HashNode(fileLocation.prefixedPath(), Hash.of(resolvedPath)));
    } else {
      return Optional.empty();
    }
  }
}
