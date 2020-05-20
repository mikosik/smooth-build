package org.smoothbuild.install;

import static org.smoothbuild.install.InstallationPaths.installationPaths;

import com.google.inject.AbstractModule;

public class InstallationPathsModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(InstallationPaths.class).toInstance(installationPaths());
  }
}
