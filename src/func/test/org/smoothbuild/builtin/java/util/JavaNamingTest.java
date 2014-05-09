package org.smoothbuild.builtin.java.util;

import static org.smoothbuild.builtin.java.util.JavaNaming.binaryNameToPackage;
import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class JavaNamingTest {

  @Test
  public void binary_name_of_class_in_default_package_is_class_name() throws Exception {
    when(toBinaryName(path("MyClass.class")));
    thenReturned("MyClass");
  }

  @Test
  public void binary_name_of_class_in_package_is_package_plus_class_name() throws Exception {
    when(toBinaryName(path("my/package/MyClass.class")));
    thenReturned("my.package.MyClass");
  }

  @Test
  public void binary_name_of_inner_class_is_package_plus_outer_class_name_plus_inner_class_name()
      throws Exception {
    when(toBinaryName(path("my/package/MyClass$Inner.class")));
    thenReturned("my.package.MyClass$Inner");
  }

  @Test
  public void package_of_class_in_default_package_is_empty() throws Exception {
    when(binaryNameToPackage("MyClass"));
    thenReturned("");
  }

  @Test
  public void package_of_class() throws Exception {
    when(binaryNameToPackage("my.package.MyClass"));
    thenReturned("my.package");
  }

  @Test
  public void package_of_inner_class_is_equal_to_package_of_outer_class() throws Exception {
    when(binaryNameToPackage("my.package.Outer$Inner"));
    thenReturned("my.package");
  }

  @Test
  public void file_without_extension_is_not_class_file() throws Exception {
    when(isClassFilePredicate()).apply("file");
    thenReturned(false);
  }

  @Test
  public void file_with_java_extension_is_not_class_file() throws Exception {
    when(isClassFilePredicate()).apply("file.java");
    thenReturned(false);
  }

  @Test
  public void file_with_class_extension_is_class_file() throws Exception {
    when(isClassFilePredicate()).apply("file.class");
    thenReturned(true);
  }
}
