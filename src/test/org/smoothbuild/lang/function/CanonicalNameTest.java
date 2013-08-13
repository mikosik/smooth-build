package org.smoothbuild.lang.function;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.CanonicalName.canonicalName;
import static org.smoothbuild.lang.function.CanonicalName.isValidSimpleName;
import static org.smoothbuild.lang.function.CanonicalName.simpleName;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

public class CanonicalNameTest {

  @Test
  public void correctCanonicalNames() {
    doTestCanonicalName("a", "", "a");
    doTestCanonicalName("ab", "", "ab");
    doTestCanonicalName("abc", "", "abc");
    doTestCanonicalName("abcd", "", "abcd");

    doTestCanonicalName("a.b", "a", "b");
    doTestCanonicalName("a.b.c", "a.b", "c");
    doTestCanonicalName("a.b.c.d", "a.b.c", "d");

    doTestCanonicalName("my.package.FuncA", "my.package", "FuncA");
    doTestCanonicalName("MY.PACKAGE.FUNC_A", "MY.PACKAGE", "FUNC_A");

    doTestCanonicalName("my_function", "", "my_function");
    doTestCanonicalName("a_package.my_function", "a_package", "my_function");
    doTestCanonicalName("_._._", "_._", "_");
    doTestCanonicalName("a_._b._c_", "a_._b", "_c_");
  }

  private static void doTestCanonicalName(String full, String aPackage, String name) {
    CanonicalName canonicalName = canonicalName(full);
    assertThat(canonicalName.full()).isEqualTo(full);
    assertThat(canonicalName.aPackage()).isEqualTo(aPackage);
    assertThat(canonicalName.name()).isEqualTo(name);
  }

  @Test
  public void incorrectCanonicalNames() {
    assertIncorrectCanonicalName("");
    assertIncorrectCanonicalName(".");
    assertIncorrectCanonicalName("..");
    assertIncorrectCanonicalName("...");

    assertIncorrectCanonicalName("@");
    assertIncorrectCanonicalName("#");

    assertIncorrectCanonicalName(".a");
    assertIncorrectCanonicalName(".a.b");
    assertIncorrectCanonicalName(".a.b.c");

    assertIncorrectCanonicalName("a.");
    assertIncorrectCanonicalName("a.b.");
    assertIncorrectCanonicalName("a.b.c.");

    assertIncorrectCanonicalName("a..b");

    assertIncorrectCanonicalName("my-function");
    assertIncorrectCanonicalName("a-package.myFunction");
  }

  private static void assertIncorrectCanonicalName(String fullName) {
    try {
      canonicalName(fullName);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void correctSimpleNames() {
    doTestSimple("a");
    doTestSimple("ab");
    doTestSimple("abc");
    doTestSimple("abcd");

    doTestSimple("my_function");
    doTestSimple("MY_FUNCTION");
    doTestSimple("myFunction");
  }

  private static void doTestSimple(String name) {
    assertThat(isValidSimpleName(name)).isTrue();

    CanonicalName canonicalName = CanonicalName.simpleName(name);
    assertThat(canonicalName.full()).isEqualTo(name);
    assertThat(canonicalName.aPackage()).isEqualTo("");
    assertThat(canonicalName.name()).isEqualTo(name);
  }

  @Test
  public void incorrectSimpleNames() {
    assertIncorrectSimpleName("a.b");
    assertIncorrectSimpleName("a.b.c");
    assertIncorrectSimpleName("a.b.c.d");

    assertIncorrectSimpleName("my.package.FuncA");
    assertIncorrectSimpleName("MY.PACKAGE.FUNC_A");

    assertIncorrectSimpleName("a_package.my_function");
    assertIncorrectSimpleName("_._._");
    assertIncorrectSimpleName("a_._b._c_");

    assertIncorrectSimpleName("");
    assertIncorrectSimpleName(".");
    assertIncorrectSimpleName("..");
    assertIncorrectSimpleName("...");

    assertIncorrectSimpleName("@");
    assertIncorrectSimpleName("#");

    assertIncorrectSimpleName(".a");
    assertIncorrectSimpleName(".a.b");
    assertIncorrectSimpleName(".a.b.c");

    assertIncorrectSimpleName("a.");
    assertIncorrectSimpleName("a.b.");
    assertIncorrectSimpleName("a.b.c.");

    assertIncorrectSimpleName("a..b");

    assertIncorrectSimpleName("my-function");
    assertIncorrectSimpleName("a-package.myFunction");
  }

  private static void assertIncorrectSimpleName(String name) {
    assertThat(isValidSimpleName(name)).isFalse();
    try {
      simpleName(name);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testEquals() throws Exception {
    EqualsVerifier.forClass(CanonicalName.class).suppress(NULL_FIELDS).verify();
  }
}
