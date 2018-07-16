package org.smoothbuild.lang.runtime;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Sets.set;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.TestingTypesDb;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;

@RunWith(QuackeryRunner.class)
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

  @Quackery
  public static Suite replace_core_type() throws Exception {
    RuntimeTypes rt = new RuntimeTypes(new TestingTypesDb());
    Type a = rt.generic("a");
    Type b = rt.generic("b");
    Type string = rt.string();
    Type arrayA = rt.array(a);
    Type arrayB = rt.array(b);
    Type arrayString = rt.array(string);
    Type array2A = rt.array(arrayA);
    Type array2B = rt.array(arrayB);
    Type array2String = rt.array(arrayString);
    Type array3A = rt.array(array2A);
    Type array3B = rt.array(array2B);
    Type array3String = rt.array(array2String);
    Type array4A = rt.array(array3A);
    Type array4B = rt.array(array3B);
    Type array4String = rt.array(array3String);

    return suite("replaceCoreType").addAll(asList(
        assertReplaceCoreType(rt, a, a, a),
        assertReplaceCoreType(rt, a, b, b),
        assertReplaceCoreType(rt, a, string, string),

        assertReplaceCoreType(rt, a, arrayA, arrayA),
        assertReplaceCoreType(rt, a, arrayB, arrayB),
        assertReplaceCoreType(rt, a, arrayString, arrayString),

        assertReplaceCoreType(rt, a, array2A, array2A),
        assertReplaceCoreType(rt, a, array2B, array2B),
        assertReplaceCoreType(rt, a, array2String, array2String),

        //

        assertReplaceCoreType(rt, arrayA, a, arrayA),
        assertReplaceCoreType(rt, arrayA, b, arrayB),
        assertReplaceCoreType(rt, arrayA, string, arrayString),

        assertReplaceCoreType(rt, arrayA, arrayA, array2A),
        assertReplaceCoreType(rt, arrayA, arrayB, array2B),
        assertReplaceCoreType(rt, arrayA, arrayString, array2String),

        assertReplaceCoreType(rt, arrayA, array2A, array3A),
        assertReplaceCoreType(rt, arrayA, array2B, array3B),
        assertReplaceCoreType(rt, arrayA, array2String, array3String),

        //

        assertReplaceCoreType(rt, array2A, a, array2A),
        assertReplaceCoreType(rt, array2A, b, array2B),
        assertReplaceCoreType(rt, array2A, string, array2String),

        assertReplaceCoreType(rt, array2A, arrayA, array3A),
        assertReplaceCoreType(rt, array2A, arrayB, array3B),
        assertReplaceCoreType(rt, array2A, arrayString, array3String),

        assertReplaceCoreType(rt, array2A, array2A, array4A),
        assertReplaceCoreType(rt, array2A, array2B, array4B),
        assertReplaceCoreType(rt, array2A, array2String, array4String)));
  }

  private static Case assertReplaceCoreType(RuntimeTypes rt, Type type, Type newCoreType,
      Type expected) {
    String name =
        "replaceCoreType(" + type.name() + "," + newCoreType.name() + ") == " + expected.name();
    return newCase(name, () -> assertEquals(expected, rt.replaceCoreType(type, newCoreType)));
  }

  private Type array(Type type) {
    return runtimeTypes.array(type);
  }
}
