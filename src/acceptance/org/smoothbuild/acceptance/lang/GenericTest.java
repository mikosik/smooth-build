package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.nativ.Flatten;

public class GenericTest extends AcceptanceTestCase {
  @Test
  public void flatten_1() throws Exception {
    givenNativeJar(Flatten.class);
    givenScript("[e] testFlatten([[e]] array);                         \n"
        + "      result = testFlatten(array=[['aa'], ['bb', 'cc']]);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("aa", "bb", "cc"));
  }

  @Test
  public void flatten_sample_2() throws Exception {
    givenNativeJar(Flatten.class);
    givenScript("[e] testFlatten([[e]] array);                           \n"
        + "      result = testFlatten(array=[[['aa'], ['bb', 'cc']]]);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(
        new Object[] {
            new Object[] { "aa" },
            new Object[] { "bb", "cc" }
        }));
  }
}
