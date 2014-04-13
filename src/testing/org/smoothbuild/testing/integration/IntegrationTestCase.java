package org.smoothbuild.testing.integration;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.cli.work.build.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.io.Constants.SMOOTH_DIR;
import static org.smoothbuild.io.cache.CacheModule.RESULTS_DIR;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public class IntegrationTestCase extends AbstractModule {
  public static final Path RESULTS_PATH = SMOOTH_DIR.append(RESULTS_DIR);

  protected FakeFileSystem fileSystem;
  protected FakeUserConsole userConsole;
  private BuildWorker buildWorker;

  @Before
  public void before() {
    Module module = Modules.override(new IntegrationTestModule()).with(this);
    Injector injector = createInjector(module);
    fileSystem = injector.getInstance(Key.get(FakeFileSystem.class, ProjectDir.class));
    userConsole = injector.getInstance(FakeUserConsole.class);
    buildWorker = injector.getInstance(BuildWorker.class);
  }

  public void script(String script) throws IOException {
    fileSystem.createFile(DEFAULT_SCRIPT, ScriptBuilder.script(script));
  }

  public void build(String... strings) {
    buildWorker.run(ImmutableList.copyOf(strings));
  }

  @Override
  protected void configure() {}
}
