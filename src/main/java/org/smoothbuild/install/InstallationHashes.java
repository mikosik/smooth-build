package org.smoothbuild.install;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.ModulePath;

import com.google.common.collect.ImmutableList;

public class InstallationHashes {
  private final InstallationPaths installationPaths;

  @Inject
  public InstallationHashes(InstallationPaths installationPaths) {
    this.installationPaths = installationPaths;
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
    List<ModulePath> modules = installationPaths.slibModules();
    ImmutableList.Builder<HashNode> builder = ImmutableList.builder();
    for (ModulePath module : modules) {
      builder.add(moduleNode(module));
    }
    return new HashNode("standard libraries", builder.build());
  }

  private static HashNode moduleNode(ModulePath module) throws IOException {
    HashNode smoothFile = new HashNode(module.smooth().shorted(), Hash.of(module.smooth().path()));
    Path nativeJar = module.nativ().path();
    String name = module.name() + " module";
    if (Files.exists(nativeJar)) {
      HashNode jarFile = new HashNode(module.nativ().shorted(), Hash.of(nativeJar));
      return new HashNode(name, ImmutableList.of(smoothFile, jarFile));
    } else {
      return new HashNode(name, ImmutableList.of(smoothFile));
    }
  }
}
