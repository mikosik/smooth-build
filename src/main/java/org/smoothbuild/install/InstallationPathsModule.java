package org.smoothbuild.install;

import java.nio.file.Path;

import com.google.inject.AbstractModule;

public class InstallationPathsModule extends AbstractModule {
  private final Path installationDir;

  public InstallationPathsModule(Path installationDir) {
    this.installationDir = installationDir;
  }

  @Override
  protected void configure() {
    bind(InstallationPaths.class).toInstance(new InstallationPaths(installationDir));
  }
}
