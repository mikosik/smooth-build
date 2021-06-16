package org.smoothbuild.install;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.install.InstallationPaths.STANDARD_LIBRARY_MODULES;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.define.FileLocation;
import org.smoothbuild.lang.base.define.ModuleFiles;

import com.google.common.collect.ImmutableList;

public class InstallationHashes {
  private final InstallationPaths installationPaths;
  private final FullPathResolver fullPathResolver;
  private final ModuleFilesDetector moduleFilesDetector;

  @Inject
  public InstallationHashes(InstallationPaths installationPaths, FullPathResolver fullPathResolver,
      ModuleFilesDetector moduleFilesDetector) {
    this.installationPaths = installationPaths;
    this.fullPathResolver = fullPathResolver;
    this.moduleFilesDetector = moduleFilesDetector;
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
    for (ModuleFiles module : moduleFilesDetector.detect(STANDARD_LIBRARY_MODULES)) {
      builder.add(moduleNode(module));
    }
    return new HashNode("standard libraries", builder.build());
  }

  private HashNode moduleNode(ModuleFiles moduleFiles) throws IOException {
    Optional<HashNode> smoothNode = nodeFor(Optional.of(moduleFiles.smoothFile()));
    Optional<HashNode> nativeNode = nodeFor(moduleFiles.nativeFile());
    var nodes = Stream.of(smoothNode, nativeNode)
        .flatMap(Optional::stream)
        .collect(toImmutableList());
    return new HashNode(moduleFiles.name() + " module", nodes);
  }

  private Optional<HashNode> nodeFor(Optional<FileLocation> file) throws IOException {
    if (file.isPresent()) {
      FileLocation fileLocation = file.get();
      Path resolvedPath = fullPathResolver.resolve(fileLocation);
      return Optional.of(new HashNode(fileLocation.prefixedPath(), Hash.of(resolvedPath)));
    } else {
      return Optional.empty();
    }
  }
}
