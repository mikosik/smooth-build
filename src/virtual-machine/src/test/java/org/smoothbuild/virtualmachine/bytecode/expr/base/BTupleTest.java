package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BTupleTest extends VmTestContext {
  @Test
  void setting_element_to_null_throws_exception() {
    assertCall(() -> bTuple(list(bString("John"), null)))
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
    protected List<BTuple> equalExprs() throws BytecodeException {
      return list(bTuple(bInt(7), bString("abc")), bTuple(bInt(7), bString("abc")));
    }

    @Override
    protected List<BTuple> nonEqualExprs() throws BytecodeException {
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
    assertThat(person.toString())
        .isEqualTo(
            """
        BTuple(
          hash = 73d8d48ae8b9dd946be996c3d2d0997dd3810e8cf3bff5b2e55da3be08e29f9e
          type = {String,String}
          elements = [
            BString(
              hash = 999a9f44d60b6c11a6d6739e78061314cb62142a71fbfd304367ee210283b369
              type = String
              value = "John"
            )
            BString(
              hash = a1e37c708997efd307bb7a76864a1965c8ea525e5c82ea2eddc0351bdbf06c05
              type = String
              value = "Doe"
            )
          ]
        )""");
  }

  private BTuple johnDoePerson() throws Exception {
    return bTuple(bString("John"), bString("Doe"));
  }
}
