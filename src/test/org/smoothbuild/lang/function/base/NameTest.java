package org.smoothbuild.lang.function.base;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;
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

    doTestSimple("a.b");
    doTestSimple("a.b.c");
    doTestSimple("a.b.c.d");

    doTestSimple("a.");
    doTestSimple("a.b.");
    doTestSimple("a.b.c.");

    doTestSimple("a..b");
    doTestSimple("a...b");

    doTestSimple("_._._");
    doTestSimple("a_._b._c_");
  }

  private static void doTestSimple(String name) {
    assertThat(isLegalName(name)).isTrue();
    assertThat(name(name).value()).isEqualTo(name);
  }

  @Test
  public void illegalSimpleNames() {

    assertIllegalSimpleName("");
    assertIllegalSimpleName(".");
    assertIllegalSimpleName("..");
    assertIllegalSimpleName("...");

    assertIllegalSimpleName("@");
    assertIllegalSimpleName("#");

    assertIllegalSimpleName(".a");
    assertIllegalSimpleName(".a.b");
    assertIllegalSimpleName(".a.b.c");

    assertIllegalSimpleName("my-function");
    assertIllegalSimpleName("a-package.myFunction");
  }

  private static void assertIllegalSimpleName(String name) {
    assertThat(isLegalName(name)).isFalse();
    try {
      name(name);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testToString() throws Exception {
    assertThat(name("abc").toString()).isEqualTo("'abc'");
  }

  @Test
  public void testEquals() throws Exception {
    EqualsVerifier.forExamples(name("a"), name("b"), name("c")).suppress(NULL_FIELDS).verify();
  }
}
