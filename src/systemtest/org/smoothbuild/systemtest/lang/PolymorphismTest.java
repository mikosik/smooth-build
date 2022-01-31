package org.smoothbuild.systemtest.lang;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.nativefunc.Flatten;
import org.smoothbuild.systemtest.SystemTestCase;

public class PolymorphismTest extends SystemTestCase {
  @Test
  public void single_elem_array() throws Exception {
    createNativeJar(Flatten.class);
    createUserModule("""
            [E] testSingleElement(E elem) = [ elem ];
            result = testSingleElement("abc");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("abc"));
  }

  @Test
  public void flatten_1() throws Exception {
    createNativeJar(Flatten.class);
    createUserModule(format("""
            @Native("%s")
            [E] testFlatten([[E]] array);
            result = testFlatten(array = [ [ "aa" ], [ "bb", "cc" ] ]);
            """, Flatten.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("aa", "bb", "cc"));
  }

  @Test
  public void flatten_sample_2() throws Exception {
    createNativeJar(Flatten.class);
    createUserModule(format("""
            @Native("%s")
            [E] testFlatten([[E]] array);
            result = testFlatten(array = [ [ [ "aa" ], [ "bb", "cc" ] ] ]);
            """, Flatten.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list(list("aa"), list("bb", "cc")));
  }

  @Test
  public void pair_and_identity() throws Exception {
    createUserModule("""
            A testIdentity(A v) = v;
            [A] pair(A a1, A a2) = [ a1, a2 ];
            result = pair(a1 = testIdentity(v = "aa"), a2 = testIdentity(v = "bb"));
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("aa", "bb"));
  }
}
