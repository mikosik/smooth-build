package org.smoothbuild.function.base;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Name.isLegalSimpleName;
import static org.smoothbuild.function.base.Name.qualifiedName;
import static org.smoothbuild.function.base.Name.simpleName;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

public class NameTest {

  @Test
  public void legalQualifiedNames() {
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
    Name qualifiedName = qualifiedName(full);
    assertThat(qualifiedName.full()).isEqualTo(full);
    assertThat(qualifiedName.aPackage()).isEqualTo(aPackage);
    assertThat(qualifiedName.simple()).isEqualTo(name);
  }

  @Test
  public void illegalQualifiedNames() {
    assertIllegalQualifiedName("");
    assertIllegalQualifiedName(".");
    assertIllegalQualifiedName("..");
    assertIllegalQualifiedName("...");

    assertIllegalQualifiedName("@");
    assertIllegalQualifiedName("#");

    assertIllegalQualifiedName(".a");
    assertIllegalQualifiedName(".a.b");
    assertIllegalQualifiedName(".a.b.c");

    assertIllegalQualifiedName("a.");
    assertIllegalQualifiedName("a.b.");
    assertIllegalQualifiedName("a.b.c.");

    assertIllegalQualifiedName("a..b");

    assertIllegalQualifiedName("my-function");
    assertIllegalQualifiedName("a-package.myFunction");
  }

  private static void assertIllegalQualifiedName(String fullName) {
    try {
      qualifiedName(fullName);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

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
    assertThat(isLegalSimpleName(name)).isTrue();

    Name simpleName = simpleName(name);
    assertThat(simpleName.full()).isEqualTo(name);
    assertThat(simpleName.aPackage()).isEqualTo("");
    assertThat(simpleName.simple()).isEqualTo(name);
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
    assertThat(isLegalSimpleName(name)).isFalse();
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

  private static Name n(String name) {
    return qualifiedName(name);
  }
}
