package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.CacheableRandom;
import org.smoothbuild.acceptance.testing.NotCacheableRandom;

public class CachingTest extends AcceptanceTestCase {
  @ParameterizedTest
  @ValueSource(strings = {"", "()"})
  public void result_from_cacheable_function_is_cached(String functionOrValue) throws Exception {
    createNativeJar(CacheableRandom.class);
    createUserModule("""
            String cacheableRandom__FUNCTION_OR_VALUE__;
            result = cacheableRandom__FUNCTION_OR_VALUE__;
            """.replaceAll("__FUNCTION_OR_VALUE__", functionOrValue));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String resultFromFirstRun = artifactFileContentAsString("result");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String resultFromSecondRun = artifactFileContentAsString("result");

    assertThat(resultFromSecondRun)
        .isEqualTo(resultFromFirstRun);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "()"})
  public void result_from_not_cacheable_function_is_cached_within_single_run(String functionOrValue)
      throws Exception {
    createNativeJar(NotCacheableRandom.class);
    createUserModule("""
            String notCacheableRandom__FUNCTION_OR_VALUE__;
            resultA = notCacheableRandom__FUNCTION_OR_VALUE__;
            resultB = notCacheableRandom__FUNCTION_OR_VALUE__;
            """.replaceAll("__FUNCTION_OR_VALUE__", functionOrValue));
    runSmoothBuild("resultA", "resultB");
    assertFinishedWithSuccess();

    assertThat(artifactFileContentAsString("resultA"))
        .isEqualTo(artifactFileContentAsString("resultB"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "()"})
  public void result_from_not_cacheable_function_is_not_cached_between_runs(String functionOrValue)
      throws Exception {
    createNativeJar(NotCacheableRandom.class);
    createUserModule("""
            String notCacheableRandom__FUNCTION_OR_VALUE__;
            result = notCacheableRandom__FUNCTION_OR_VALUE__;
            """.replaceAll("__FUNCTION_OR_VALUE__", functionOrValue));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String resultFromFirstRun = artifactFileContentAsString("result");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String resultFromSecondRun = artifactFileContentAsString("result");

    assertThat(resultFromSecondRun)
        .isNotEqualTo(resultFromFirstRun);
  }
}
