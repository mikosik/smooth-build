package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CachingTest extends AcceptanceTestCase {
  @Test
  public void second_call_to_a_function_uses_cached_result() throws Exception {
    givenScript("result1: cacheableRandom(); result2 : cacheableRandom();");
    whenSmoothBuild("result1");
    whenSmoothBuild("result2");
    thenFinishedWithSuccess();
    then(artifact("result1"), hasContent(artifactContent("result2")));
  }

  @Test
  public void second_call_to_not_cached_function_invokes_function() throws Exception {
    givenScript("result1: notCacheableRandom(); result2 : notCacheableRandom();");
    whenSmoothBuild("result1");
    whenSmoothBuild("result2");
    thenFinishedWithSuccess();
    then(artifact("result1"), not(hasContent(artifactContent("result2"))));
  }
}
