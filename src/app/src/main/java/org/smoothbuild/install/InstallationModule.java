package org.smoothbuild.install;

import java.nio.file.Path;

import com.google.inject.AbstractModule;

public class InstallationModule extends AbstractModule {
  private final Path installationDir;

  public InstallationModule(Path installationDir) {
    this.installationDir = installationDir;
  }

  @Override
  protected void configure() {
    bind(InstallationPaths.class).toInstance(new InstallationPaths(installationDir));
  }
}
