package org.smoothbuild.db.values;

import static org.smoothbuild.util.Streams.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class ValueHashTest {
  private TypesDb typesDb;
  private ValuesDb valuesDb;
  private SString sstring;
  private Blob blob;
  private Struct struct;
  private Array array;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void hash_of_empty_string_is_stable() throws Exception {
    given(sstring = valuesDb.string(""));
    when(sstring).hash();
    thenReturned(HashCode.fromString("0af6d4b68d3dadc04f1fd8d207702afd4809a0c2"));
  }

  @Test
  public void hash_of_some_string_is_stable() throws Exception {
    given(sstring = valuesDb.string("abc"));
    when(sstring).hash();
    thenReturned(HashCode.fromString("4d3465c4280a64e3a6c2272fc48e971d102eac93"));
  }

  @Test
  public void hash_of_empty_blob_is_stable() throws Exception {
    given(blob = valuesDb.blobBuilder().build());
    when(() -> blob.hash());
    thenReturned(HashCode.fromString("6915b0de6d40fc94df5c229b46da15e34d7c232b"));
  }

  @Test
  public void hash_of_some_blob_is_stable() throws Exception {
    given(blob = createBlob(valuesDb, new byte[] { 1, 2, 3 }));
    when(() -> blob.hash());
    thenReturned(HashCode.fromString("e8c40738ed9655dcc5cdc344654c20aa365a75bf"));
  }

  @Test
  public void hash_of_empty_struct_is_stable() throws Exception {
    given(struct = createStruct(valuesDb, "John", "Doe"));
    when(() -> struct.hash());
    thenReturned(HashCode.fromString("a94de85056893e6464baa8ded12a9a765c5b56fc"));
  }

  @Test
  public void hash_of_some_struct_is_stable() throws Exception {
    given(struct = createStruct(valuesDb, "John", "Doe"));
    when(() -> struct.hash());
    thenReturned(HashCode.fromString("a94de85056893e6464baa8ded12a9a765c5b56fc"));
  }

  @Test
  public void hash_of_empty_string_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.string()).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("b2d4a44801204a93da825d1b4db4ef4af2787d82"));
  }

  @Test
  public void hash_of_non_empty_string_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.string()).add(valuesDb.string("")).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("98370fae56927d0832578f133ca73ff1f58fe415"));
  }

  @Test
  public void hash_of_empty_blob_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.blob()).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("000c25ccefc9fbd916400c36eb99bd2610f507ea"));
  }

  @Test
  public void hash_of_non_empty_blob_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.blob()).add(
        createBlob(valuesDb, new byte[] {})).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("3dd2f9efc115cc922304f91df183bc8359df9ec8"));
  }

  @Test
  public void hash_of_empty_struct_array_is_stable() throws Exception {
    given(struct = createStruct(valuesDb, "John", "Doe"));
    given(array = valuesDb.arrayBuilder(personType()).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("21187af9783689f62abc2f1e10fb76233d167af8"));
  }

  @Test
  public void hash_of_non_empty_struct_array_is_stable() throws Exception {
    given(struct = createStruct(valuesDb, "John", "Doe"));
    given(array = valuesDb.arrayBuilder(personType()).add(struct).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("28c181c073e198d5ca5f233ac682a7a09f0aeb63"));
  }

  @Test
  public void hash_of_empty_nothing_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.nothing()).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("034da224a1b3f7e2d2702ce8c5dd986f11b9b08a"));
  }

  private Struct createStruct(ValuesDb valuesDb, String firstName, String lastName)
      throws Exception {
    return valuesDb.structBuilder(personType())
        .set("firstName", valuesDb.string(firstName))
        .set("lastName", valuesDb.string(lastName))
        .build();
  }

  private static Blob createBlob(ValuesDb valuesDb, byte[] content) throws Exception {
    BlobBuilder blobBuilder = valuesDb.blobBuilder();
    writeAndClose(blobBuilder, content);
    return blobBuilder.build();
  }

  private StructType personType() {
    Type string = typesDb.string();
    return typesDb.struct(
        "Person", ImmutableMap.of("firstName", string, "lastName", string));
  }
}
