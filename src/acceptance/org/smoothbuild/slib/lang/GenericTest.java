package org.smoothbuild.slib.lang;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;
import org.smoothbuild.slib.testing.Flatten;

public class GenericTest extends AcceptanceTestCase {
  @Test
  public void flatten_1() throws Exception {
    givenNativeJar(Flatten.class);
    givenScript(
        "  [E] testFlatten([[E]] array);                                ",
        "  result = testFlatten(array = [ [ 'aa' ], [ 'bb', 'cc' ] ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("aa", "bb", "cc"));
  }

  @Test
  public void flatten_sample_2() throws Exception {
    givenNativeJar(Flatten.class);
    givenScript(
        "  [E] testFlatten([[E]] array);                                    ",
        "  result = testFlatten(array = [ [ [ 'aa' ], [ 'bb', 'cc' ] ] ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list(list("aa"), list("bb", "cc")));
  }

  @Test
  public void pair_and_identity() throws Exception {
    givenScript(
        "  A testIdentity(A v) = v;                                                ",
        "  [A] pair(A a1, A a2) = [ a1, a2 ];                                      ",
        "  result = pair(a1=testIdentity(v = 'aa'), a2 = testIdentity(v = 'bb'));  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("aa", "bb"));
  }
}
