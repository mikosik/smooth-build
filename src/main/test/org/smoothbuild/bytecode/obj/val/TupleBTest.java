package org.smoothbuild.bytecode.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.Lists;

public class TupleBTest extends TestingContext {
  @Test
  public void polymorphic_tuple_is_forbidden() {
    assertCall(() -> objDb().tuple(tupleTB(varB("A")), list()))
        .throwsException(new IllegalArgumentException(
            "Cannot create tuple object with polymorphic type `{A}`."));
  }

  @Test
  public void creating_tuple_with_less_items_than_specified_in_its_type_causes_exception() {
    assertCall(() -> objDb().tuple(personTB(), list(stringB("John"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_tuple_with_item_with_different_type_than_specified_in_tuple_type_causes_exception() {
    assertCall(() -> objDb().tuple(personTB(), list(stringB(), intB())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_tuple_with_item_type_being_subtype_of_specified_in_tupleT_causes_exc() {
    var tupleT = tupleTB(arrayTB(intTB()));
    assertCall(() -> objDb().tuple(tupleT, Lists.list(arrayB(nothingTB()))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_tuple_with_more_items_than_specified_in_its_type_causes_exception() {
    assertCall(() -> objDb().tuple(
        personTB(), list(stringB("John"), stringB("Doe"), stringB("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_item_to_null_throws_exception() {
    assertCall(() -> objDb().tuple(personTB(), list(stringB("John"), null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_item_to_object_of_wrong_type_throws_exception() {
    assertCall(() -> objDb().tuple(personTB(), list(stringB("John"), intB(123))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void type_of_person_tuple_is_person_type() {
    TupleB person = johnDoePerson();
    assertThat(person.cat())
        .isEqualTo(personTB());
  }

  @Test
  public void item_contains_object_passed_to_builder() {
    TupleB person = johnDoePerson();
    assertThat(person.cat())
        .isEqualTo(personTB());
    assertThat(person.get(0))
        .isEqualTo(stringB("John"));
  }

  @Test
  public void reading_items_with_negative_index_throws_exception() {
    TupleB person = johnDoePerson();
    assertCall(() -> person.get(-1))
        .throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void reading_items_with_index_greater_than_max_index_throws_exception() {
    TupleB person = johnDoePerson();
    assertCall(() -> person.get(2))
        .throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void tuple_hash_is_different_of_its_item_hash() {
    TupleB person = johnDoePerson();
    assertThat(person.hash())
        .isNotEqualTo(person.get(0).hash());
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<TupleB> {
    @Override
    protected List<TupleB> equalValues() {
      return list(
          tupleB(intB(7), stringB("abc")),
          tupleB(intB(7), stringB("abc"))
      );
    }

    @Override
    protected List<TupleB> nonEqualValues() {
      return list(
          tupleB(),
          tupleB(intB(0)),
          tupleB(intB(7)),
          tupleB(intB(0), intB(0)),
          tupleB(intB(0), intB(7)),
          tupleB(intB(7), intB(0))
      );
    }
  }

  @Test
  public void tuples_can_be_read_by_hash() {
    TupleB person = johnDoePerson();
    assertThat(objDbOther().get(person.hash()))
        .isEqualTo(person);
  }

  @Test
  public void tuples_read_by_hash_have_equal_items() {
    TupleB person = johnDoePerson();
    TupleB personRead = (TupleB) objDbOther().get(person.hash());
    assertThat(personRead.get(0))
        .isEqualTo(person.get(0));
    assertThat(personRead.get(1))
        .isEqualTo(person.get(1));
  }

  @Test
  public void to_string() {
    TupleB person = johnDoePerson();
    assertThat(person.toString())
        .isEqualTo("""
            {"John","Doe"}@""" + person.hash());
  }

  private TupleB johnDoePerson() {
    return tupleB(stringB("John"), stringB("Doe"));
  }
}
