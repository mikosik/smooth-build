package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class MethodBTest extends TestingContext {
  @Test
  public void type_is_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(methodH.type())
        .isEqualTo(methodTH);
  }

  @Test
  public void components_are_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(type, jar, classBinaryName, isPure);

    assertThat(methodH.jar())
        .isEqualTo(jar);
    assertThat(methodH.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(methodH.isPure())
        .isEqualTo(isPure);
  }

  @Test
  public void method_with_equal_values_are_equal() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName, isPure);
    var method2 = methodB(type, jar, classBinaryName, isPure);
    assertThat(method1)
        .isEqualTo(method2);
  }

  @Test
  public void method_with_different_jar_files_are_not_equal() {
    var jar1 = blobB(1);
    var jar2 = blobB(2);
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar1, classBinaryName, isPure);
    var method2 = methodB(type, jar2, classBinaryName, isPure);

    assertThat(method1)
        .isNotEqualTo(method2);
  }

  @Test
  public void method_with_different_class_binary_names_are_not_equal() {
    var jar = blobB();
    var classBinaryName1 = stringB("abc");
    var classBinaryName2 = stringB("def");
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName1, isPure);
    var method2 = methodB(type, jar, classBinaryName2, isPure);
    assertThat(method1)
        .isNotEqualTo(method2);
  }

  @Test
  public void method_with_different_is_pure_are_not_equal() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure1 = boolB(true);
    var isPure2 = boolB(false);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName, isPure1);
    var method2 = methodB(type, jar, classBinaryName, isPure2);
    assertThat(method1)
        .isNotEqualTo(method2);
  }

  @Test
  public void hash_of_method_with_equal_values_are_equal() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName, isPure);
    var method2 = methodB(type, jar, classBinaryName, isPure);
    assertThat(method1.hash())
        .isEqualTo(method2.hash());
  }

  @Test
  public void hash_of_method_with_different_jar_files_are_not_equal() {
    var jar1 = blobB(1);
    var jar2 = blobB(2);
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar1, classBinaryName, isPure);
    var method2 = methodB(type, jar2, classBinaryName, isPure);

    assertThat(method1.hash())
        .isNotEqualTo(method2.hash());
  }

  @Test
  public void hash_of_method_with_different_class_binary_names_are_not_equal() {
    var jar = blobB();
    var classBinaryName1 = stringB("abc");
    var classBinaryName2 = stringB("def");
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName1, isPure);
    var method2 = methodB(type, jar, classBinaryName2, isPure);
    assertThat(method1.hash())
        .isNotEqualTo(method2.hash());
  }

  @Test
  public void hash_of_method_with_different_is_pure_are_not_equal() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure1 = boolB(true);
    var isPure2 = boolB(false);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName, isPure1);
    var method2 = methodB(type, jar, classBinaryName, isPure2);
    assertThat(method1.hash())
        .isNotEqualTo(method2.hash());
  }

  @Test
  public void hashCode_of_method_with_equal_values_are_equal() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName, isPure);
    var method2 = methodB(type, jar, classBinaryName, isPure);
    assertThat(method1.hashCode())
        .isEqualTo(method2.hashCode());
  }

  @Test
  public void hashCode_of_method_with_different_jar_files_are_not_equal() {
    var jar1 = blobB(1);
    var jar2 = blobB(2);
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar1, classBinaryName, isPure);
    var method2 = methodB(type, jar2, classBinaryName, isPure);

    assertThat(method1.hashCode())
        .isNotEqualTo(method2.hashCode());
  }

  @Test
  public void hashCode_of_method_with_different_class_binary_names_are_not_equal() {
    var jar = blobB();
    var classBinaryName1 = stringB("abc");
    var classBinaryName2 = stringB("def");
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName1, isPure);
    var method2 = methodB(type, jar, classBinaryName2, isPure);
    assertThat(method1.hashCode())
        .isNotEqualTo(method2.hashCode());
  }

  @Test
  public void hashCode_of_method_with_different_is_pure_are_not_equal() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure1 = boolB(true);
    var isPure2 = boolB(false);
    var type = methodTB(stringTB(), list(intTB()));

    var method1 = methodB(type, jar, classBinaryName, isPure1);
    var method2 = methodB(type, jar, classBinaryName, isPure2);
    assertThat(method1.hashCode())
        .isNotEqualTo(method2.hashCode());
  }

  @Test
  public void method_can_be_read_back_by_hash() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(byteDbOther().get(methodH.hash()))
        .isEqualTo(methodH);
  }

  @Test
  public void method_read_back_by_hash_has_same_data() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    var readMethodH = (MethodB) byteDbOther().get(methodH.hash());
    assertThat(readMethodH.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readMethodH.jar())
        .isEqualTo(jar);
    assertThat(readMethodH.isPure())
        .isEqualTo(isPure);
  }

  @Test
  public void to_string() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(methodH.toString())
        .isEqualTo("Method(_String(Int))@" + methodH.hash());
  }
}