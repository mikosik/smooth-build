package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.nativ.CacheableRandom;
import org.smoothbuild.acceptance.lang.nativ.NotCacheableRandom;

public class CachingTest extends AcceptanceTestCase {
  @Test
  public void result_from_cacheable_function_is_cached() throws Exception {
    givenNativeJar(CacheableRandom.class);
    givenScript("String cacheableRandom();\n"
        + "      result = cacheableRandom;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    String resultFromFirstCall = artifactContent("result");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo(resultFromFirstCall));
  }

  @Test
  public void result_from_not_cacheable_function_is_not_cached() throws Exception {
    givenNativeJar(NotCacheableRandom.class);
    givenScript("String notCacheableRandom();\n"
        + "      result = notCacheableRandom;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    String resultFromFirstCall = artifactContent("result");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), not(equalTo(resultFromFirstCall)));
  }
}
