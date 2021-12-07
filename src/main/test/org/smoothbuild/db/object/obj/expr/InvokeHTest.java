package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class InvokeHTest extends TestingContext {
  @Test
  public void type_of_invoke_expr_is_calculated_correctly() {
    assertThat(invokeH(blobH(), stringH()).cat())
        .isEqualTo(invokeCH());
  }

  @Test
  public void jar_file_returns_jar_file_component() {
    var jarFile = blobH();
    var invokeH = invokeH(jarFile, stringH());
    assertThat(invokeH.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void class_binary_name_returns_class_binary_name_component() {
    var classBinaryName = stringH();
    var invokeH = invokeH(blobH(), classBinaryName);
    assertThat(invokeH.classBinaryName())
        .isEqualTo(classBinaryName);
  }

  @Test
  public void invoke_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(invokeH(jarFile, classBinaryName))
        .isEqualTo(invokeH(jarFile, classBinaryName));
  }

  @Test
  public void invoke_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(invokeH(blobH(ByteString.of((byte) 1)), classBinaryName))
        .isNotEqualTo(invokeH(blobH(ByteString.of((byte) 2)), classBinaryName));
  }

  @Test
  public void invoke_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(invokeH(jarFile, stringH("a")))
        .isNotEqualTo(invokeH(jarFile, stringH("b")));
  }

  @Test
  public void hash_of_invoke_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(invokeH(jarFile, classBinaryName).hash())
        .isEqualTo(invokeH(jarFile, classBinaryName).hash());
  }

  @Test
  public void hash_of_invoke_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(invokeH(blobH(ByteString.of((byte) 1)), classBinaryName).hash())
        .isNotEqualTo(invokeH(blobH(ByteString.of((byte) 2)), classBinaryName).hash());
  }

  @Test
  public void hash_of_invoke_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(invokeH(jarFile, stringH("a")).hash())
        .isNotEqualTo(invokeH(jarFile, stringH("b")).hash());
  }

  @Test
  public void hash_code_of_invoke_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(invokeH(jarFile, classBinaryName).hashCode())
        .isEqualTo(invokeH(jarFile, classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_invoke_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(invokeH(blobH(ByteString.of((byte) 1)), classBinaryName).hashCode())
        .isNotEqualTo(invokeH(blobH(ByteString.of((byte) 2)), classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_invoke_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(invokeH(jarFile, stringH("a")).hashCode())
        .isNotEqualTo(invokeH(jarFile, stringH("b")).hashCode());
  }

  @Test
  public void invoke_can_be_read_back_by_hash() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    var invokeH = invokeH(jarFile, classBinaryName);
    assertThat(objDbOther().get(invokeH.hash()))
        .isEqualTo(invokeH);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    var invokeH = invokeH(jarFile, classBinaryName);
    var readInvokeH = (InvokeH) objDbOther().get(invokeH.hash());
    assertThat(readInvokeH.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readInvokeH.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void to_string() {
    var invokeH = invokeH(blobH(), stringH());
    assertThat(invokeH.toString())
        .isEqualTo("Invoke:Blob(???)@" + invokeH.hash());
  }
}
