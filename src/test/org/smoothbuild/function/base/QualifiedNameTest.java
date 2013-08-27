package org.smoothbuild.function.base;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.QualifiedName.qualifiedName;
import static org.smoothbuild.function.base.QualifiedName.isValidSimpleName;
import static org.smoothbuild.function.base.QualifiedName.simpleName;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

public class QualifiedNameTest {

  @Test
  public void correctQualifiedNames() {
    doTestQualifiedName("a", "", "a");
    doTestQualifiedName("ab", "", "ab");
    doTestQualifiedName("abc", "", "abc");
    doTestQualifiedName("abcd", "", "abcd");

    doTestQualifiedName("a.b", "a", "b");
    doTestQualifiedName("a.b.c", "a.b", "c");
    doTestQualifiedName("a.b.c.d", "a.b.c", "d");

    doTestQualifiedName("my.package.FuncA", "my.package", "FuncA");
    doTestQualifiedName("MY.PACKAGE.FUNC_A", "MY.PACKAGE", "FUNC_A");

    doTestQualifiedName("my_function", "", "my_function");
    doTestQualifiedName("a_package.my_function", "a_package", "my_function");
    doTestQualifiedName("_._._", "_._", "_");
    doTestQualifiedName("a_._b._c_", "a_._b", "_c_");
  }

  private static void doTestQualifiedName(String full, String aPackage, String name) {
    QualifiedName qualifiedName = qualifiedName(full);
    assertThat(qualifiedName.full()).isEqualTo(full);
    assertThat(qualifiedName.aPackage()).isEqualTo(aPackage);
    assertThat(qualifiedName.simple()).isEqualTo(name);
  }

  @Test
  public void incorrectQualifiedNames() {
    assertIncorrectQualifiedName("");
    assertIncorrectQualifiedName(".");
    assertIncorrectQualifiedName("..");
    assertIncorrectQualifiedName("...");

    assertIncorrectQualifiedName("@");
    assertIncorrectQualifiedName("#");

    assertIncorrectQualifiedName(".a");
    assertIncorrectQualifiedName(".a.b");
    assertIncorrectQualifiedName(".a.b.c");

    assertIncorrectQualifiedName("a.");
    assertIncorrectQualifiedName("a.b.");
    assertIncorrectQualifiedName("a.b.c.");

    assertIncorrectQualifiedName("a..b");

    assertIncorrectQualifiedName("my-function");
    assertIncorrectQualifiedName("a-package.myFunction");
  }

  private static void assertIncorrectQualifiedName(String fullName) {
    try {
      qualifiedName(fullName);
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

    QualifiedName qualifiedName = QualifiedName.simpleName(name);
    assertThat(qualifiedName.full()).isEqualTo(name);
    assertThat(qualifiedName.aPackage()).isEqualTo("");
    assertThat(qualifiedName.simple()).isEqualTo(name);
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
    assertThat(qualifiedName("abc").toString()).isEqualTo("'abc'");
  }

  @Test
  public void testEquals() throws Exception {
    EqualsVerifier
        .forExamples(n("a"), n("b"), n("c"), n("a.a"), n("a.b"), n("b.a"), n("b.b"), n("a.b.c"))
        .suppress(NULL_FIELDS).verify();
  }

  private static QualifiedName n(String name) {
    return qualifiedName(name);
  }
}
