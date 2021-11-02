package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

import okio.ByteString;

public class NativeMethodTest extends TestingContextImpl {
  @Test
  public void spec_of_native_method_expr_is_calculated_correctly() {
    assertThat(nativeMethod(blob(), string()).spec())
        .isEqualTo(nativeMethodSpec());
  }

  @Test
  public void jar_file_returns_jar_file_component() {
    Blob jarFile = blob();
    NativeMethod nativeMethod = nativeMethod(jarFile, string());
    assertThat(nativeMethod.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void class_binary_name_returns_class_binary_name_component() {
    Str classBinaryName = string();
    NativeMethod nativeMethod = nativeMethod(blob(), classBinaryName);
    assertThat(nativeMethod.classBinaryName())
        .isEqualTo(classBinaryName);
  }

  @Test
  public void native_method_with_equal_values_are_equal() {
    Blob jarFile = blob();
    Str classBinaryName = string();
    assertThat(nativeMethod(jarFile, classBinaryName))
        .isEqualTo(nativeMethod(jarFile, classBinaryName));
  }

  @Test
  public void native_method_with_different_jar_files_are_not_equal() {
    Str classBinaryName = string();
    assertThat(nativeMethod(blob(ByteString.of((byte) 1)), classBinaryName))
        .isNotEqualTo(nativeMethod(blob(ByteString.of((byte) 2)), classBinaryName));
  }

  @Test
  public void native_method_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blob();
    assertThat(nativeMethod(jarFile, string("a")))
        .isNotEqualTo(nativeMethod(jarFile, string("b")));
  }

  @Test
  public void hash_of_native_method_with_equal_values_are_equal() {
    Blob jarFile = blob();
    Str classBinaryName = string();
    assertThat(nativeMethod(jarFile, classBinaryName).hash())
        .isEqualTo(nativeMethod(jarFile, classBinaryName).hash());
  }

  @Test
  public void hash_of_native_method_with_different_jar_files_are_not_equal() {
    Str classBinaryName = string();
    assertThat(nativeMethod(blob(ByteString.of((byte) 1)), classBinaryName).hash())
        .isNotEqualTo(nativeMethod(blob(ByteString.of((byte) 2)), classBinaryName).hash());
  }

  @Test
  public void hash_of_native_method_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blob();
    assertThat(nativeMethod(jarFile, string("a")).hash())
        .isNotEqualTo(nativeMethod(jarFile, string("b")).hash());
  }

  @Test
  public void hash_code_of_native_method_with_equal_values_are_equal() {
    Blob jarFile = blob();
    Str classBinaryName = string();
    assertThat(nativeMethod(jarFile, classBinaryName).hashCode())
        .isEqualTo(nativeMethod(jarFile, classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_native_method_with_different_jar_files_are_not_equal() {
    Str classBinaryName = string();
    assertThat(nativeMethod(blob(ByteString.of((byte) 1)), classBinaryName).hashCode())
        .isNotEqualTo(nativeMethod(blob(ByteString.of((byte) 2)), classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_native_method_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blob();
    assertThat(nativeMethod(jarFile, string("a")).hashCode())
        .isNotEqualTo(nativeMethod(jarFile, string("b")).hashCode());
  }

  @Test
  public void native_method_can_be_read_back_by_hash() {
    Blob jarFile = blob();
    Str classBinaryName = string();
    NativeMethod nativeMethod = nativeMethod(jarFile, classBinaryName);
    assertThat(objectDbOther().get(nativeMethod.hash()))
        .isEqualTo(nativeMethod);
  }

  @Test
  public void native_method_read_back_by_hash_has_same_data() {
    Blob jarFile = blob();
    Str classBinaryName = string();
    NativeMethod nativeMethod = nativeMethod(jarFile, classBinaryName);
    NativeMethod readNativeMethod = (NativeMethod) objectDbOther().get(nativeMethod.hash());
    assertThat(readNativeMethod.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readNativeMethod.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void to_string() {
    NativeMethod nativeMethod = nativeMethod(blob(), string());
    assertThat(nativeMethod.toString())
        .isEqualTo("NativeMethod(???)@" + nativeMethod.hash());
  }
}
