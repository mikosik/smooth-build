package org.smoothbuild.cli.dagger;

import dagger.Module;
import dagger.Provides;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.virtualmachine.dagger.Project;

@Module
public class CliTestModule {
  @Provides
  @Artifacts
  FullPath provideArtifacts(@Project FullPath projectPath) {
    return projectPath.append(".smooth/artifacts");
  }
}
