package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class NatFuncHTest extends TestingContext {
  @Test
  public void type_of_nat_func_expr_is_calculated_correctly() {
    assertThat(natFuncH(blobH(), stringH()).cat())
        .isEqualTo(natFuncTH());
  }

  @Test
  public void jar_file_returns_jar_file_component() {
    var jarFile = blobH();
    var natFuncH = natFuncH(jarFile, stringH());
    assertThat(natFuncH.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void class_binary_name_returns_class_binary_name_component() {
    var classBinaryName = stringH();
    var natFuncH = natFuncH(blobH(), classBinaryName);
    assertThat(natFuncH.classBinaryName())
        .isEqualTo(classBinaryName);
  }

  @Test
  public void nat_func_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(natFuncH(jarFile, classBinaryName))
        .isEqualTo(natFuncH(jarFile, classBinaryName));
  }

  @Test
  public void nat_func_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(natFuncH(blobH(ByteString.of((byte) 1)), classBinaryName))
        .isNotEqualTo(natFuncH(blobH(ByteString.of((byte) 2)), classBinaryName));
  }

  @Test
  public void nat_func_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(natFuncH(jarFile, stringH("a")))
        .isNotEqualTo(natFuncH(jarFile, stringH("b")));
  }

  @Test
  public void hash_of_nat_func_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(natFuncH(jarFile, classBinaryName).hash())
        .isEqualTo(natFuncH(jarFile, classBinaryName).hash());
  }

  @Test
  public void hash_of_nat_func_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(natFuncH(blobH(ByteString.of((byte) 1)), classBinaryName).hash())
        .isNotEqualTo(natFuncH(blobH(ByteString.of((byte) 2)), classBinaryName).hash());
  }

  @Test
  public void hash_of_nat_func_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(natFuncH(jarFile, stringH("a")).hash())
        .isNotEqualTo(natFuncH(jarFile, stringH("b")).hash());
  }

  @Test
  public void hash_code_of_nat_func_with_equal_values_are_equal() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    assertThat(natFuncH(jarFile, classBinaryName).hashCode())
        .isEqualTo(natFuncH(jarFile, classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_nat_func_with_different_jar_files_are_not_equal() {
    var classBinaryName = stringH();
    assertThat(natFuncH(blobH(ByteString.of((byte) 1)), classBinaryName).hashCode())
        .isNotEqualTo(natFuncH(blobH(ByteString.of((byte) 2)), classBinaryName).hashCode());
  }

  @Test
  public void hash_code_of_nat_func_with_different_class_binary_names_are_not_equal() {
    var jarFile = blobH();
    assertThat(natFuncH(jarFile, stringH("a")).hashCode())
        .isNotEqualTo(natFuncH(jarFile, stringH("b")).hashCode());
  }

  @Test
  public void nat_func_can_be_read_back_by_hash() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    var natFuncH = natFuncH(jarFile, classBinaryName);
    assertThat(objDbOther().get(natFuncH.hash()))
        .isEqualTo(natFuncH);
  }

  @Test
  public void nat_func_read_back_by_hash_has_same_data() {
    var jarFile = blobH();
    var classBinaryName = stringH();
    var natFuncH = natFuncH(jarFile, classBinaryName);
    var readNatFunc = (NatFuncH) objDbOther().get(natFuncH.hash());
    assertThat(readNatFunc.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readNatFunc.jarFile())
        .isEqualTo(jarFile);
  }

  @Test
  public void to_string() {
    var natFuncH = natFuncH(blobH(), stringH());
    assertThat(natFuncH.toString())
        .isEqualTo("NatFuncH(???)@" + natFuncH.hash());
  }
}
