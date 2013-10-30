package org.smoothbuild.function.base;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Name.isLegalName;
import static org.smoothbuild.function.base.Name.simpleName;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

public class NameTest {

  @Test
  public void legalSimpleNames() {
    doTestSimple("a");
    doTestSimple("ab");
    doTestSimple("abc");
    doTestSimple("abcd");

    doTestSimple("my_function");
    doTestSimple("MY_FUNCTION");
    doTestSimple("myFunction");
  }

  private static void doTestSimple(String name) {
    assertThat(isLegalName(name)).isTrue();
    assertThat(simpleName(name).value()).isEqualTo(name);
  }

  @Test
  public void illegalSimpleNames() {
    assertIllegalSimpleName("a.b");
    assertIllegalSimpleName("a.b.c");
    assertIllegalSimpleName("a.b.c.d");

    assertIllegalSimpleName("my.package.FuncA");
    assertIllegalSimpleName("MY.PACKAGE.FUNC_A");

    assertIllegalSimpleName("a_package.my_function");
    assertIllegalSimpleName("_._._");
    assertIllegalSimpleName("a_._b._c_");

    assertIllegalSimpleName("");
    assertIllegalSimpleName(".");
    assertIllegalSimpleName("..");
    assertIllegalSimpleName("...");

    assertIllegalSimpleName("@");
    assertIllegalSimpleName("#");

    assertIllegalSimpleName(".a");
    assertIllegalSimpleName(".a.b");
    assertIllegalSimpleName(".a.b.c");

    assertIllegalSimpleName("a.");
    assertIllegalSimpleName("a.b.");
    assertIllegalSimpleName("a.b.c.");

    assertIllegalSimpleName("a..b");

    assertIllegalSimpleName("my-function");
    assertIllegalSimpleName("a-package.myFunction");
  }

  private static void assertIllegalSimpleName(String name) {
    assertThat(isLegalName(name)).isFalse();
    try {
      simpleName(name);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testToString() throws Exception {
    assertThat(simpleName("abc").toString()).isEqualTo("'abc'");
  }

  @Test
  public void testEquals() throws Exception {
    EqualsVerifier.forExamples(simpleName("a"), simpleName("b"), simpleName("c")).suppress(NULL_FIELDS).verify();
  }
}
