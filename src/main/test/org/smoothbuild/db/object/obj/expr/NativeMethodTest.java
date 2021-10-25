package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.testing.TestingContextImpl;

import okio.ByteString;

public class NativeMethodTest extends TestingContextImpl {
  @Test
  public void spec_of_native_method_expr_is_calculated_correctly() {
    assertThat(nativeMethodVal(blobVal(), strVal()).spec())
        .isEqualTo(nativeMethodSpec());
  }

  @Test
  public void jar_file_returns_jar_file_component() {
    Blob jarFile = blobVal();
    NativeMethod nativeMethod = nativeMethodVal(jarFile, strVal());
    assertThat(nativeMethod.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void class_binary_name_returns_class_binary_name_component() {
    Str classBinaryName = strVal();
    NativeMethod nativeMethod = nativeMethodVal(blobVal(), classBinaryName);
    assertThat(nativeMethod.classBinaryName())
        .isEqualTo(classBinaryName);
  }

  @Test
  public void native_method_with_equal_values_are_equal() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    assertThat(nativeMethodVal(jarFile, classBinaryName))
        .isEqualTo(nativeMethodVal(jarFile, classBinaryName));
  }

  @Test
  public void native_method_with_different_jar_files_are_not_equal() {
    Str classBinaryName = strVal();
    assertThat(nativeMethodVal(blobVal(ByteString.of((byte) 1)), classBinaryName))
        .isNotEqualTo(nativeMethodVal(blobVal(ByteString.of((byte) 2)), classBinaryName));
  }

  @Test
  public void native_method_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blobVal();
    assertThat(nativeMethodVal(jarFile, strVal("a")))
        .isNotEqualTo(nativeMethodVal(jarFile, strVal("b")));
  }

  @Test
  public void hash_of_native_method_with_equal_values_are_equal() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    assertThat(nativeMethodVal(jarFile, classBinaryName).hash())
        .isEqualTo(nativeMethodVal(jarFile, classBinaryName).hash());
  }

  @Test
  public void hash_of_native_method_with_different_jar_files_are_not_equal() {
    Str classBinaryName = strVal();
    assertThat(nativeMethodVal(blobVal(ByteString.of((byte) 1)), classBinaryName).hash())
        .isNotEqualTo(nativeMethodVal(blobVal(ByteString.of((byte) 2)), classBinaryName).hash());
  }

  @Test
  public void hash_of_native_method_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blobVal();
    assertThat(nativeMethodVal(jarFile, strVal("a")).hash())
        .isNotEqualTo(nativeMethodVal(jarFile, strVal("b")).hash());
  }

  @Test
  public void hash_code_of_native_method_with_equal_values_are_equal() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    assertThat(nativeMethodVal(jarFile, classBinaryName).hashCode())
        .isEqualTo(nativeMethodVal(jarFile, classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_native_method_with_different_jar_files_are_not_equal() {
    Str classBinaryName = strVal();
    assertThat(nativeMethodVal(blobVal(ByteString.of((byte) 1)), classBinaryName).hashCode())
        .isNotEqualTo(nativeMethodVal(blobVal(ByteString.of((byte) 2)), classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_native_method_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blobVal();
    assertThat(nativeMethodVal(jarFile, strVal("a")).hashCode())
        .isNotEqualTo(nativeMethodVal(jarFile, strVal("b")).hashCode());
  }

  @Test
  public void native_method_can_be_read_back_by_hash() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    NativeMethod nativeMethod = nativeMethodVal(jarFile, classBinaryName);
    assertThat(objectDbOther().get(nativeMethod.hash()))
        .isEqualTo(nativeMethod);
  }

  @Test
  public void native_method_read_back_by_hash_has_same_data() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    NativeMethod nativeMethod = nativeMethodVal(jarFile, classBinaryName);
    NativeMethod readNativeMethod = (NativeMethod) objectDbOther().get(nativeMethod.hash());
    assertThat(readNativeMethod.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readNativeMethod.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void to_string() {
    NativeMethod nativeMethod = nativeMethodVal(blobVal(), strVal());
    assertThat(nativeMethod.toString())
        .isEqualTo("NativeMethod(???)@" + nativeMethod.hash());
  }
}
