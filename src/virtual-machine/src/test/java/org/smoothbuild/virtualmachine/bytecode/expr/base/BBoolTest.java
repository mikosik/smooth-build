package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BBoolTest extends TestingVirtualMachine {
  @Test
  public void type_of_bool_is_bool_type() throws Exception {
    assertThat(bBool(true).kind()).isEqualTo(bBoolType());
  }

  @Test
  public void to_j_returns_java_true_from_true_bool() throws Exception {
    var bool = bBool(true);
    assertThat(bool.toJavaBoolean()).isTrue();
  }

  @Test
  public void javlue_returns_java_false_from_false_bool() throws Exception {
    var bool = bBool(false);
    assertThat(bool.toJavaBoolean()).isFalse();
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BBool> {
    @Override
    protected List<BBool> equalExprs() throws BytecodeException {
      return list(bBool(true), bBool(true));
    }

    @Override
    protected List<BBool> nonEqualExprs() throws BytecodeException {
      return list(bBool(true), bBool(false));
    }
  }

  @Test
  public void bool_can_be_read_back_by_hash() throws Exception {
    var bool = bBool(true);
    var hash = bool.hash();
    assertThat(exprDbOther().get(hash)).isEqualTo(bool);
  }

  @Test
  public void bool_read_back_by_hash_has_same_to_j() throws Exception {
    var bool = bBool(true);
    assertThat(((BBool) exprDbOther().get(bool.hash())).toJavaBoolean()).isTrue();
  }

  @Test
  public void to_string_contains_value() throws Exception {
    var bool = bBool(true);
    assertThat(bool.toString()).isEqualTo("true@" + bool.hash());
  }
}