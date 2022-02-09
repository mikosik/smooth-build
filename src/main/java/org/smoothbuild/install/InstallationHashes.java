package org.smoothbuild.install;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static org.smoothbuild.install.InstallationPaths.SDK_MODS;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.smoothbuild.db.Hash;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.FileResolver;
import org.smoothbuild.lang.base.define.ModFiles;
import org.smoothbuild.lang.base.define.ModPath;

import com.google.common.collect.ImmutableList;

public class InstallationHashes {
  private final InstallationPaths installationPaths;
  private final FileResolver fileResolver;
  private final ModFilesDetector modFilesDetector;

  @Inject
  public InstallationHashes(InstallationPaths installationPaths, FileResolver fileResolver,
      ModFilesDetector modFilesDetector) {
    this.installationPaths = installationPaths;
    this.fileResolver = fileResolver;
    this.modFilesDetector = modFilesDetector;
  }

  public HashNode installationNode() throws IOException {
    return new HashNode("installation", list(sandboxNode(), standardLibsNode()));
  }

  public HashNode sandboxNode() throws IOException {
    return new HashNode("sandbox", list(smoothJarNode(), javaPlatformNode()));
  }

  private HashNode smoothJarNode() throws IOException {
    return new HashNode("smooth.jar", Hash.of(installationPaths.smoothJar()));
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
    var files = modFilesDetector.detect(SDK_MODS);
    for (Entry<ModPath, ModFiles> entry : files.entrySet()) {
      builder.add(modNode(entry.getKey(), entry.getValue()));
    }
    return new HashNode("standard libraries", builder.build());
  }

  private HashNode modNode(ModPath path, ModFiles modFiles) throws IOException {
    Optional<HashNode> smoothNode = nodeFor(Optional.of(modFiles.smoothFile()));
    Optional<HashNode> nativeNode = nodeFor(modFiles.nativeFile());
    var nodes = Stream.of(smoothNode, nativeNode)
        .flatMap(Optional::stream)
        .collect(toImmutableList());
    return new HashNode(path + " module", nodes);
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
