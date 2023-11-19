package org.smoothbuild.filesystem.space;

import static org.smoothbuild.filesystem.install.InstallationLayout.BIN_DIR_NAME;
import static org.smoothbuild.filesystem.install.InstallationLayout.STD_LIB_DIR_NAME;
import static org.smoothbuild.filesystem.space.Space.BINARY;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.filesystem.space.Space.STANDARD_LIBRARY;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import java.nio.file.Path;

public class DiskFileSystemModule extends AbstractModule {
  private final ImmutableMap<Space, Path> spaceToPath;

  public DiskFileSystemModule(Path installationDir, Path projectDir) {
    this.spaceToPath = ImmutableMap.of(
        PROJECT, projectDir,
        STANDARD_LIBRARY, installationDir.resolve(STD_LIB_DIR_NAME),
        BINARY, installationDir.resolve(BIN_DIR_NAME));
  }

  public DiskFileSystemModule(Path installationDir) {
    this.spaceToPath = ImmutableMap.of(
        STANDARD_LIBRARY, installationDir.resolve(STD_LIB_DIR_NAME),
        BINARY, installationDir.resolve(BIN_DIR_NAME));
  }

  @Override
  protected void configure() {
    bind(FileSystemFactory.class).to(DiskFileSystemFactory.class);
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, Path.class);
    spaceToPath.forEach((space, path) -> mapBinder.addBinding(space).toInstance(path));
  }
}
