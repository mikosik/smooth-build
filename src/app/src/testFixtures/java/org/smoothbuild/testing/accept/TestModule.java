package org.smoothbuild.testing.accept;

import static com.google.common.io.ByteStreams.nullOutputStream;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;

import java.io.PrintWriter;

import org.smoothbuild.out.report.Console;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.eval.report.TaskMatcher;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.evaluate.SandboxHash;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class TestModule extends AbstractModule {
  private final MemoryReporter memoryReporter;

  public TestModule(MemoryReporter memoryReporter) {
    this.memoryReporter = memoryReporter;
  }

  @Override
  protected void configure() {
    bind(MemoryReporter.class).toInstance(memoryReporter);
    bind(Reporter.class).to(MemoryReporter.class);
    bind(Console.class).toInstance(new Console(new PrintWriter(nullOutputStream(), true)));
    bind(TaskMatcher.class).toInstance(ALL);
  }

  @Provides
  @Singleton
  @SandboxHash
  public Hash provideSandboxHash() {
    return Hash.of(33);
  }
}
