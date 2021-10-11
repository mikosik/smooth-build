package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class InvokeTest extends TestingContext {
  @Test
  public void spec_of_invoke_expr_is_calculated_correctly() {
    assertThat(invokeExpr(blobVal(), strVal(), intSpec()).spec())
        .isEqualTo(invokeSpec(intSpec()));
  }

  @Test
  public void jar_file_returns_jar_file_component() {
    Blob jarFile = blobVal();
    Invoke invoke = invokeExpr(jarFile, strVal(), intSpec());
    assertThat(invoke.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void class_binary_name_returns_class_binary_name_component() {
    Str classBinaryName = strVal();
    Invoke invoke = invokeExpr(blobVal(), classBinaryName, intSpec());
    assertThat(invoke.classBinaryName())
        .isEqualTo(classBinaryName);
  }

  @Test
  public void invoke_with_equal_values_are_equal() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    assertThat(invokeExpr(jarFile, classBinaryName, intSpec()))
        .isEqualTo(invokeExpr(jarFile, classBinaryName, intSpec()));
  }

  @Test
  public void invoke_with_different_jar_files_are_not_equal() {
    Str classBinaryName = strVal();
    assertThat(invokeExpr(blobVal(ByteString.of((byte) 1)), classBinaryName, intSpec()))
        .isNotEqualTo(invokeExpr(blobVal(ByteString.of((byte) 2)), classBinaryName, intSpec()));
  }

  @Test
  public void invoke_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blobVal();
    assertThat(invokeExpr(jarFile, strVal("a"), intSpec()))
        .isNotEqualTo(invokeExpr(jarFile, strVal("b"), intSpec()));
  }

  @Test
  public void hash_of_invoke_with_equal_values_are_equal() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    assertThat(invokeExpr(jarFile, classBinaryName, intSpec()).hash())
        .isEqualTo(invokeExpr(jarFile, classBinaryName, intSpec()).hash());
  }

  @Test
  public void hash_of_invoke_with_different_jar_files_are_not_equal() {
    Str classBinaryName = strVal();
    assertThat(invokeExpr(blobVal(ByteString.of((byte) 1)), classBinaryName, intSpec()).hash())
        .isNotEqualTo(invokeExpr(blobVal(ByteString.of((byte) 2)), classBinaryName, intSpec()).hash());
  }

  @Test
  public void hash_of_invoke_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blobVal();
    assertThat(invokeExpr(jarFile, strVal("a"), intSpec()).hash())
        .isNotEqualTo(invokeExpr(jarFile, strVal("b"), intSpec()).hash());
  }

  @Test
  public void hash_code_of_invoke_with_equal_values_are_equal() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    assertThat(invokeExpr(jarFile, classBinaryName, intSpec()).hashCode())
        .isEqualTo(invokeExpr(jarFile, classBinaryName, intSpec()).hashCode());
  }

  @Test
  public void hash_code_of_invoke_with_different_jar_files_are_not_equal() {
    Str classBinaryName = strVal();
    assertThat(invokeExpr(blobVal(ByteString.of((byte) 1)), classBinaryName, intSpec()).hashCode())
        .isNotEqualTo(invokeExpr(blobVal(ByteString.of((byte) 2)), classBinaryName, intSpec()).hashCode());
  }

  @Test
  public void hash_code_of_invoke_with_different_class_binary_names_are_not_equal() {
    Blob jarFile = blobVal();
    assertThat(invokeExpr(jarFile, strVal("a"), intSpec()).hashCode())
        .isNotEqualTo(invokeExpr(jarFile, strVal("b"), intSpec()).hashCode());
  }

  @Test
  public void invoke_can_be_read_back_by_hash() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    Invoke invoke = invokeExpr(jarFile, classBinaryName, intSpec());
    assertThat(objectDbOther().get(invoke.hash()))
        .isEqualTo(invoke);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    Blob jarFile = blobVal();
    Str classBinaryName = strVal();
    Invoke invoke = invokeExpr(jarFile, classBinaryName, intSpec());
    Invoke readInvoke = (Invoke) objectDbOther().get(invoke.hash());
    assertThat(readInvoke.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readInvoke.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void to_string() {
    Invoke invoke = invokeExpr(blobVal(), strVal(), intSpec());
    assertThat(invoke.toString())
        .isEqualTo("Invoke(???)@" + invoke.hash());
  }
}
