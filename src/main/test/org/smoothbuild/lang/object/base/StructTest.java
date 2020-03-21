package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class StructTest extends TestingContext {
  @Test
  public void setting_nonexistent_field_throws_exception() {
    StructBuilder builder = structBuilder(personType());
    SString sstring = string("abc");
    assertCall(() -> builder.set("unknown", sstring))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_field_to_null_throws_exception() {
    StructBuilder builder = structBuilder(personType());
    assertCall(() -> builder.set("firstName", null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_field_to_object_of_wrong_type_throws_exception() {
    Array array = arrayBuilder(stringType()).build();
    StructBuilder builder = structBuilder(personType());
    assertCall(() -> builder.set("firstName", array))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void type_of_person_struct_is_person() {
    Struct person = structBuilder(personType())
        .set("firstName", string("John"))
        .set("lastName", string("Doe"))
        .build();
    assertThat(person.type())
        .isEqualTo(personType());
  }

  @Test
  public void field_contains_object_passed_to_builder() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertThat(person.get("firstName"))
        .isEqualTo(firstName);
  }

  @Test
  public void reading_nonexistent_fields_throws_exception() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertCall(() -> person.get("nonexistent"))
        .throwsException(new IllegalArgumentException("nonexistent"));
  }

  @Test
  public void build_throws_exception_when_not_all_fields_are_set() {
    StructBuilder builder = structBuilder(personType()).set("firstName", string("John"));
    assertCall(builder::build)
        .throwsException(new IllegalStateException("Field lastName hasn't been specified."));
  }

  @Test
  public void super_object_is_equal_to_first_field() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertThat(person.superObject())
        .isEqualTo(firstName);
  }

  @Test
  public void super_object_is_null_when_struct_type_has_no_fields() {
    Struct struct = structBuilder(structType("MyStruct", list())).build();
    assertThat(struct.superObject())
        .isNull();
  }

  @Test
  public void struct_hash_is_different_of_its_field_hash() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertThat(person.hash())
        .isNotEqualTo(person.get("firstName").hash());
  }

  @Test
  public void structs_with_equal_fields_are_equal() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person1 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    Struct person2 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertThat(person1)
        .isEqualTo(person2);
  }

  @Test
  public void structs_with_one_field_different_are_not_equal() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person1 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    Struct person2 = structBuilder(personType())
        .set("firstName", string("different"))
        .set("lastName", lastName)
        .build();
    assertThat(person1)
        .isNotEqualTo(person2);
  }

  @Test
  public void structs_with_equal_fields_have_equal_hashes() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person1 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    Struct person2 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertThat(person1.hash())
        .isEqualTo(person2.hash());
  }

  @Test
  public void structs_with_different_field_have_different_hashes() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person1 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    Struct person2 = structBuilder(personType())
        .set("firstName", string("different"))
        .set("lastName", lastName)
        .build();
    assertThat(person1.hash())
        .isNotEqualTo(person2.hash());
  }

  @Test
  public void structs_with_equal_fields_have_equal_hash_codes() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person1 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    Struct person2 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertThat(person1.hashCode())
        .isEqualTo(person2.hashCode());
  }

  @Test
  public void structs_with_different_field_have_different_hash_codes() {
        SString firstName = string("John");
        SString lastName = string("Doe");
        Struct person1 = structBuilder(personType())
            .set("firstName", firstName)
            .set("lastName", lastName)
            .build();
        Struct person2 = structBuilder(personType())
            .set("firstName", string("different"))
            .set("lastName", lastName)
            .build();
        assertThat(person1.hashCode())
            .isNotEqualTo(person2.hashCode());
  }

  @Test
  public void struct_can_be_read_by_hash() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertThat(objectDbOther().get(person.hash()))
        .isEqualTo(person);
  }

  @Test
  public void struct_read_by_hash_have_equal_fields() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    Struct personRead = (Struct) objectDbOther().get(person.hash());
    assertThat(personRead.get("firstName"))
        .isEqualTo(person.get("firstName"));
    assertThat(personRead.get("lastName"))
        .isEqualTo(person.get("lastName"));
  }

  @Test
  public void to_string() {
    SString firstName = string("John");
    SString lastName = string("Doe");
    Struct person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
    assertThat(person.toString())
        .isEqualTo("Person(...):" + person.hash());
  }
}
