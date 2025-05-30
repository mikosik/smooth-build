package org.smoothbuild.virtualmachine.dagger;

import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;

import dagger.Module;
import dagger.Provides;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;

@Module(includes = VmModule.class)
public interface VmTestModule {
  static Alias PROJECT = new Alias("t-project");

  @Provides
  static Set<Alias> provideAliases() {
    return set(PROJECT);
  }

  @Provides
  @BytecodeDb
  static FullPath bytecodeDbPath(@Project FullPath projectPath) {
    return projectPath.append(path(".smooth/bytecode"));
  }

  @Provides
  @ComputationDb
  static FullPath computationDbPath(@Project FullPath projectPath) {
    return projectPath.append(".smooth/computations");
  }

  @Provides
  @Project
  static FullPath projectPath() {
    return fullPath(PROJECT, Path.root());
  }

  @Provides
  @PerCommand
  @Sandbox
  static Hash provideSandboxHash() {
    return Hash.of(33);
  }
}
