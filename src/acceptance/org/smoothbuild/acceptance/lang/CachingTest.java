package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.CacheableRandom;
import org.smoothbuild.acceptance.testing.NotCacheableRandom;

public class CachingTest extends AcceptanceTestCase {
  @Test
  public void result_from_cacheable_function_is_cached() throws Exception {
    givenNativeJar(CacheableRandom.class);
    givenScript(
        "  String cacheableRandom();  ",
        "  result = cacheableRandom;  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    String resultFromFirstCall = artifactContent("result");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo(resultFromFirstCall);
  }

  @Test
  public void result_from_not_cacheable_function_is_not_cached() throws Exception {
    givenNativeJar(NotCacheableRandom.class);
    givenScript(
        "  String notCacheableRandom();  ",
        "  result = notCacheableRandom;  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    String resultFromFirstCall = artifactContent("result");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isNotEqualTo(resultFromFirstCall);
  }
}
