package org.smoothbuild.lang.runtime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Sets.set;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.GenericArrayType;
import org.smoothbuild.lang.type.GenericType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.testing.TestingContext;

@RunWith(QuackeryRunner.class)
public class RuntimeTypesTest extends TestingContext {
  private StructType type;
  private ArrayType arrayType;
  private Type a;

  @Test
  public void names_returns_unmodifiable_set() throws Exception {
    when(() -> runtimeTypes().names().remove("abc"));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void names_returns_all_basic_types_initially() throws Exception {
    when(() -> runtimeTypes().names());
    thenReturned(set(
        boolType().name(),
        stringType().name(),
        blobType().name(),
        nothingType().name()));
  }

  @Test
  public void names_does_not_contain_name_of_array_type_that_was_queried_before() throws Exception {
    given(arrayType = runtimeTypes().array(runtimeTypes().string()));
    when(() -> runtimeTypes().names());
    thenReturned(not(hasItem(arrayType.name())));
  }

  @Test
  public void names_contain_name_of_struct_that_was_added_before() throws Exception {
    given(runtimeTypes()).struct("MyStruct", list());
    when(() -> runtimeTypes().names());
    thenReturned(hasItem("MyStruct"));
  }

  @Test
  public void name_to_type_map_is_unmodifiable() throws Exception {
    when(() -> runtimeTypes().nameToTypeMap().remove("abc"));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void name_to_type_map_contains_basic_types_initially() throws Exception {
    when(() -> runtimeTypes().nameToTypeMap().keySet());
    thenReturned(set(
        boolType().name(),
        stringType().name(),
        blobType().name(),
        nothingType().name()));
  }

  @Test
  public void names_to_type_mape_contains_name_of_struct_that_was_added_before() throws Exception {
    given(runtimeTypes()).struct("MyStruct", list());
    when(() -> runtimeTypes().nameToTypeMap().keySet());
    thenReturned(hasItem("MyStruct"));
  }

  @Test
  public void bool_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes().getType("Bool"));
    thenReturned(boolType());
  }

  @Test
  public void string_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes().getType("String"));
    thenReturned(stringType());
  }

  @Test
  public void blob_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes().getType("Blob"));
    thenReturned(blobType());
  }

  @Test
  public void nothing_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes().getType("Nothing"));
    thenReturned(nothingType());
  }

  @Test
  public void generic_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes().getType("a"));
    thenReturned(new GenericType("a"));
  }

  @Test
  public void custom_struct_type_can_be_retrieved_by_name() throws Exception {
    given(type = runtimeTypes().struct(
        "MyStruct", list(new Field(stringType(), "field", unknownLocation()))));
    when(() -> runtimeTypes().getType("MyStruct"));
    thenReturned(type);
  }

  @Test
  public void generic_array_type_can_be_created() throws Exception {
    given(a = runtimeTypes().getType("a"));
    when(() -> runtimeTypes().array(a));
    thenReturned(new GenericArrayType(new GenericType("a")));
  }

  @Test
  public void reusing_struct_name_causes_exception() throws Exception {
    given(type = runtimeTypes().struct(
        "MyStruct", list(new Field(stringType(), "field", unknownLocation()))));
    when(() -> runtimeTypes().struct("MyStruct", list()));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void reusing_basic_type_name_as_struct_name_causes_exception() throws Exception {
    when(() -> runtimeTypes().struct("String", list()));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void type_type_can_not_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes().getType("Type"));
    thenThrown(IllegalStateException.class);
  }
}
