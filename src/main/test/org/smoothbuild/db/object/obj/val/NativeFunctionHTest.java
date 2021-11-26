package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class NativeFunctionHTest extends TestingContext {
  @Test
  public void type_of_native_method_expr_is_calculated_correctly() {
    assertThat(nativeFunctionH(blobH(), stringH()).type())
        .isEqualTo(nativeFunctionHT());
  }

  @Test
  public void jar_file_returns_jar_file_component() {
    var jarFile = blobH();
    var nativeFunctionH = nativeFunctionH(jarFile, stringH());
    assertThat(nativeFunctionH.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void class_binary_name_returns_class_binary_name_component() {
    var classBinaryName = stringH();
    var nativeFunctionH = nativeFunctionH(blobH(), classBinaryName);
    assertThat(nativeFunctionH.classBinaryName())
        .isEqualTo(classBinaryName);
  }

  @Test
  public void native_method_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(nativeFunctionH(jarFile, classBinaryName))
        .isEqualTo(nativeFunctionH(jarFile, classBinaryName));
  }

  @Test
  public void native_method_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(nativeFunctionH(blobH(ByteString.of((byte) 1)), classBinaryName))
        .isNotEqualTo(nativeFunctionH(blobH(ByteString.of((byte) 2)), classBinaryName));
  }

  @Test
  public void native_method_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(nativeFunctionH(jarFile, stringH("a")))
        .isNotEqualTo(nativeFunctionH(jarFile, stringH("b")));
  }

  @Test
  public void hash_of_native_method_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(nativeFunctionH(jarFile, classBinaryName).hash())
        .isEqualTo(nativeFunctionH(jarFile, classBinaryName).hash());
  }

  @Test
  public void hash_of_native_method_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(nativeFunctionH(blobH(ByteString.of((byte) 1)), classBinaryName).hash())
        .isNotEqualTo(nativeFunctionH(blobH(ByteString.of((byte) 2)), classBinaryName).hash());
  }

  @Test
  public void hash_of_native_method_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(nativeFunctionH(jarFile, stringH("a")).hash())
        .isNotEqualTo(nativeFunctionH(jarFile, stringH("b")).hash());
  }

  @Test
  public void hash_code_of_native_method_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(nativeFunctionH(jarFile, classBinaryName).hashCode())
        .isEqualTo(nativeFunctionH(jarFile, classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_native_method_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(nativeFunctionH(blobH(ByteString.of((byte) 1)), classBinaryName).hashCode())
        .isNotEqualTo(nativeFunctionH(blobH(ByteString.of((byte) 2)), classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_native_method_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(nativeFunctionH(jarFile, stringH("a")).hashCode())
        .isNotEqualTo(nativeFunctionH(jarFile, stringH("b")).hashCode());
  }

  @Test
  public void native_method_can_be_read_back_by_hash() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    var nativeFunctionH = nativeFunctionH(jarFile, classBinaryName);
    assertThat(objectHDbOther().get(nativeFunctionH.hash()))
        .isEqualTo(nativeFunctionH);
  }

  @Test
  public void native_method_read_back_by_hash_has_same_data() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    var nativeFunctionH = nativeFunctionH(jarFile, classBinaryName);
    var readNativeMethod = (NativeFunctionH) objectHDbOther().get(nativeFunctionH.hash());
    assertThat(readNativeMethod.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readNativeMethod.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void to_string() {
    var nativeFunctionH = nativeFunctionH(blobH(), stringH());
    assertThat(nativeFunctionH.toString())
        .isEqualTo("NativeFunctionH(???)@" + nativeFunctionH.hash());
  }
}
