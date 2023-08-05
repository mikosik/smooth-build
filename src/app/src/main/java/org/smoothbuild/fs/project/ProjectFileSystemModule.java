package org.smoothbuild.fs.project;

import static org.smoothbuild.fs.space.Space.PROJECT;
import static org.smoothbuild.fs.space.SpaceUtils.addMapBindingForSpaceFileSystem;
import static org.smoothbuild.fs.space.SpaceUtils.forSpace;

import org.smoothbuild.common.fs.base.FileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Key;

public class ProjectFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Key.get(FileSystem.class, forSpace(PROJECT))).toProvider(ProjectFileSystemProvider.class);
    addMapBindingForSpaceFileSystem(binder(), PROJECT);
  }
}
