package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.Flatten;

public class GenericTest extends AcceptanceTestCase {
  @Test
  public void flatten_1() throws Exception {
    createNativeJar(Flatten.class);
    createUserModule(
        "  [E] testFlatten([[E]] array);                                ",
        "  result = testFlatten(array = [ [ 'aa' ], [ 'bb', 'cc' ] ]);  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("aa", "bb", "cc"));
  }

  @Test
  public void flatten_sample_2() throws Exception {
    createNativeJar(Flatten.class);
    createUserModule(
        "  [E] testFlatten([[E]] array);                                    ",
        "  result = testFlatten(array = [ [ [ 'aa' ], [ 'bb', 'cc' ] ] ]);  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list(list("aa"), list("bb", "cc")));
  }

  @Test
  public void pair_and_identity() throws Exception {
    createUserModule(
        "  A testIdentity(A v) = v;                                                ",
        "  [A] pair(A a1, A a2) = [ a1, a2 ];                                      ",
        "  result = pair(a1 = testIdentity(v = 'aa'), a2 = testIdentity(v = 'bb'));  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("aa", "bb"));
  }
}
