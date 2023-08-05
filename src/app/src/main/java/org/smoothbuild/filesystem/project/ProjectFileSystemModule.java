package org.smoothbuild.filesystem.project;

import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.filesystem.space.SpaceUtils.addMapBindingForSpaceFileSystem;
import static org.smoothbuild.filesystem.space.SpaceUtils.forSpace;

import org.smoothbuild.common.filesystem.base.FileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Key;

public class ProjectFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Key.get(FileSystem.class, forSpace(PROJECT))).toProvider(ProjectFileSystemProvider.class);
    addMapBindingForSpaceFileSystem(binder(), PROJECT);
  }
}
