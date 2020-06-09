package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.CacheableRandom;
import org.smoothbuild.acceptance.testing.NotCacheableRandom;

public class CachingTest extends AcceptanceTestCase {
  @Test
  public void result_from_cacheable_function_is_cached() throws Exception {
    createNativeJar(CacheableRandom.class);
    createUserModule(
        "  String cacheableRandom();  ",
        "  result = cacheableRandom;  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String resultFromFirstRun = artifactFileContent("result");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String resultFromSecondRun = artifactFileContent("result");

    assertThat(resultFromSecondRun)
        .isEqualTo(resultFromFirstRun);
  }

  @Test
  public void result_from_not_cacheable_function_is_cached_within_single_run() throws Exception {
    createNativeJar(NotCacheableRandom.class);
    createUserModule(
        "  String notCacheableRandom();   ",
        "  resultA = notCacheableRandom;  ",
        "  resultB = notCacheableRandom;  ");
    runSmoothBuild("resultA", "resultB");
    assertFinishedWithSuccess();

    assertThat(artifactFileContent("resultA"))
        .isEqualTo(artifactFileContent("resultB"));
  }

  @Test
  public void result_from_not_cacheable_function_is_not_cached_between_runs() throws Exception {
    createNativeJar(NotCacheableRandom.class);
    createUserModule(
        "  String notCacheableRandom();  ",
        "  result = notCacheableRandom;  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String resultFromFirstRun = artifactFileContent("result");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String resultFromSecondRun = artifactFileContent("result");

    assertThat(resultFromSecondRun)
        .isNotEqualTo(resultFromFirstRun);
  }
}
