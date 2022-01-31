package org.smoothbuild.systemtest.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.nativefunc.Random;
import org.smoothbuild.systemtest.SystemTestCase;

public class CachingTest extends SystemTestCase {
  @Nested
  class _result_from_eval_which_is_ {
    @Test
    public void pure_func_result_is_cached_on_disk() throws Exception {
      createNativeJar(Random.class);
      createUserModule(format("""
            @Native("%s")
            String cachedRandom();
            result = cachedRandom();
            """, Random.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      String resultFromFirstRun = artifactAsString("result");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      String resultFromSecondRun = artifactAsString("result");

      assertThat(resultFromSecondRun)
          .isEqualTo(resultFromFirstRun);
    }

    @Test
    public void impure_func_result_is_cached_in_single_build() throws Exception {
      createNativeJar(Random.class);
      createUserModule(format("""
            @Native("%s", IMPURE)
            String cachedInMemoryRandom();
            resultA = cachedInMemoryRandom();
            resultB = cachedInMemoryRandom();
            """, Random.class.getCanonicalName()));
      runSmoothBuild("resultA", "resultB");
      assertFinishedWithSuccess();

      assertThat(artifactAsString("resultA"))
          .isEqualTo(artifactAsString("resultB"));
    }

    @Test
    public void impure_func_result_is_not_cached_on_disk() throws Exception {
      createNativeJar(Random.class);
      createUserModule(format("""
            @Native("%s", IMPURE)
            String cachedInMemoryRandom();
            result = cachedInMemoryRandom();
            """, Random.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      String resultFromFirstRun = artifactAsString("result");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      String resultFromSecondRun = artifactAsString("result");

      assertThat(resultFromSecondRun)
          .isNotEqualTo(resultFromFirstRun);
    }
  }

  @Test
  public void native_func_with_same_pure_native_share_cache_results() throws Exception {
    createNativeJar(Random.class);
    createUserModule(format("""
            @Native("%s", PURE)
            String first();
            @Native("%s", PURE)
            String second();
            random1 = first();
            random2 = second();
            """, Random.class.getCanonicalName(),
        Random.class.getCanonicalName()));

    runSmoothBuild("random1", "random2");
    assertFinishedWithSuccess();
    String random1 = artifactAsString("random1");
    String random2 = artifactAsString("random2");

    assertThat(random1).isEqualTo(random2);
  }

  @Test
  public void native_func_with_same_impure_native_share_cache_results() throws Exception {
    createNativeJar(Random.class);
    createUserModule(format("""
            @Native("%s", IMPURE)
            String first();
            @Native("%s", IMPURE)
            String second();
            random1 = first();
            random2 = second();
            """, Random.class.getCanonicalName(), Random.class.getCanonicalName()));

    runSmoothBuild("random1", "random2");
    assertFinishedWithSuccess();
    String random1 = artifactAsString("random1");
    String random2 = artifactAsString("random2");

    assertThat(random1).isEqualTo(random2);
  }

  @Test
  public void native_func_with_same_native_but_different_pureness_dont_share_cache_results()
      throws Exception {
    createNativeJar(Random.class);
    createUserModule(format("""
            @Native("%s", IMPURE)
            String first();
            @Native("%s", PURE)
            String second();
            random1 = first();
            random2 = second();
            """, Random.class.getCanonicalName(), Random.class.getCanonicalName()));

    runSmoothBuild("random1", "random2");
    assertFinishedWithSuccess();
    String random1 = artifactAsString("random1");
    String random2 = artifactAsString("random2");

    assertThat(random1).isNotEqualTo(random2);
  }
}
