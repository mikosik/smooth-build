package org.smoothbuild.install;

import static org.smoothbuild.lang.base.define.Space.STANDARD_LIBRARY;
import static org.smoothbuild.lang.base.define.Space.USER;

import java.nio.file.Path;

import org.smoothbuild.lang.base.define.Space;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;

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

    bind(FullPathResolver.class).toInstance(
        new FullPathResolver(resolvers(installationPaths.standardLibraryDir(), projectDir)));
  }

  private ImmutableMap<Space, Path> resolvers(Path standardLibraryDir, Path projectDir) {
    if (projectDir == null) {
      return ImmutableMap.of(STANDARD_LIBRARY, standardLibraryDir);
    } else {
      return ImmutableMap.of(STANDARD_LIBRARY, standardLibraryDir, USER, projectDir);
    }
  }
}
