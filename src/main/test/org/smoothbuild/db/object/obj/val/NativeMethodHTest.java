package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class NativeMethodHTest extends TestingContext {
  @Test
  public void type_of_native_method_expr_is_calculated_correctly() {
    assertThat(nativeMethodH(blobH(), stringH()).type())
        .isEqualTo(nativeMethodHT());
  }

  @Test
  public void jar_file_returns_jar_file_component() {
    BlobH jarFile = blobH();
    NativeMethodH nativeMethod = nativeMethodH(jarFile, stringH());
    assertThat(nativeMethod.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void class_binary_name_returns_class_binary_name_component() {
    StringH classBinaryName = stringH();
    NativeMethodH nativeMethod = nativeMethodH(blobH(), classBinaryName);
    assertThat(nativeMethod.classBinaryName())
        .isEqualTo(classBinaryName);
  }

  @Test
  public void native_method_with_equal_values_are_equal() {
    BlobH jarFile = blobH();
    StringH classBinaryName = stringH();
    assertThat(nativeMethodH(jarFile, classBinaryName))
        .isEqualTo(nativeMethodH(jarFile, classBinaryName));
  }

  @Test
  public void native_method_with_different_jar_files_are_not_equal() {
    StringH classBinaryName = stringH();
    assertThat(nativeMethodH(blobH(ByteString.of((byte) 1)), classBinaryName))
        .isNotEqualTo(nativeMethodH(blobH(ByteString.of((byte) 2)), classBinaryName));
  }

  @Test
  public void native_method_with_different_class_binary_names_are_not_equal() {
    BlobH jarFile = blobH();
    assertThat(nativeMethodH(jarFile, stringH("a")))
        .isNotEqualTo(nativeMethodH(jarFile, stringH("b")));
  }

  @Test
  public void hash_of_native_method_with_equal_values_are_equal() {
    BlobH jarFile = blobH();
    StringH classBinaryName = stringH();
    assertThat(nativeMethodH(jarFile, classBinaryName).hash())
        .isEqualTo(nativeMethodH(jarFile, classBinaryName).hash());
  }

  @Test
  public void hash_of_native_method_with_different_jar_files_are_not_equal() {
    StringH classBinaryName = stringH();
    assertThat(nativeMethodH(blobH(ByteString.of((byte) 1)), classBinaryName).hash())
        .isNotEqualTo(nativeMethodH(blobH(ByteString.of((byte) 2)), classBinaryName).hash());
  }

  @Test
  public void hash_of_native_method_with_different_class_binary_names_are_not_equal() {
    BlobH jarFile = blobH();
    assertThat(nativeMethodH(jarFile, stringH("a")).hash())
        .isNotEqualTo(nativeMethodH(jarFile, stringH("b")).hash());
  }

  @Test
  public void hash_code_of_native_method_with_equal_values_are_equal() {
    BlobH jarFile = blobH();
    StringH classBinaryName = stringH();
    assertThat(nativeMethodH(jarFile, classBinaryName).hashCode())
        .isEqualTo(nativeMethodH(jarFile, classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_native_method_with_different_jar_files_are_not_equal() {
    StringH classBinaryName = stringH();
    assertThat(nativeMethodH(blobH(ByteString.of((byte) 1)), classBinaryName).hashCode())
        .isNotEqualTo(nativeMethodH(blobH(ByteString.of((byte) 2)), classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_native_method_with_different_class_binary_names_are_not_equal() {
    BlobH jarFile = blobH();
    assertThat(nativeMethodH(jarFile, stringH("a")).hashCode())
        .isNotEqualTo(nativeMethodH(jarFile, stringH("b")).hashCode());
  }

  @Test
  public void native_method_can_be_read_back_by_hash() {
    BlobH jarFile = blobH();
    StringH classBinaryName = stringH();
    NativeMethodH nativeMethod = nativeMethodH(jarFile, classBinaryName);
    assertThat(objectHDbOther().get(nativeMethod.hash()))
        .isEqualTo(nativeMethod);
  }

  @Test
  public void native_method_read_back_by_hash_has_same_data() {
    BlobH jarFile = blobH();
    StringH classBinaryName = stringH();
    NativeMethodH nativeMethod = nativeMethodH(jarFile, classBinaryName);
    NativeMethodH readNativeMethod = (NativeMethodH) objectHDbOther().get(nativeMethod.hash());
    assertThat(readNativeMethod.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readNativeMethod.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void to_string() {
    NativeMethodH nativeMethod = nativeMethodH(blobH(), stringH());
    assertThat(nativeMethod.toString())
        .isEqualTo("NativeMethod(???)@" + nativeMethod.hash());
  }
}
