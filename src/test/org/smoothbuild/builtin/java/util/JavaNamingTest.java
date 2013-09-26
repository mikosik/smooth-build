package org.smoothbuild.builtin.java.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.builtin.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Test;

public class JavaNamingTest {

  @Test
  public void testToBinaryName() {
    assertToBinaryNameResult("MyClass.class", "MyClass");
    assertToBinaryNameResult("my/package/MyClass.class", "my.package.MyClass");
    assertToBinaryNameResult("my/package/MyClass$InnerClass.class", "my.package.MyClass$InnerClass");
    assertToBinaryNameResult("my/package/MyClass.class", "my.package.MyClass");
  }

  private void assertToBinaryNameResult(String path, String expected) {
    assertThat(toBinaryName(path(path))).isEqualTo(expected);
  }

  @Test
  public void testBinaryNameToPackage() throws Exception {
    assertBinaryNameToPackageResult("MyClass", "");
    assertBinaryNameToPackageResult("my.package.MyClass", "my.package");
    assertBinaryNameToPackageResult("my.package.MyClass$Inner", "my.package");
  }

  private void assertBinaryNameToPackageResult(String binaryName, String expected) {
    assertThat(binaryNameToPackage(binaryName)).isEqualTo(expected);
  }

  @Test
  public void testIsClassFilePredicate() throws Exception {
    assertThat(isClassFilePredicate().apply("abc")).isFalse();
    assertThat(isClassFilePredicate().apply("Klass.java")).isFalse();
    assertThat(isClassFilePredicate().apply("abc/Klass.java")).isFalse();

    assertThat(isClassFilePredicate().apply("Klass.class")).isTrue();
    assertThat(isClassFilePredicate().apply("abc/Klass.class")).isTrue();
  }
}
