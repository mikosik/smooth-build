package org.smoothbuild.filesystem.install;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.io.Paths.removeExtension;
import static org.smoothbuild.filesystem.install.InstallationLayout.SMOOTH_JAR;
import static org.smoothbuild.filesystem.space.Space.BINARY;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.compile.fs.lang.define.ModuleResources;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.FileResolver;
import org.smoothbuild.filesystem.space.ForSpace;
import org.smoothbuild.vm.bytecode.hashed.Hash;

import com.google.common.collect.ImmutableList;

import jakarta.inject.Inject;

public class InstallationHashes {
  private final FileSystem fileSystem;
  private final FileResolver fileResolver;
  private final ModuleResourcesDetector moduleResourcesDetector;

  @Inject
  public InstallationHashes(
      @ForSpace(BINARY) FileSystem fileSystem,
      FileResolver fileResolver,
      ModuleResourcesDetector moduleResourcesDetector) {
    this.fileSystem = fileSystem;
    this.fileResolver = fileResolver;
    this.moduleResourcesDetector = moduleResourcesDetector;
  }

  public HashNode installationNode() throws IOException {
    return new HashNode("installation", list(smoothJarNode(), standardLibsNode()));
  }

  public HashNode sandboxNode() throws IOException {
    return new HashNode("sandbox", list(smoothJarNode(), javaPlatformNode()));
  }

  private HashNode smoothJarNode() throws IOException {
    return new HashNode("smooth.jar", Hash.of(fileSystem.source(SMOOTH_JAR)));
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
    var modules = moduleResourcesDetector.detect(InstallationLayout.STD_LIB_MODS);
    for (ModuleResources module : modules) {
      builder.add(modNode(module));
    }
    return new HashNode("standard libraries", builder.build());
  }

  private HashNode modNode(ModuleResources module) throws IOException {
    FilePath smoothFile = module.smoothFile();
    Optional<HashNode> smoothNode = nodeFor(Optional.of(smoothFile));
    Optional<HashNode> nativeNode = nodeFor(module.nativeFile());
    var nodes = Stream.of(smoothNode, nativeNode)
        .flatMap(Optional::stream)
        .collect(toImmutableList());
    var moduleName = removeExtension(smoothFile.toString());
    return new HashNode(moduleName + " module", nodes);
  }

  private Optional<HashNode> nodeFor(Optional<FilePath> file) throws IOException {
    if (file.isPresent()) {
      FilePath filePath = file.get();
      return Optional.of(new HashNode(filePath.toString(), fileResolver.hashOf(filePath)));
    } else {
      return Optional.empty();
    }
  }
}
