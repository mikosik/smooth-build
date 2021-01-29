package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.CacheableRandom;
import org.smoothbuild.acceptance.testing.CachingMemoryRandom;
import org.smoothbuild.acceptance.testing.CachingNoneRandom;

public class CachingTest extends AcceptanceTestCase {
  @Nested
  class _result_from_referencable_with_ {
    @ParameterizedTest
    @ValueSource(strings = {"", "()"})
    public void disk_level_caching_is_cached_on_disk(String functionOrValue) throws Exception {
      createNativeJar(CacheableRandom.class);
      createUserModule(format("""
            @Native("%s.function")
            String cacheableRandom%s;
            result = cacheableRandom%s;
            """, CacheableRandom.class.getCanonicalName(), functionOrValue, functionOrValue));
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
    public void memory_level_caching_is_cached(String functionOrValue) throws Exception {
      createNativeJar(CachingMemoryRandom.class);
      createUserModule(format("""
            @Native("%s.function")
            String cachingMemoryRandom%s;
            resultA = cachingMemoryRandom%s;
            resultB = cachingMemoryRandom%s;
            """, CachingMemoryRandom.class.getCanonicalName(), functionOrValue, functionOrValue,
          functionOrValue));
      runSmoothBuild("resultA", "resultB");
      assertFinishedWithSuccess();

      assertThat(artifactFileContentAsString("resultA"))
          .isEqualTo(artifactFileContentAsString("resultB"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "()"})
    public void memory_level_caching_is_not_cached_on_disk(String functionOrValue)
        throws Exception {
      createNativeJar(CachingMemoryRandom.class);
      createUserModule(format("""
            @Native("%s.function")
            String cachingMemoryRandom%s;
            result = cachingMemoryRandom%s;
            """, CachingMemoryRandom.class.getCanonicalName(), functionOrValue, functionOrValue));
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      String resultFromFirstRun = artifactFileContentAsString("result");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      String resultFromSecondRun = artifactFileContentAsString("result");

      assertThat(resultFromSecondRun)
          .isNotEqualTo(resultFromFirstRun);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "()"})
    public void none_level_caching_is_not_cached_in_memory(String functionOrValue)
        throws Exception {
      createNativeJar(CachingNoneRandom.class);
      createUserModule(format("""
            @Native("%s.function")
            String cachingNoneRandom%s;
            resultA = cachingNoneRandom%s;
            resultB = cachingNoneRandom%s;
            """, CachingNoneRandom.class.getCanonicalName(), functionOrValue, functionOrValue,
          functionOrValue));
      runSmoothBuild("resultA", "resultB");
      assertFinishedWithSuccess();

      assertThat(artifactFileContentAsString("resultA"))
          .isNotEqualTo(artifactFileContentAsString("resultB"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "()"})
    public void none_level_caching_is_not_cached_on_disk(String functionOrValue) throws Exception {
      createNativeJar(CachingNoneRandom.class);
      createUserModule(format("""
            @Native("%s.function")
            String cachingNoneRandom%s;
            result = cachingNoneRandom%s;
            """, CachingNoneRandom.class.getCanonicalName(), functionOrValue, functionOrValue));
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
}
