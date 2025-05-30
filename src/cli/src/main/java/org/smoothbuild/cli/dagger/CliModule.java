package org.smoothbuild.cli.dagger;

import static org.smoothbuild.cli.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.cli.layout.Layout.BYTECODE_DB_PATH;
import static org.smoothbuild.cli.layout.Layout.COMPUTATION_DB_PATH;
import static org.smoothbuild.cli.layout.Layout.PROJECT_PATH;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import org.smoothbuild.cli.layout.SandboxHashProvider;
import org.smoothbuild.cli.layout.SandboxHashProviderInitializer;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.virtualmachine.dagger.BytecodeDb;
import org.smoothbuild.virtualmachine.dagger.ComputationDb;
import org.smoothbuild.virtualmachine.dagger.Project;
import org.smoothbuild.virtualmachine.dagger.Sandbox;

@Module
public interface CliModule {
  @Provides
  @Artifacts
  static FullPath provideArtifacts() {
    return ARTIFACTS_PATH;
  }

  @Provides
  @BytecodeDb
  static FullPath provideBytecodeDb() {
    return BYTECODE_DB_PATH;
  }

  @Provides
  @ComputationDb
  static FullPath provideComputationDb() {
    return COMPUTATION_DB_PATH;
  }

  @Provides
  @Project
  static FullPath provideProject() {
    return PROJECT_PATH;
  }

  @Binds
  @IntoSet
  Initializable bindSandboxHashProviderInitializer(SandboxHashProviderInitializer i);

  @Provides
  @PerCommand
  @Sandbox
  static Hash provideSandboxHash(SandboxHashProvider sandboxHashProvider) {
    return sandboxHashProvider.get();
  }
}
