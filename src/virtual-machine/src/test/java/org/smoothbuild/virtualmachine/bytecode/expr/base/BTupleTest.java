package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;

public class BTupleTest extends BytecodeTestContext {
  @Test
  void setting_element_to_null_throws_exception() {
    assertCall(() -> exprDb().newTuple(list(bString("John"), null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  void type_of_person_tuple_is_person_type() throws Exception {
    var person = johnDoePerson();
    assertThat(person.kind()).isEqualTo(bPersonType());
  }

  @Test
  void element_contains_object_passed_to_builder() throws Exception {
    var person = johnDoePerson();
    assertThat(person.kind()).isEqualTo(bPersonType());
    assertThat(person.get(0)).isEqualTo(bString("John"));
  }

  @Test
  void reading_elements_with_negative_index_throws_exception() throws Exception {
    var person = johnDoePerson();
    assertCall(() -> person.get(-1)).throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  void reading_elements_with_index_greater_than_max_index_throws_exception() throws Exception {
    var person = johnDoePerson();
    assertCall(() -> person.get(2)).throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  void tuple_hash_is_different_of_its_element_hash() throws Exception {
    var person = johnDoePerson();
    assertThat(person.hash()).isNotEqualTo(person.get(0).hash());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BTuple> {
    @Override
    protected java.util.List<BTuple> equalExprs() throws BytecodeException {
      return list(bTuple(bInt(7), bString("abc")), bTuple(bInt(7), bString("abc")));
    }

    @Override
    protected java.util.List<BTuple> nonEqualExprs() throws BytecodeException {
      return list(
          bTuple(),
          bTuple(bInt(0)),
          bTuple(bInt(7)),
          bTuple(bInt(0), bInt(0)),
          bTuple(bInt(0), bInt(7)),
          bTuple(bInt(7), bInt(0)));
    }
  }

  @Test
  void tuples_can_be_read_by_hash() throws Exception {
    var person = johnDoePerson();
    assertThat(exprDbOther().get(person.hash())).isEqualTo(person);
  }

  @Test
  void tuples_read_by_hash_have_equal_elements() throws Exception {
    var person = johnDoePerson();
    var personRead = (BTuple) exprDbOther().get(person.hash());
    assertThat(personRead.get(0)).isEqualTo(person.get(0));
    assertThat(personRead.get(1)).isEqualTo(person.get(1));
  }

  @Test
  void to_string() throws Exception {
    var person = johnDoePerson();
    assertThat(person.toString()).isEqualTo("""
            {"John","Doe"}@""" + person.hash());
  }

  private BTuple johnDoePerson() throws Exception {
    return bTuple(bString("John"), bString("Doe"));
  }
}
