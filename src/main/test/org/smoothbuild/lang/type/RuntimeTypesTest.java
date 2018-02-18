package org.smoothbuild.lang.type;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.collect.ImmutableMap;

public class RuntimeTypesTest {
  private HashedDb hashedDb;
  private TypesDb typesDb;
  private RuntimeTypes runtimeTypes;
  private StructType type;

  @Before
  public void before() {
    given(hashedDb = new HashedDb());
    given(typesDb = new TypesDb(hashedDb));
    given(runtimeTypes = new RuntimeTypes(typesDb));
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
  public void has_nothing_type() throws Exception {
    when(() -> runtimeTypes.hasType("Nothing"));
    thenReturned(true);
  }

  @Test
  public void has_file_type() throws Exception {
    when(() -> runtimeTypes.hasType("File"));
    thenReturned(true);
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
    when(() -> runtimeTypes.withName("String"));
    thenReturned(typesDb.string());
  }

  @Test
  public void blob_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes.withName("Blob"));
    thenReturned(typesDb.blob());
  }

  @Test
  public void nothing_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes.withName("Nothing"));
    thenReturned(typesDb.nothing());
  }

  @Test
  public void file_type_can_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes.withName("File"));
    thenReturned(typesDb.file());
  }

  @Test
  public void custom_struct_type_can_be_retrieved_by_name() throws Exception {
    given(type = runtimeTypes.struct("MyStruct", ImmutableMap.of("field", typesDb.string())));
    when(() -> runtimeTypes.withName("MyStruct"));
    thenReturned(type);
  }

  @Test
  public void reusing_struct_name_causes_exception() throws Exception {
    given(type = runtimeTypes.struct("MyStruct", ImmutableMap.of("field", typesDb.string())));
    when(() -> runtimeTypes.struct("MyStruct", ImmutableMap.of()));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void reusing_basic_type_name_as_struct_name_causes_exception() throws Exception {
    when(() -> runtimeTypes.struct("String", ImmutableMap.of()));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void type_type_can_not_be_retrieved_by_name() throws Exception {
    when(() -> runtimeTypes.withName("Type"));
    thenThrown(IllegalStateException.class);
  }
}
