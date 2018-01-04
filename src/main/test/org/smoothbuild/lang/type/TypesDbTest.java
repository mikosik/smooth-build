package org.smoothbuild.lang.type;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.collect.ImmutableMap;

public class TypesDbTest {
  private HashedDb hashedDb;
  private TypesDb typesDb;
  private ImmutableMap<String, Type> fields;
  private Type type;
  private Type type2;
  private ArrayType arrayType;
  private StructType structType;

  @Before
  public void before() {
    given(hashedDb = new HashedDb());
    given(typesDb = new TypesDb(hashedDb));
  }

  @Test
  public void struct_type_with_different_field_order_has_different_hash() throws Exception {
    given(type = typesDb.struct("Struct",
        ImmutableMap.of("a", typesDb.string(), "b", typesDb.string())));
    given(type2 = typesDb.struct("Struct",
        ImmutableMap.of("b", typesDb.string(), "a", typesDb.string())));
    when(() -> type.hash());
    thenReturned(not(equalTo(type2.hash())));
  }

  @Test
  public void type_type_can_be_read_back() throws Exception {
    given(type = typesDb.type());
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void string_type_can_be_read_back() throws Exception {
    given(type = typesDb.string());
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void blob_type_can_be_read_back() throws Exception {
    given(type = typesDb.blob());
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void nothing_type_can_be_read_back() throws Exception {
    given(type = typesDb.nothing());
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void array_of_string_type_can_be_read_back() throws Exception {
    given(type = typesDb.array(typesDb.string()));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void array_of_array_of_string_type_can_be_read_back() throws Exception {
    given(type = typesDb.array(typesDb.array(typesDb.string())));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void struct_type_can_be_read_back() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(type = typesDb.struct("TypeName", fields));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void two_level_deep_struct_type_can_be_read_back() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(type = typesDb.struct("TypeName", fields));
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", type));
    given(type = typesDb.struct("TypeName2", fields));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void type_type_is_cached() throws Exception {
    given(type = typesDb.type());
    when(() -> typesDb.type());
    thenReturned(same(type));
  }

  @Test
  public void type_type_is_cached_when_read_by_hash() throws Exception {
    given(type = typesDb.type());
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void type_type_is_cached_when_read_twice_by_hash() throws Exception {
    given(type = typesDb.type());
    given(typesDb = new TypesDb(hashedDb));
    given(type = typesDb.read(type.hash()));
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void string_type_is_cached() throws Exception {
    given(type = typesDb.string());
    when(() -> typesDb.string());
    thenReturned(same(type));
  }

  @Test
  public void string_type_is_cached_when_read_by_hash() throws Exception {
    given(type = typesDb.string());
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void string_type_is_cached_when_read_twice_by_hash() throws Exception {
    given(type = typesDb.string());
    given(typesDb = new TypesDb(hashedDb));
    given(type = typesDb.read(type.hash()));
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void blob_type_is_cached() throws Exception {
    given(type = typesDb.blob());
    when(() -> typesDb.blob());
    thenReturned(same(type));
  }

  @Test
  public void blob_type_is_cached_when_read_by_hash() throws Exception {
    given(type = typesDb.blob());
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void blob_type_is_cached_when_read_tiwce_by_hash() throws Exception {
    given(type = typesDb.blob());
    given(typesDb = new TypesDb(hashedDb));
    given(type = typesDb.read(type.hash()));
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void nothing_type_is_cached() throws Exception {
    given(type = typesDb.nothing());
    when(() -> typesDb.nothing());
    thenReturned(same(type));
  }

  @Test
  public void nothing_type_is_cached_when_read_by_hash() throws Exception {
    given(type = typesDb.nothing());
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void nothing_type_is_cached_when_read_twice_by_hash() throws Exception {
    given(type = typesDb.nothing());
    given(typesDb = new TypesDb(hashedDb));
    given(type = typesDb.read(type.hash()));
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void array_of_string_type_is_cached() throws Exception {
    given(arrayType = typesDb.array(typesDb.string()));
    when(() -> typesDb.array(typesDb.string()));
    thenReturned(same(arrayType));
    then(arrayType.elemType(), same(((ArrayType) typesDb.read(arrayType.hash())).elemType()));
  }

  @Test
  public void array_of_string_type_is_cached_when_read_by_hash() throws Exception {
    given(arrayType = typesDb.array(typesDb.string()));
    when(() -> typesDb.read(arrayType.hash()));
    thenReturned(same(arrayType));
    then(arrayType.elemType(), same(((ArrayType) typesDb.read(arrayType.hash())).elemType()));
  }

  @Test
  public void array_of_string_type_is_cached_when_read_twice_by_hash() throws Exception {
    given(arrayType = typesDb.array(typesDb.string()));
    given(typesDb = new TypesDb(hashedDb));
    given(arrayType = (ArrayType) typesDb.read(arrayType.hash()));
    when(() -> typesDb.read(arrayType.hash()));
    thenReturned(same(arrayType));
    then(arrayType.elemType(), same(((ArrayType) typesDb.read(arrayType.hash())).elemType()));
  }

  @Test
  public void array_of_array_of_string_type_is_cached() throws Exception {
    given(arrayType = typesDb.array(typesDb.array(typesDb.string())));
    when(() -> typesDb.array(typesDb.array(typesDb.string())));
    thenReturned(same(arrayType));
    then(arrayType.elemType(), same(typesDb.array(typesDb.array(typesDb.string())).elemType()));
    then(((ArrayType) arrayType.elemType()).elemType(),
        same(((ArrayType) typesDb.array(typesDb.array(typesDb.string())).elemType()).elemType()));
  }

  @Test
  public void array_of_array_of_string_type_is_cached_when_read_by_hash() throws Exception {
    given(arrayType = typesDb.array(typesDb.array(typesDb.string())));
    when(() -> typesDb.read(arrayType.hash()));
    thenReturned(same(arrayType));
    then(arrayType.elemType(), same(((ArrayType) typesDb.read(arrayType.hash())).elemType()));
    then(((ArrayType) arrayType.elemType()).elemType(),
        same(((ArrayType) ((ArrayType) typesDb.read(arrayType.hash())).elemType()).elemType()));
  }

  @Test
  public void array_of_array_of_string_type_is_cached_when_read_twice_by_hash() throws Exception {
    given(arrayType = typesDb.array(typesDb.array(typesDb.string())));
    given(typesDb = new TypesDb(hashedDb));
    given(arrayType = (ArrayType) typesDb.read(arrayType.hash()));
    when(() -> typesDb.read(arrayType.hash()));
    thenReturned(same(arrayType));
    then(arrayType.elemType(), same(((ArrayType) typesDb.read(arrayType.hash())).elemType()));
    then(((ArrayType) arrayType.elemType()).elemType(),
        same(((ArrayType) ((ArrayType) typesDb.read(arrayType.hash())).elemType()).elemType()));
  }

  @Test
  public void struct_type_is_cached() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(structType = typesDb.struct("TypeName", fields));
    when(() -> typesDb.struct("TypeName", fields));
    thenReturned(same(structType));
    then(structType.fields().get("field1"),
        same(typesDb.struct("TypeName", fields).fields().get(("field1"))));
  }

  @Test
  public void struct_type_is_cached_when_read_by_hash() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(structType = typesDb.struct("TypeName", fields));
    when(() -> typesDb.read(structType.hash()));
    thenReturned(same(structType));
    then(structType.fields().get("field1"),
        same(((StructType) typesDb.read(structType.hash())).fields().get(("field1"))));
  }

  @Test
  public void struct_type_is_cached_when_read_twice_by_hash() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(structType = typesDb.struct("TypeName", fields));
    given(typesDb = new TypesDb(hashedDb));
    given(structType = (StructType) typesDb.read(structType.hash()));
    when(() -> typesDb.read(structType.hash()));
    thenReturned(same(structType));
    then(structType.fields().get("field1"),
        same(((StructType) typesDb.read(structType.hash())).fields().get(("field1"))));
  }
}
