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
  class _result_from_evaluable_which_is_ {
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
  public void native_evaluables_with_same_pure_native_share_cache_results(
      String functionOrValue) throws Exception {
    createNativeJar(Random.class);
    createUserModule(format("""
            @Native("%s", PURE)
            String first%s;
            @Native("%s", PURE)
            String second%s;
            random1 = first%s;
            random2 = second%s;
            """, Random.class.getCanonicalName(), functionOrValue,
        Random.class.getCanonicalName(), functionOrValue, functionOrValue, functionOrValue));

    runSmoothBuild("random1", "random2");
    assertFinishedWithSuccess();
    String random1 = artifactFileContentAsString("random1");
    String random2 = artifactFileContentAsString("random2");

    assertThat(random1).isEqualTo(random2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "()"})
  public void native_evaluables_with_same_impure_native_share_cache_results(
      String functionOrValue) throws Exception {
    createNativeJar(Random.class);
    createUserModule(format("""
            @Native("%s", IMPURE)
            String first%s;
            @Native("%s", IMPURE)
            String second%s;
            random1 = first%s;
            random2 = second%s;
            """, Random.class.getCanonicalName(), functionOrValue,
        Random.class.getCanonicalName(), functionOrValue, functionOrValue, functionOrValue));

    runSmoothBuild("random1", "random2");
    assertFinishedWithSuccess();
    String random1 = artifactFileContentAsString("random1");
    String random2 = artifactFileContentAsString("random2");

    assertThat(random1).isEqualTo(random2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "()"})
  public void native_evaluables_with_same_native_but_different_pureness_dont_share_cache_results(
      String functionOrValue) throws Exception {
    createNativeJar(Random.class);
    createUserModule(format("""
            @Native("%s", IMPURE)
            String first%s;
            @Native("%s", PURE)
            String second%s;
            random1 = first%s;
            random2 = second%s;
            """, Random.class.getCanonicalName(), functionOrValue,
        Random.class.getCanonicalName(), functionOrValue, functionOrValue, functionOrValue));

    runSmoothBuild("random1", "random2");
    assertFinishedWithSuccess();
    String random1 = artifactFileContentAsString("random1");
    String random2 = artifactFileContentAsString("random2");

    assertThat(random1).isNotEqualTo(random2);
  }
}
