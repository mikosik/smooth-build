package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.Random;

public class CachingTest extends AcceptanceTestCase {
  @Nested
  class _result_from_referencable_which_is_ {
    @ParameterizedTest
    @ValueSource(strings = {"", "()"})
    public void pure_is_cached_on_disk(String functionOrValue) throws Exception {
      createNativeJar(Random.class);
      createUserModule(format("""
            @Native("%s")
            String cachedRandom%s;
            result = cachedRandom%s;
            """, Random.class.getCanonicalName(), functionOrValue, functionOrValue));
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
    public void impure_is_cached_in_single_build(String functionOrValue) throws Exception {
      createNativeJar(Random.class);
      createUserModule(format("""
            @Native("%s", IMPURE)
            String cachedInMemoryRandom%s;
            resultA = cachedInMemoryRandom%s;
            resultB = cachedInMemoryRandom%s;
            """, Random.class.getCanonicalName(), functionOrValue, functionOrValue,
          functionOrValue));
      runSmoothBuild("resultA", "resultB");
      assertFinishedWithSuccess();

      assertThat(artifactFileContentAsString("resultA"))
          .isEqualTo(artifactFileContentAsString("resultB"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "()"})
    public void impure_is_not_cached_on_disk(String functionOrValue)
        throws Exception {
      createNativeJar(Random.class);
      createUserModule(format("""
            @Native("%s", IMPURE)
            String cachedInMemoryRandom%s;
            result = cachedInMemoryRandom%s;
            """, Random.class.getCanonicalName(), functionOrValue, functionOrValue));
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

  @ParameterizedTest
  @ValueSource(strings = {"", "()"})
  public void native_can_implement_two_things_each_with_different_pureness(
      String functionOrValue) throws Exception {
    createNativeJar(Random.class);
    createUserModule(format("""
            @Native("%s")
            String cachingRandom%s;
            @Native("%s", IMPURE)
            String notCachingRandom%s;
            caching = cachingRandom%s;
            notCaching = notCachingRandom%s;
            """, Random.class.getCanonicalName(), functionOrValue,
        Random.class.getCanonicalName(), functionOrValue, functionOrValue, functionOrValue));

    runSmoothBuild("caching", "notCaching");
    assertFinishedWithSuccess();
    String cachingA = artifactFileContentAsString("caching");
    String notCachingA = artifactFileContentAsString("notCaching");

    runSmoothBuild("caching", "notCaching");
    assertFinishedWithSuccess();
    String cachingB = artifactFileContentAsString("caching");
    String notCachingB = artifactFileContentAsString("notCaching");

    assertThat(cachingA).isEqualTo(cachingB);
    assertThat(cachingA).isNotEqualTo(notCachingA);
    assertThat(cachingA).isNotEqualTo(notCachingB);
    assertThat(notCachingA).isNotEqualTo(notCachingB);
  }
}
