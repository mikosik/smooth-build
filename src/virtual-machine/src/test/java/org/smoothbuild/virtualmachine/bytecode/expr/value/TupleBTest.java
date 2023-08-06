package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class TupleBTest extends TestVirtualMachine {
  @Test
  public void setting_element_to_null_throws_exception() {
    assertCall(() -> exprDb().tuple(list(stringB("John"), null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_of_person_tuple_is_person_type() throws Exception {
    TupleB person = johnDoePerson();
    assertThat(person.category()).isEqualTo(personTB());
  }

  @Test
  public void element_contains_object_passed_to_builder() throws Exception {
    TupleB person = johnDoePerson();
    assertThat(person.category()).isEqualTo(personTB());
    assertThat(person.get(0)).isEqualTo(stringB("John"));
  }

  @Test
  public void reading_elements_with_negative_index_throws_exception() throws Exception {
    TupleB person = johnDoePerson();
    assertCall(() -> person.get(-1)).throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void reading_elements_with_index_greater_than_max_index_throws_exception()
      throws Exception {
    TupleB person = johnDoePerson();
    assertCall(() -> person.get(2)).throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void tuple_hash_is_different_of_its_element_hash() throws Exception {
    TupleB person = johnDoePerson();
    assertThat(person.hash()).isNotEqualTo(person.get(0).hash());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<TupleB> {
    @Override
    protected java.util.List<TupleB> equalExprs() throws BytecodeException {
      return list(tupleB(intB(7), stringB("abc")), tupleB(intB(7), stringB("abc")));
    }

    @Override
    protected java.util.List<TupleB> nonEqualExprs() throws BytecodeException {
      return list(
          tupleB(),
          tupleB(intB(0)),
          tupleB(intB(7)),
          tupleB(intB(0), intB(0)),
          tupleB(intB(0), intB(7)),
          tupleB(intB(7), intB(0)));
    }
  }

  @Test
  public void tuples_can_be_read_by_hash() throws Exception {
    TupleB person = johnDoePerson();
    assertThat(exprDbOther().get(person.hash())).isEqualTo(person);
  }

  @Test
  public void tuples_read_by_hash_have_equal_elements() throws Exception {
    TupleB person = johnDoePerson();
    TupleB personRead = (TupleB) exprDbOther().get(person.hash());
    assertThat(personRead.get(0)).isEqualTo(person.get(0));
    assertThat(personRead.get(1)).isEqualTo(person.get(1));
  }

  @Test
  public void to_string() throws Exception {
    TupleB person = johnDoePerson();
    assertThat(person.toString()).isEqualTo("""
            {"John","Doe"}@""" + person.hash());
  }

  private TupleB johnDoePerson() throws Exception {
    return tupleB(stringB("John"), stringB("Doe"));
  }
}
