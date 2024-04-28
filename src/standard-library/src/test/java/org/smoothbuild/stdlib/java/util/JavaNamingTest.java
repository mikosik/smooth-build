package org.smoothbuild.stdlib.java.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.stdlib.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.stdlib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.stdlib.java.util.JavaNaming.toBinaryName;

import org.junit.jupiter.api.Test;

public class JavaNamingTest {
  @Test
  void binary_name_of_class_in_default_package_is_class_name() {
    assertThat(toBinaryName("MyClass.class")).isEqualTo("MyClass");
  }

  @Test
  void binary_name_of_class_in_package_is_package_plus_class_name() {
    assertThat(toBinaryName("my/package/MyClass.class")).isEqualTo("my.package.MyClass");
  }

  @Test
  void binary_name_of_inner_class_is_package_plus_outer_class_name_plus_inner_class_name() {
    assertThat(toBinaryName("my/package/MyClass$Inner.class"))
        .isEqualTo("my.package.MyClass$Inner");
  }

  @Test
  void package_of_class_in_default_package_is_empty() {
    assertThat(binaryNameToPackage("MyClass")).isEqualTo("");
  }

  @Test
  void package_of_class() {
    assertThat(binaryNameToPackage("my.package.MyClass")).isEqualTo("my.package");
  }

  @Test
  void package_of_inner_class_is_equal_to_package_of_outer_class() {
    assertThat(binaryNameToPackage("my.package.Outer$Inner")).isEqualTo("my.package");
  }

  @Test
  void file_without_extension_is_not_class_file() {
    assertThat(isClassFilePredicate().test("file")).isFalse();
  }

  @Test
  void file_with_java_extension_is_not_class_file() {
    assertThat(isClassFilePredicate().test("file.java")).isFalse();
  }

  @Test
  void file_with_class_extension_is_class_file() {
    assertThat(isClassFilePredicate().test("file.class")).isTrue();
  }
}
