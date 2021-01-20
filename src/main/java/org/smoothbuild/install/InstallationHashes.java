package org.smoothbuild.install;

import static org.smoothbuild.install.InstallationPaths.standardLibraryModuleLocations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.define.ModuleLocation;

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
    for (ModuleLocation module : standardLibraryModuleLocations()) {
      builder.add(moduleNode(module));
    }
    return new HashNode("standard libraries", builder.build());
  }

  private HashNode moduleNode(ModuleLocation moduleLocation) throws IOException {
    HashNode smoothFile = new HashNode(
        moduleLocation.prefixedPath(), Hash.of(fullPathResolver.resolve(moduleLocation)));
    ModuleLocation nativeModuleLocation = moduleLocation.toNative();
    Path nativeFullPath = fullPathResolver.resolve(nativeModuleLocation);
    String name = moduleLocation.name() + " module";
    if (Files.exists(nativeFullPath)) {
      HashNode jarFile = new HashNode(nativeModuleLocation.prefixedPath(), Hash.of(nativeFullPath));
      return new HashNode(name, ImmutableList.of(smoothFile, jarFile));
    } else {
      return new HashNode(name, ImmutableList.of(smoothFile));
    }
  }
}
