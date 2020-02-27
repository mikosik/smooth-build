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
    givenScript(
        "  [E] testFlatten([[E]] array);                                ",
        "  result = testFlatten(array = [ [ 'aa' ], [ 'bb', 'cc' ] ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aa", "bb", "cc")));
  }

  @Test
  public void flatten_sample_2() throws Exception {
    givenNativeJar(Flatten.class);
    givenScript(
        "  [E] testFlatten([[E]] array);                                    ",
        "  result = testFlatten(array = [ [ [ 'aa' ], [ 'bb', 'cc' ] ] ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list(list("aa"), list("bb", "cc"))));
  }

  @Test
  public void pair_and_identity() throws Exception {
    givenScript(
        "  A testIdentity(A v) = v;                                                ",
        "  [A] pair(A a1, A a2) = [ a1, a2 ];                                      ",
        "  result = pair(a1=testIdentity(v = 'aa'), a2 = testIdentity(v = 'bb'));  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("aa", "bb")));
  }
}
