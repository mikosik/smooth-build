package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.equalTo;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.Flatten;

public class GenericTest extends AcceptanceTestCase {
  @Test
  public void flatten_1() throws Exception {
    givenNativeJar(Flatten.class);
    givenScript("[e] testFlatten([[e]] array);                         \n"
        + "      result = testFlatten(array=[['aa'], ['bb', 'cc']]);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aa", "bb", "cc")));
  }

  @Test
  public void flatten_sample_2() throws Exception {
    givenNativeJar(Flatten.class);
    givenScript("[e] testFlatten([[e]] array);                           \n"
        + "      result = testFlatten(array=[[['aa'], ['bb', 'cc']]]);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list(list("aa"), list("bb", "cc"))));
  }

  @Test
  public void pair_and_identity() throws Exception {
    givenScript("a testIdentity(a v) = v;                                           \n"
        + "      [a] pair(a e1, a e2) = [e1, e2];                                   \n"
        + "      result = pair(e1=testIdentity(v='aa'), e2=testIdentity(v='bb'));   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aa", "bb")));
  }
}
