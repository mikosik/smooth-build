package org.smoothbuild.vm;

import java.io.IOException;

import javax.inject.Singleton;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.install.InstallationHashes;
import org.smoothbuild.vm.report.TaskMatcher;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class VmModule extends AbstractModule {
  private final TaskMatcher taskMatcher;

  public VmModule(TaskMatcher taskMatcher) {
    this.taskMatcher = taskMatcher;
  }

  @Override
  protected void configure() {
    bind(TaskMatcher.class).toInstance(taskMatcher);
    bind(VmFactory.class).to(VmFactoryImpl.class);
  }

  @Provides
  @Singleton
  @SandboxHash
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }
}
