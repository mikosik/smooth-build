package org.smoothbuild.db.record.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.truth.Truth;

public class TupleTest extends TestingContext {
  @Test
  public void creating_tuple_with_less_elements_than_specified_in_its_spec_causes_exception() {
    assertCall(() -> tuple(personSpec(), List.of(string("John"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_tuple_with_more_elements_than_specified_in_its_spec_causes_exception() {
    assertCall(() -> tuple(personSpec(), List.of(string("John"), string("Doe"), string("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_element_to_null_throws_exception() {
    assertCall(() -> tuple(personSpec(), List.of(string("John"), null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_element_to_object_of_wrong_spec_throws_exception() {
    Array array = arrayBuilder(stringSpec()).build();
    assertCall(() -> tuple(personSpec(), List.of(string("John"), array)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void spec_of_person_tuple_is_person() {
    Tuple person = johnDoePerson();
    assertThat(person.spec())
        .isEqualTo(personSpec());
  }

  @Test
  public void element_contains_object_passed_to_builder() {
    Tuple person = johnDoePerson();
    assertThat(person.spec())
        .isEqualTo(personSpec());
    assertThat(person.get(0))
        .isEqualTo(string("John"));
  }

  @Test
  public void reading_elements_with_negative_index_throws_exception() {
    Tuple person = johnDoePerson();
    assertCall(() -> person.get(-1))
        .throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void reading_elements_with_index_greater_than_max_index_throws_exception() {
    Tuple person = johnDoePerson();
    assertCall(() -> person.get(2))
        .throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void super_object_is_equal_to_first_element() {
    Tuple person = johnDoePerson();
    assertThat(person.superObject())
        .isEqualTo(string("John"));
  }

  @Test
  public void super_object_is_null_when_tuple_spec_has_no_elements() {
    Tuple tuple = tuple(emptySpec(), List.of());
    assertThat(tuple.superObject())
        .isNull();
  }

  @Test
  public void tuple_hash_is_different_of_its_element_hash() {
    Tuple person = johnDoePerson();
    assertThat(person.hash())
        .isNotEqualTo(person.get(0).hash());
  }

  @Test
  public void tuples_with_equal_elements_are_equal() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = johnDoePerson();
    assertThat(person1)
        .isEqualTo(person2);
  }

  @Test
  public void tuples_with_one_element_different_are_not_equal() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = tuple(personSpec(), List.of(string("John"), string("Doe2")));

    assertThat(person1)
        .isNotEqualTo(person2);
  }

  @Test
  public void tuples_with_equal_elements_have_equal_hashes() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = johnDoePerson();
    assertThat(person1.hash())
        .isEqualTo(person2.hash());
  }

  @Test
  public void tuples_with_different_element_have_different_hashes() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = tuple(personSpec(), List.of(string("John"), string("Doe2")));
    assertThat(person1.hash())
        .isNotEqualTo(person2.hash());
  }

  @Test
  public void tuples_with_equal_elements_have_equal_hash_codes() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = johnDoePerson();
    assertThat(person1.hashCode())
        .isEqualTo(person2.hashCode());
  }

  @Test
  public void tuples_with_different_element_have_different_hash_codes() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = tuple(personSpec(), List.of(string("John"), string("Doe2")));
        assertThat(person1.hashCode())
            .isNotEqualTo(person2.hashCode());
  }

  @Test
  public void tuple_can_be_read_by_hash() {
    Tuple person = johnDoePerson();
    Truth.assertThat(recordDbOther().get(person.hash()))
        .isEqualTo(person);
  }

  @Test
  public void tuple_read_by_hash_have_equal_elements() {
    Tuple person = johnDoePerson();
    Tuple personRead = (Tuple) recordDbOther().get(person.hash());
    assertThat(personRead.get(0))
        .isEqualTo(person.get(0));
    assertThat(personRead.get(1))
        .isEqualTo(person.get(1));
  }

  @Test
  public void to_string() {
    Tuple person = johnDoePerson();
    assertThat(person.toString())
        .isEqualTo("""
            {"John","Doe"}:""" + person.hash());
  }

  private Tuple johnDoePerson() {
    return tuple(personSpec(), List.of(string("John"), string("Doe")));
  }
}
