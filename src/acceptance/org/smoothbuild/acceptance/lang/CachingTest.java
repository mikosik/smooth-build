package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CachingTest extends AcceptanceTestCase {
  @Test
  public void second_call_to_a_function_uses_cached_result() throws Exception {
    givenBuildScript(script("result1: cacheableRandom(); result2 : cacheableRandom();"));
    whenRunSmoothBuild("result1");
    whenRunSmoothBuild("result2");
    thenReturnedCode(0);
    thenArtifact("result1", hasContent(artifactContent("result2")));
  }

  @Test
  public void second_call_to_not_cached_function_invokes_function() throws Exception {
    givenBuildScript(script("result1: notCacheableRandom(); result2 : notCacheableRandom();"));
    whenRunSmoothBuild("result1");
    whenRunSmoothBuild("result2");
    thenReturnedCode(0);
    thenArtifact("result1", not(hasContent(artifactContent("result2"))));
  }
}
