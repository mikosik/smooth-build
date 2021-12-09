package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class InvokeHTest extends TestingContext {
  @Test
  public void type_is_read_correctly() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));
    var invokeCH = invokeCH(stringTH(), list(intTH()));
    var invokeH = invokeH(invokeCH, jar, classBinaryName, isPure, args);
    assertThat(invokeH.cat())
        .isEqualTo(invokeCH);
  }

  @Test
  public void components_are_read_correctly() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));
    var invokeH = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args);

    assertThat(invokeH.jarFile())
        .isEqualTo(jar);
    assertThat(invokeH.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(invokeH.isPure())
        .isEqualTo(isPure);
    assertThat(invokeH.args())
        .isEqualTo(args);
  }

  @Test
  public void invoke_with_equal_values_are_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args);
    assertThat(invoke1)
        .isEqualTo(invoke2);
  }

  @Test
  public void invoke_with_different_jar_files_are_not_equal() {
    var jar1 = blobH(1);
    var jar2 = blobH(2);
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar1, classBinaryName, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar2, classBinaryName, isPure, args);

    assertThat(invoke1)
        .isNotEqualTo(invoke2);
  }

  @Test
  public void invoke_with_different_class_binary_names_are_not_equal() {
    var jar = blobH();
    var classBinaryName1 = stringH("abc");
    var classBinaryName2 = stringH("def");
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName1, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName2, isPure, args);
    assertThat(invoke1)
        .isNotEqualTo(invoke2);
  }

  @Test
  public void invoke_with_different_is_pure_are_not_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure1 = boolH(true);
    var isPure2 = boolH(false);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure1, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure2, args);
    assertThat(invoke1)
        .isNotEqualTo(invoke2);
  }

  @Test
  public void invoke_with_different_args_are_not_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args1 = combineH(list(intH(3)));
    var args2 = combineH(list(intH(4)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args1);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args2);
    assertThat(invoke1)
        .isNotEqualTo(invoke2);
  }

  @Test
  public void hash_of_invoke_with_equal_values_are_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args);
    assertThat(invoke1.hash())
        .isEqualTo(invoke2.hash());
  }

  @Test
  public void hash_of_invoke_with_different_jar_files_are_not_equal() {
    var jar1 = blobH(1);
    var jar2 = blobH(2);
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar1, classBinaryName, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar2, classBinaryName, isPure, args);

    assertThat(invoke1.hash())
        .isNotEqualTo(invoke2.hash());
  }

  @Test
  public void hash_of_invoke_with_different_class_binary_names_are_not_equal() {
    var jar = blobH();
    var classBinaryName1 = stringH("abc");
    var classBinaryName2 = stringH("def");
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName1, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName2, isPure, args);
    assertThat(invoke1.hash())
        .isNotEqualTo(invoke2.hash());
  }

  @Test
  public void hash_of_invoke_with_different_is_pure_are_not_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure1 = boolH(true);
    var isPure2 = boolH(false);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure1, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure2, args);
    assertThat(invoke1.hash())
        .isNotEqualTo(invoke2.hash());
  }

  @Test
  public void hash_of_invoke_with_different_args_are_not_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args1 = combineH(list(intH(3)));
    var args2 = combineH(list(intH(4)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args1);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args2);
    assertThat(invoke1.hash())
        .isNotEqualTo(invoke2.hash());
  }

  @Test
  public void hashCode_of_invoke_with_equal_values_are_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args);
    assertThat(invoke1.hashCode())
        .isEqualTo(invoke2.hashCode());
  }

  @Test
  public void hashCode_of_invoke_with_different_jar_files_are_not_equal() {
    var jar1 = blobH(1);
    var jar2 = blobH(2);
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar1, classBinaryName, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar2, classBinaryName, isPure, args);

    assertThat(invoke1.hashCode())
        .isNotEqualTo(invoke2.hashCode());
  }

  @Test
  public void hashCode_of_invoke_with_different_class_binary_names_are_not_equal() {
    var jar = blobH();
    var classBinaryName1 = stringH("abc");
    var classBinaryName2 = stringH("def");
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName1, isPure, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName2, isPure, args);
    assertThat(invoke1.hashCode())
        .isNotEqualTo(invoke2.hashCode());
  }

  @Test
  public void hashCode_of_invoke_with_different_is_pure_are_not_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure1 = boolH(true);
    var isPure2 = boolH(false);
    var args = combineH(list(intH(3)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure1, args);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure2, args);
    assertThat(invoke1.hashCode())
        .isNotEqualTo(invoke2.hashCode());
  }

  @Test
  public void hashCode_of_invoke_with_different_args_are_not_equal() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args1 = combineH(list(intH(3)));
    var args2 = combineH(list(intH(4)));

    var invoke1 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args1);
    var invoke2 = invokeH(invokeCH(stringTH(), list(intTH())), jar, classBinaryName, isPure, args2);
    assertThat(invoke1.hashCode())
        .isNotEqualTo(invoke2.hashCode());
  }

  @Test
  public void invoke_can_be_read_back_by_hash() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));
    var invokeCH = invokeCH(stringTH(), list(intTH()));
    var invokeH = invokeH(invokeCH, jar, classBinaryName, isPure, args);
    assertThat(objDbOther().get(invokeH.hash()))
        .isEqualTo(invokeH);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    var jar = blobH();
    var classBinaryName = stringH();
    var isPure = boolH(true);
    var args = combineH(list(intH(3)));
    var invokeCH = invokeCH(stringTH(), list(intTH()));
    var invokeH = invokeH(invokeCH, jar, classBinaryName, isPure, args);
    var readInvokeH = (InvokeH) objDbOther().get(invokeH.hash());
    assertThat(readInvokeH.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readInvokeH.jarFile())
        .isEqualTo(jar);
    assertThat(readInvokeH.isPure())
        .isEqualTo(isPure);
    assertThat(readInvokeH.args())
        .isEqualTo(args);
  }

  @Test
  public void to_string() {
    var invokeH = invokeH(blobH(), stringH());
    assertThat(invokeH.toString())
        .isEqualTo("Invoke:Blob(???)@" + invokeH.hash());
  }
}
