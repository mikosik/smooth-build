package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.StructBuilder;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class StructTest {
  private ValuesDb valuesDb;
  private HashCode hash;
  private SString firstName;
  private SString lastName;
  private StructBuilder builder;
  private Struct person;
  private Struct person2;

  @Before
  public void before() {
    valuesDb = memoryValuesDb();
  }

  @Test
  public void setting_field_to_null_throws_exception() throws Exception {
    when(() -> valuesDb.structBuilder(personType()).set("firstName", null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_of_person_struct_is_person() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType()).set("firstName", firstName).set("lastName",
        lastName).build());
    when(() -> person.type());
    thenReturned(personType());
  }

  @Test
  public void field_contains_value_passed_to_builder() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType()).set("firstName", firstName).set("lastName",
        lastName).build());
    when(() -> person.get("firstName"));
    thenReturned(firstName);
  }

  @Test
  public void reading_nonexistent_fields_throws_exception() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType()).set("firstName", firstName).set("lastName",
        lastName).build());
    when(() -> person.get("nonexistent"));
    thenThrown(exception(new IllegalArgumentException("nonexistent")));
  }

  @Test
  public void build_throws_exception_when_not_all_fields_are_set() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(builder = valuesDb.structBuilder(personType()).set("firstName", firstName));
    when(() -> builder.build());
    thenThrown(exception(new IllegalStateException("Field lastName hasn't been specified.")));
  }

  @Test
  public void struct_hash_is_different_of_its_field_hash() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType()).set("firstName", firstName).set("lastName",
        lastName).build());
    when(() -> person.hash());
    thenReturned(not((person.get("firstName")).hash()));
  }

  @Test
  public void structs_with_equal_fields_are_equal() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    when(() -> valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    thenReturned(valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
  }

  @Test
  public void structs_with_one_field_different_are_not_equal() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    when(() -> valuesDb.structBuilder(personType()).set("firstName", firstName).set("lastName",
        lastName).build());
    thenReturned(not(valuesDb.structBuilder(personType())
        .set("firstName", valuesDb.string("different")).set("lastName", lastName).build()));
  }

  @Test
  public void structs_with_equal_fields_have_equal_hashes() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    given(person2 = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    when(person.hash());
    thenReturned(person2.hash());
  }

  @Test
  public void structs_with_different_field_have_different_hashes() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    given(person2 = valuesDb.structBuilder(personType())
        .set("firstName", valuesDb.string("different")).set("lastName", lastName).build());
    when(person.hash());
    thenReturned(not(person2.hash()));
  }

  @Test
  public void structs_with_equal_fields_have_equal_hash_codes() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    given(person2 = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    when(person.hashCode());
    thenReturned(person2.hashCode());
  }

  @Test
  public void structs_with_different_field_have_different_hash_codes()
      throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    given(person2 = valuesDb.structBuilder(personType())
        .set("firstName", valuesDb.string("different")).set("lastName", lastName).build());
    when(person.hashCode());
    thenReturned(not(person2.hashCode()));
  }

  @Test
  public void struct_can_be_read_by_hash() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    when(valuesDb.read(personType(), person.hash()));
    thenReturned(person);
  }

  @Test
  public void struct_read_by_hash_have_equal_fields() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    when(person2 = valuesDb.read(personType(), person.hash()));
    thenEqual(person2.get("firstName"), person.get("firstName"));
    thenEqual(person2.get("lastName"), person.get("lastName"));
  }

  @Test
  public void to_string() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(person = valuesDb.structBuilder(personType())
        .set("firstName", firstName).set("lastName", lastName).build());
    when(person).toString();
    thenReturned("Person(firstName=John, lastName=Doe)");
  }

  @Test
  public void reading_not_stored_struct_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    when(() -> valuesDb.read(personType(), hash));
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }

  private static StructType personType() {
    Type string = new TypeSystem().string();
    return new StructType("Person", ImmutableMap.of("firstName", string, "lastName", string));
  }
}
