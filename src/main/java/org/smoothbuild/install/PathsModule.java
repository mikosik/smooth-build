package org.smoothbuild.install;

import static org.smoothbuild.io.fs.base.Space.PRJ;
import static org.smoothbuild.io.fs.base.Space.SDK;

import java.nio.file.Path;

import org.smoothbuild.io.fs.base.Space;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class PathsModule extends AbstractModule {
  private final Path projectDir;
  private final Path installationDir;

  public PathsModule(Path installationDir) {
    this(installationDir, null);
  }

  public PathsModule(Path installationDir, Path projectDir) {
    this.installationDir = installationDir;
    this.projectDir = projectDir;
  }

  @Override
  protected void configure() {
    InstallationPaths installationPaths = new InstallationPaths(installationDir);
    bind(InstallationPaths.class).toInstance(installationPaths);
  }

  @Provides
  @Singleton
  public ImmutableMap<Space, Path> provideSpaceToPathMap(InstallationPaths installationPaths) {
    Path sdkApiDir = installationPaths.standardLibraryDir();
    if (projectDir == null) {
      return ImmutableMap.of(SDK, sdkApiDir);
    } else {
      return ImmutableMap.of(SDK, sdkApiDir, PRJ, projectDir);
    }
  }
}
