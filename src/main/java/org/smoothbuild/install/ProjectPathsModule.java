package org.smoothbuild.install;

import java.nio.file.Path;

import com.google.inject.AbstractModule;

public class ProjectPathsModule extends AbstractModule {
  private final Path projectDir;

  public ProjectPathsModule(Path projectDir) {
    this.projectDir = projectDir;
  }

  @Override
  protected void configure() {
    bind(ProjectPaths.class).toInstance(new ProjectPaths(projectDir));
  }
}
