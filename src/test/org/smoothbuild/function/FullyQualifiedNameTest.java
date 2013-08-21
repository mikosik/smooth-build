package org.smoothbuild.function;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.function.FullyQualifiedName.fullyQualifiedName;
import static org.smoothbuild.function.FullyQualifiedName.isValidSimpleName;
import static org.smoothbuild.function.FullyQualifiedName.simpleName;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

public class FullyQualifiedNameTest {

  @Test
  public void correctFullyQualifiedNames() {
    doTestFullyQualifiedName("a", "", "a");
    doTestFullyQualifiedName("ab", "", "ab");
    doTestFullyQualifiedName("abc", "", "abc");
    doTestFullyQualifiedName("abcd", "", "abcd");

    doTestFullyQualifiedName("a.b", "a", "b");
    doTestFullyQualifiedName("a.b.c", "a.b", "c");
    doTestFullyQualifiedName("a.b.c.d", "a.b.c", "d");

    doTestFullyQualifiedName("my.package.FuncA", "my.package", "FuncA");
    doTestFullyQualifiedName("MY.PACKAGE.FUNC_A", "MY.PACKAGE", "FUNC_A");

    doTestFullyQualifiedName("my_function", "", "my_function");
    doTestFullyQualifiedName("a_package.my_function", "a_package", "my_function");
    doTestFullyQualifiedName("_._._", "_._", "_");
    doTestFullyQualifiedName("a_._b._c_", "a_._b", "_c_");
  }

  private static void doTestFullyQualifiedName(String full, String aPackage, String name) {
    FullyQualifiedName fullyQualifiedName = fullyQualifiedName(full);
    assertThat(fullyQualifiedName.full()).isEqualTo(full);
    assertThat(fullyQualifiedName.aPackage()).isEqualTo(aPackage);
    assertThat(fullyQualifiedName.simple()).isEqualTo(name);
  }

  @Test
  public void incorrectFullyQualifiedNames() {
    assertIncorrectFullyQualifiedName("");
    assertIncorrectFullyQualifiedName(".");
    assertIncorrectFullyQualifiedName("..");
    assertIncorrectFullyQualifiedName("...");

    assertIncorrectFullyQualifiedName("@");
    assertIncorrectFullyQualifiedName("#");

    assertIncorrectFullyQualifiedName(".a");
    assertIncorrectFullyQualifiedName(".a.b");
    assertIncorrectFullyQualifiedName(".a.b.c");

    assertIncorrectFullyQualifiedName("a.");
    assertIncorrectFullyQualifiedName("a.b.");
    assertIncorrectFullyQualifiedName("a.b.c.");

    assertIncorrectFullyQualifiedName("a..b");

    assertIncorrectFullyQualifiedName("my-function");
    assertIncorrectFullyQualifiedName("a-package.myFunction");
  }

  private static void assertIncorrectFullyQualifiedName(String fullName) {
    try {
      fullyQualifiedName(fullName);
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

    FullyQualifiedName fullyQualifiedName = FullyQualifiedName.simpleName(name);
    assertThat(fullyQualifiedName.full()).isEqualTo(name);
    assertThat(fullyQualifiedName.aPackage()).isEqualTo("");
    assertThat(fullyQualifiedName.simple()).isEqualTo(name);
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
  public void testToString() throws Exception {
    assertThat(fullyQualifiedName("abc").toString()).isEqualTo("'abc'");
  }

  @Test
  public void testEquals() throws Exception {
    EqualsVerifier
        .forExamples(n("a"), n("b"), n("c"), n("a.a"), n("a.b"), n("b.a"), n("b.b"), n("a.b.c"))
        .suppress(NULL_FIELDS).verify();
  }

  private static FullyQualifiedName n(String name) {
    return fullyQualifiedName(name);
  }
}
