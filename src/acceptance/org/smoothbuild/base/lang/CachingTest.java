package org.smoothbuild.base.lang;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.artifactPath;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.util.Random;

import javax.inject.Inject;

import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class CachingTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Test
  public void second_call_to_a_function_uses_cached_result() throws Exception {
    createInjector(new AcceptanceTestModule(CacheableRandom.class)).injectMembers(this);
    String script = "result1 : random(); " + "result2 : random(); ";
    script(fileSystem, script);

    buildWorker.run(asList("result1", "result2"));

    userConsole.messages().assertNoProblems();

    String result1 = inputStreamToString(fileSystem.openInputStream(artifactPath("result1")));
    String result2 = inputStreamToString(fileSystem.openInputStream(artifactPath("result2")));
    assertEquals(result1, result2);
  }

  public static class CacheableRandom {
    @SmoothFunction
    public static SString random(NativeApi nativeApi) {
      long randomLong = new Random().nextLong();
      return nativeApi.string(Long.toString(randomLong));
    }
  }

  @Test
  public void second_call_to_not_cached_function_invokes_function() throws Exception {
    createInjector(new AcceptanceTestModule(NotCacheableRandom.class)).injectMembers(this);
    String script = "result1 : random(); " + "result2 : random(); ";
    script(fileSystem, script);

    buildWorker.run(asList("result1", "result2"));

    userConsole.messages().assertNoProblems();

    String result1 = inputStreamToString(fileSystem.openInputStream(artifactPath("result1")));
    String result2 = inputStreamToString(fileSystem.openInputStream(artifactPath("result2")));
    assertNotEquals(result1, result2);
  }

  public static class NotCacheableRandom {
    @SmoothFunction
    @NotCacheable
    public static SString random(NativeApi nativeApi) {
      long randomLong = new Random().nextLong();
      return nativeApi.string(Long.toString(randomLong));
    }
  }
}
