package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.accept.AcceptanceTestCase;
import org.smoothbuild.virtualmachine.testing.func.nativ.Random;

public class CachingTest extends AcceptanceTestCase {
  @Nested
  class _result_from_eval_which_is_ {
    @Test
    public void pure_func_result_is_cached_on_disk() throws Exception {
      createUserNativeJar(Random.class);
      createUserModule(format(
          """
            @Native("%s")
            String cachedRandom();
            result = cachedRandom();
            """,
          Random.class.getCanonicalName()));
      evaluate("result");
      var resultFromFirstRun = artifact();
      restartSmoothWithSameFileSystems();
      evaluate("result");
      var resultFromSecondRun = artifact();

      assertThat(resultFromSecondRun).isEqualTo(resultFromFirstRun);
    }

    @Test
    public void impure_func_result_is_cached_in_single_build() throws Exception {
      createUserNativeJar(Random.class);
      createUserModule(format(
          """
            @NativeImpure("%s")
            String cachedInMemoryRandom();
            resultA = cachedInMemoryRandom();
            resultB = cachedInMemoryRandom();
            """,
          Random.class.getCanonicalName()));
      evaluate("resultA", "resultB");
      assertThat(artifact(0)).isEqualTo(artifact(1));
    }

    @Test
    public void impure_func_result_is_not_cached_on_disk() throws Exception {
      createUserNativeJar(Random.class);
      createUserModule(format(
          """
            @NativeImpure("%s")
            String cachedInMemoryRandom();
            result = cachedInMemoryRandom();
            """,
          Random.class.getCanonicalName()));
      evaluate("result");
      var resultFromFirstRun = artifact();
      restartSmoothWithSameFileSystems();
      evaluate("result");
      var resultFromSecondRun = artifact();

      assertThat(resultFromSecondRun).isNotEqualTo(resultFromFirstRun);
    }
  }

  @Test
  public void native_func_with_same_pure_native_share_cache_results() throws Exception {
    createUserNativeJar(Random.class);
    createUserModule(format(
        """
            @Native("%s")
            String first();
            @Native("%s")
            String second();
            random1 = first();
            random2 = second();
            """,
        Random.class.getCanonicalName(), Random.class.getCanonicalName()));
    evaluate("random1", "random2");
    var random1 = artifact(0);
    var random2 = artifact(1);

    assertThat(random1).isEqualTo(random2);
  }

  @Test
  public void native_func_with_same_impure_native_share_cache_results() throws Exception {
    createUserNativeJar(Random.class);
    createUserModule(format(
        """
            @NativeImpure("%s")
            String first();
            @NativeImpure("%s")
            String second();
            random1 = first();
            random2 = second();
            """,
        Random.class.getCanonicalName(), Random.class.getCanonicalName()));
    evaluate("random1", "random2");
    var random1 = artifact(0);
    var random2 = artifact(1);

    assertThat(random1).isEqualTo(random2);
  }

  @Test
  public void native_func_with_same_native_but_different_pureness_dont_share_cache_results()
      throws Exception {
    createUserNativeJar(Random.class);
    createUserModule(format(
        """
            @NativeImpure("%s")
            String first();
            @Native("%s")
            String second();
            random1 = first();
            random2 = second();
            """,
        Random.class.getCanonicalName(), Random.class.getCanonicalName()));

    evaluate("random1", "random2");
    var random1 = artifact(0);
    var random2 = artifact(1);

    assertThat(random1).isNotEqualTo(random2);
  }
}
