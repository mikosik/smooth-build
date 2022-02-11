package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.accept.AcceptanceTestCase;
import org.smoothbuild.testing.nativefunc.Flatten;

public class PolymorphismTest extends AcceptanceTestCase {
  @Test
  public void single_elem_array() throws Exception {
    createUserNativeJar(Flatten.class);
    createUserModule("""
            [E] testSingleElement(E elem) = [elem ];
            result = testSingleElement("abc");
            """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("abc")));
  }

  @Test
  public void flatten_1() throws Exception {
    createUserNativeJar(Flatten.class);
    createUserModule(format("""
            @Native("%s")
            [E] testFlatten([[E]] array);
            result = testFlatten(array = [["aa" ], ["bb", "cc" ] ]);
            """, Flatten.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("aa"), stringB("bb"), stringB("cc")));
  }

  @Test
  public void flatten_sample_2() throws Exception {
    createUserNativeJar(Flatten.class);
    createUserModule(format("""
            @Native("%s")
            [E] testFlatten([[E]] array);
            result = testFlatten(array = [[["aa"], ["bb", "cc" ] ] ]);
            """, Flatten.class.getCanonicalName()));
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(arrayB(stringB("aa")), arrayB(stringB("bb"), stringB("cc"))));
  }

  @Test
  public void pair_and_identity() throws Exception {
    createUserModule("""
            A testIdentity(A v) = v;
            [A] pair(A a1, A a2) = [a1, a2 ];
            result = pair(a1 = testIdentity(v = "aa"), a2 = testIdentity(v = "bb"));
            """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(stringB("aa"), stringB("bb")));
  }
}
