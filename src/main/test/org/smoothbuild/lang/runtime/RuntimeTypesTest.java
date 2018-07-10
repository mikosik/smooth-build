package org.smoothbuild.lang.runtime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.message.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Sets.set;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.function.Field;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;

public class RuntimeTypesTest {
  private HashedDb hashedDb;
  private TypesDb typesDb;
  private RuntimeTypes runtimeTypes;
  private StructType type;
  private ArrayType arrayType;
  private Type a;
  private Type string;
  private Type array;
  private Type blob;
  private Type b;
  private Type array2;

  @Before
  public void before() {
    given(hashedDb = new TestingHashedDb());
    given(typesDb = new TypesDb(hashedDb));
    given(runtimeTypes = new RuntimeTypes(typesDb));
  }

  @Test
  public void names_returns_unmodifiable_set() throws Exception {
    when(() -> runtimeTypes.names().remove("abc"));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void names_returns_all_basic_types_initially() throws Exception {
    when(() -> runtimeTypes.names());
    thenReturned(set(
        typesDb.string().name(),
        typesDb.blob().name()));
  }

  @Test
  public void names_does_not_contain_name_of_array_type_that_was_queried_before() throws Exception {
    given(arrayType = runtimeTypes.array(runtimeTypes.string()));
    when(() -> runtimeTypes.names());
    thenReturned(not(hasItem(arrayType.name())));
  }

  @Test
  public void names_contain_name_of_struct_that_was_added_before() throws Exception {
    given(runtimeTypes).struct("MyStruct", list());
    when(() -> runtimeTypes.names());
    thenReturned(hasItem("MyStruct"));
  }

  @Test
  public void name_to_type_map_is_unmodifiable() throws Exception {
    when(() -> runtimeTypes.nameToTypeMap().remove("abc"));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void name_to_type_map_contains_basic_types_initially() throws Exception {
    when(() -> runtimeTypes.nameToTypeMap().keySet());
    thenReturned(set(
        typesDb.string().name(),
        typesDb.blob().name()));
  }

  @Test
  public void names_to_type_mape_contains_name_of_struct_that_was_added_before() throws Exception {
    given(runtimeTypes).struct("MyStruct", list());
    when(() -> runtimeTypes.nameToTypeMap().keySet());
    thenReturned(hasItem("MyStruct"));
  }

  @Test
  public void has_string_type() throws Exception {
    when(() -> runtimeTypes.hasType("String"));
    thenReturned(true);
  }

  @Test
  public void has_blob_type() throws Exception {
    when(() -> runtimeTypes.hasType("Blob"));
    thenReturned(true);
  }

  @Test
  public void has_not_generic_type() throws Exception {
    when(() -> runtimeTypes.hasType("a"));
    thenReturned(false);
  }

  @Test
  public void does_not_have_unknown_type() throws Exception {
    when(() -> runtimeTypes.hasType("Unknown"));
    thenReturned(false);
  }

  @Test
  public void does_not_have_array_of_string_type() throws Exception {
    when(() -> runtimeTypes.hasType("[String]"));
    thenReturned(false);
  }

  @Test
  public void string_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes.getType("String"));
    thenReturned(typesDb.string());
  }

  @Test
  public void blob_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes.getType("Blob"));
    thenReturned(typesDb.blob());
  }

  @Test
  public void generic_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes.getType("a"));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void custom_struct_type_can_be_retrieved_by_name() throws Exception {
    given(type = runtimeTypes.struct(
        "MyStruct", list(new Field(typesDb.string(), "field", unknownLocation()))));
    when(() -> runtimeTypes.getType("MyStruct"));
    thenReturned(type);
  }

  @Test
  public void reusing_struct_name_causes_exception() throws Exception {
    given(type = runtimeTypes.struct(
        "MyStruct", list(new Field(typesDb.string(), "field", unknownLocation()))));
    when(() -> runtimeTypes.struct("MyStruct", list()));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void reusing_basic_type_name_as_struct_name_causes_exception() throws Exception {
    when(() -> runtimeTypes.struct("String", list()));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void type_type_can_not_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes.getType("Type"));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void no_name_clash_between_concrete_type_and_that_concrete_type() throws Exception {
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(string, string));
    thenReturned(string);
  }

  @Test
  public void no_name_clash_between_array_of_concrete_type_and_that_concrete_type()
      throws Exception {
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(array(string), string));
    thenReturned(string);
  }

  @Test
  public void no_name_clash_between_concrete_type_and_array_of_that_concrete_type()
      throws Exception {
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(string, array(string)));
    thenReturned(array(string));
  }

  @Test
  public void no_name_clash_between_array_of_concrete_type_and_array_of_that_concrete_type()
      throws Exception {
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(array(string), array(string)));
    thenReturned(array(string));
  }

  @Test
  public void no_name_clash_between_different_concrete_types() throws Exception {
    given(blob = runtimeTypes.blob());
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(blob, string));
    thenReturned(string);
  }

  @Test
  public void no_name_clash_between_generic_type_and_concrete_type() throws Exception {
    given(a = runtimeTypes.generic("a"));
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(a, string));
    thenReturned(string);
  }

  @Test
  public void no_name_clash_between_concrete_type_and_generic_type() throws Exception {
    given(a = runtimeTypes.generic("a"));
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(string, a));
    thenReturned(a);
  }

  @Test
  public void no_name_clash_between_array_of_generic_type_and_concrete_type() throws Exception {
    given(array = array(runtimeTypes.generic("a")));
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(array, string));
    thenReturned(string);
  }

  @Test
  public void no_name_clash_between_concrete_type_and_array_of_generic_type() throws Exception {
    given(array = array(runtimeTypes.generic("a")));
    given(string = runtimeTypes.string());
    when(() -> runtimeTypes.fixNameClashIfExists(string, array));
    thenReturned(array);
  }

  @Test
  public void no_name_clash_between_generic_type_and_array_of_concrete_type() throws Exception {
    given(a = runtimeTypes.generic("a"));
    given(array = array(runtimeTypes.string()));
    when(() -> runtimeTypes.fixNameClashIfExists(a, array));
    thenReturned(array);
  }

  @Test
  public void no_name_clash_between_array_of_concrete_type_and_generic_type() throws Exception {
    given(array = array(runtimeTypes.string()));
    given(a = runtimeTypes.generic("a"));
    when(() -> runtimeTypes.fixNameClashIfExists(array, a));
    thenReturned(a);
  }

  @Test
  public void no_name_clash_between_generic_type_and_different_generic_type() throws Exception {
    given(a = runtimeTypes.generic("a"));
    given(b = runtimeTypes.generic("b"));
    when(() -> runtimeTypes.fixNameClashIfExists(a, b));
    thenReturned(b);
  }

  @Test
  public void no_name_clash_between_generic_type_and_array_of_different_generic_type()
      throws Exception {
    given(a = runtimeTypes.generic("a"));
    given(array = array(runtimeTypes.generic("b")));
    when(() -> runtimeTypes.fixNameClashIfExists(a, array));
    thenReturned(array);
  }

  @Test
  public void no_name_clash_between_array_of_generic_type_and_different_generic_type()
      throws Exception {
    given(array = array(runtimeTypes.generic("a")));
    given(b = runtimeTypes.generic("b"));
    when(() -> runtimeTypes.fixNameClashIfExists(array, b));
    thenReturned(b);
  }

  @Test
  public void no_name_clash_between_array_of_generic_type_and_array_of_different_generic_type()
      throws Exception {
    given(array = array(runtimeTypes.generic("a")));
    given(array2 = array(runtimeTypes.generic("b")));
    when(() -> runtimeTypes.fixNameClashIfExists(array, array2));
    thenReturned(array2);
  }

  @Test
  public void name_clash_between_generic_type_and_that_generic_type_is_fixed() throws Exception {
    given(a = runtimeTypes.generic("a"));
    when(() -> runtimeTypes.fixNameClashIfExists(a, a));
    thenReturned(runtimeTypes.generic("a'"));
  }

  @Test
  public void name_clash_between_generic_type_and_array_of_that_type_is_fixed() throws Exception {
    given(a = runtimeTypes.generic("a"));
    given(array = array(a));
    when(() -> runtimeTypes.fixNameClashIfExists(a, array));
    thenReturned(runtimeTypes.array(runtimeTypes.generic("a'")));
  }

  @Test
  public void name_clash_between_array_of_generic_type_and_that_generic_type_is_fixed()
      throws Exception {
    given(a = runtimeTypes.generic("a"));
    given(array = array(a));
    when(() -> runtimeTypes.fixNameClashIfExists(array, a));
    thenReturned(runtimeTypes.generic("a'"));
  }

  @Test
  public void name_clash_between_array_of_generic_type_and_array_of_that_generic_type_is_fixed()
      throws Exception {
    given(a = runtimeTypes.generic("a"));
    given(array = array(a));
    when(() -> runtimeTypes.fixNameClashIfExists(array, array));
    thenReturned(array(runtimeTypes.generic("a'")));
  }

  private Type array(Type type) {
    return runtimeTypes.array(type);
  }
}
