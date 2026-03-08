package org.smoothbuild.cli.command.build;

import dagger.Module;
import dagger.Provides;
import org.smoothbuild.cli.command.base.BaseModule;
import org.smoothbuild.virtualmachine.VmConfig;
import org.smoothbuild.virtualmachine.dagger.VmModule;

@Module(includes = {BaseModule.class, VmModule.class})
public interface BuildCommandModule {
  @Provides
  static VmConfig provideVmConfig() {
    return new VmConfig(128);
  }
}
