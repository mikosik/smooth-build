package org.smoothbuild.lang.object.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

public class StructTest extends TestingContext {
  private SString firstName;
  private SString lastName;
  private Array array;
  private StructBuilder builder;
  private Struct person;
  private Struct person2;
  private Struct struct;

  @Test
  public void setting_nonexistent_field_throws_exception() throws Exception {
    when(() -> structBuilder(personType()).set("unknown", string("abc")));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void setting_field_to_null_throws_exception() throws Exception {
    when(() -> structBuilder(personType()).set("firstName", null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void setting_field_to_object_of_wrong_type_throws_exception() throws Exception {
    given(array = arrayBuilder(stringType()).build());
    when(() -> structBuilder(personType()).set("firstName", array));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void type_of_person_struct_is_person() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(() -> person.type());
    thenReturned(personType());
  }

  @Test
  public void field_contains_object_passed_to_builder() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName).
        build());
    when(() -> person.get("firstName"));
    thenReturned(firstName);
  }

  @Test
  public void reading_nonexistent_fields_throws_exception() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(() -> person.get("nonexistent"));
    thenThrown(exception(new IllegalArgumentException("nonexistent")));
  }

  @Test
  public void build_throws_exception_when_not_all_fields_are_set() throws Exception {
    given(firstName = string("John"));
    given(builder = structBuilder(personType()).set("firstName", firstName));
    when(() -> builder.build());
    thenThrown(exception(new IllegalStateException("Field lastName hasn't been specified.")));
  }

  @Test
  public void super_object_is_equal_to_first_field() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(() -> person.superObject());
    thenReturned(firstName);
  }

  @Test
  public void super_object_is_null_when_struct_type_has_no_fields() throws Exception {
    given(struct = structBuilder(structType("MyStruct", list())).build());
    when(() -> struct.superObject());
    thenReturned(null);
  }

  @Test
  public void struct_hash_is_different_of_its_field_hash() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(() -> person.hash());
    thenReturned(not((person.get("firstName")).hash()));
  }

  @Test
  public void structs_with_equal_fields_are_equal() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    when(() -> structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    thenReturned(structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
  }

  @Test
  public void structs_with_one_field_different_are_not_equal() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    when(() -> structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    thenReturned(not(structBuilder(personType())
        .set("firstName", string("different"))
        .set("lastName", lastName)
        .build()));
  }

  @Test
  public void structs_with_equal_fields_have_equal_hashes() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    given(person2 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(person.hash());
    thenReturned(person2.hash());
  }

  @Test
  public void structs_with_different_field_have_different_hashes() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    given(person2 = structBuilder(personType())
        .set("firstName", string("different"))
        .set("lastName", lastName)
        .build());
    when(person.hash());
    thenReturned(not(person2.hash()));
  }

  @Test
  public void structs_with_equal_fields_have_equal_hash_codes() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    given(person2 = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(person.hashCode());
    thenReturned(person2.hashCode());
  }

  @Test
  public void structs_with_different_field_have_different_hash_codes()
      throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    given(person2 = structBuilder(personType())
        .set("firstName", string("different"))
        .set("lastName", lastName)
        .build());
    when(person.hashCode());
    thenReturned(not(person2.hashCode()));
  }

  @Test
  public void struct_can_be_read_by_hash() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(() -> objectDbOther().get(person.hash()));
    thenReturned(person);
  }

  @Test
  public void struct_read_by_hash_have_equal_fields() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(person2 = (Struct) objectDbOther().get(person.hash()));
    thenEqual(person2.get("firstName"), person.get("firstName"));
    thenEqual(person2.get("lastName"), person.get("lastName"));
  }

  @Test
  public void to_string() throws Exception {
    given(firstName = string("John"));
    given(lastName = string("Doe"));
    given(person = structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build());
    when(() -> person.toString());
    thenReturned("Person(...):" + person.hash());
  }
}
