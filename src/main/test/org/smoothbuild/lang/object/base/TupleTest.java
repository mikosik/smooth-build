package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class TupleTest extends TestingContext {
  @Test
  public void creating_struct_with_less_fields_than_specified_in_its_type_causes_exception() {
    assertCall(() -> struct(personType(), List.of(string("John"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_struct_with_more_fields_than_specified_in_its_type_causes_exception() {
    assertCall(() -> struct(personType(), List.of(string("John"), string("Doe"), string("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_field_to_null_throws_exception() {
    assertCall(() -> struct(personType(), List.of(string("John"), null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_field_to_object_of_wrong_type_throws_exception() {
    Array array = arrayBuilder(stringType()).build();
    assertCall(() -> struct(personType(), List.of(string("John"), array)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void type_of_person_struct_is_person() {
    Tuple person = johnDoePerson();
    assertThat(person.type())
        .isEqualTo(personType());
  }

  @Test
  public void field_contains_object_passed_to_builder() {
    Tuple person = johnDoePerson();
    assertThat(person.type())
        .isEqualTo(personType());
    assertThat(person.get(0))
        .isEqualTo(string("John"));
  }

  @Test
  public void reading_fields_with_negative_index_throws_exception() {
    Tuple person = johnDoePerson();
    assertCall(() -> person.get(-1))
        .throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void reading_fields_with_index_greater_than_max_index_throws_exception() {
    Tuple person = johnDoePerson();
    assertCall(() -> person.get(2))
        .throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void super_object_is_equal_to_first_field() {
    Tuple person = johnDoePerson();
    assertThat(person.superObject())
        .isEqualTo(string("John"));
  }

  @Test
  public void super_object_is_null_when_struct_type_has_no_fields() {
    Tuple tuple = struct(emptyType(), List.of());
    assertThat(tuple.superObject())
        .isNull();
  }

  @Test
  public void struct_hash_is_different_of_its_field_hash() {
    Tuple person = johnDoePerson();
    assertThat(person.hash())
        .isNotEqualTo(person.get(0).hash());
  }

  @Test
  public void structs_with_equal_fields_are_equal() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = johnDoePerson();
    assertThat(person1)
        .isEqualTo(person2);
  }

  @Test
  public void structs_with_one_field_different_are_not_equal() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = struct(personType(), List.of(string("John"), string("Doe2")));

    assertThat(person1)
        .isNotEqualTo(person2);
  }

  @Test
  public void structs_with_equal_fields_have_equal_hashes() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = johnDoePerson();
    assertThat(person1.hash())
        .isEqualTo(person2.hash());
  }

  @Test
  public void structs_with_different_field_have_different_hashes() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = struct(personType(), List.of(string("John"), string("Doe2")));
    assertThat(person1.hash())
        .isNotEqualTo(person2.hash());
  }

  @Test
  public void structs_with_equal_fields_have_equal_hash_codes() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = johnDoePerson();
    assertThat(person1.hashCode())
        .isEqualTo(person2.hashCode());
  }

  @Test
  public void structs_with_different_field_have_different_hash_codes() {
    Tuple person1 = johnDoePerson();
    Tuple person2 = struct(personType(), List.of(string("John"), string("Doe2")));
        assertThat(person1.hashCode())
            .isNotEqualTo(person2.hashCode());
  }

  @Test
  public void struct_can_be_read_by_hash() {
    Tuple person = johnDoePerson();
    assertThat(objectDbOther().get(person.hash()))
        .isEqualTo(person);
  }

  @Test
  public void struct_read_by_hash_have_equal_fields() {
    Tuple person = johnDoePerson();
    Tuple personRead = (Tuple) objectDbOther().get(person.hash());
    assertThat(personRead.get(0))
        .isEqualTo(person.get(0));
    assertThat(personRead.get(1))
        .isEqualTo(person.get(1));
  }

  @Test
  public void to_string() {
    Tuple person = johnDoePerson();
    assertThat(person.toString())
        .isEqualTo("TUPLE(...):" + person.hash());
  }

  private Tuple johnDoePerson() {
    return struct(personType(), List.of(string("John"), string("Doe")));
  }
}
