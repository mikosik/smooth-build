package org.smoothbuild.db.values;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.util.Streams.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class ValueHashTest {
  private ValuesDb valuesDb;
  private SString sstring;
  private Blob blob;
  private Struct struct;
  private Array array;

  @Before
  public void before() {
    valuesDb = memoryValuesDb();
  }

  @Test
  public void hash_of_empty_string_is_stable() throws Exception {
    given(sstring = valuesDb.string(""));
    when(sstring).hash();
    thenReturned(HashCode.fromString("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
  }

  @Test
  public void hash_of_some_string_is_stable() throws Exception {
    given(sstring = valuesDb.string("abc"));
    when(sstring).hash();
    thenReturned(HashCode.fromString("a9993e364706816aba3e25717850c26c9cd0d89d"));
  }

  @Test
  public void hash_of_empty_blob_is_stable() throws Exception {
    given(blob = valuesDb.blobBuilder().build());
    when(() -> blob.hash());
    thenReturned(HashCode.fromString("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
  }

  @Test
  public void hash_of_some_blob_is_stable() throws Exception {
    given(blob = createBlob(valuesDb, new byte[] { 1, 2, 3 }));
    when(() -> blob.hash());
    thenReturned(HashCode.fromString("7037807198c22a7d2b0807371d763779a84fdfcf"));
  }

  @Test
  public void hash_of_empty_struct_is_stable() throws Exception {
    given(struct = createStruct(valuesDb, "John", "Doe"));
    when(() -> struct.hash());
    thenReturned(HashCode.fromString("bc6915c8051b7fd49d11c5082976f70f9526d07b"));
  }

  @Test
  public void hash_of_some_struct_is_stable() throws Exception {
    given(struct = createStruct(valuesDb, "John", "Doe"));
    when(() -> struct.hash());
    thenReturned(HashCode.fromString("bc6915c8051b7fd49d11c5082976f70f9526d07b"));
  }

  @Test
  public void hash_of_empty_string_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(STRING).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
  }

  @Test
  public void hash_of_non_empty_string_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(STRING).add(valuesDb.string("")).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("be1bdec0aa74b4dcb079943e70528096cca985f8"));
  }

  @Test
  public void hash_of_empty_blob_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(BLOB).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
  }

  @Test
  public void hash_of_non_empty_blob_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(BLOB).add(createBlob(valuesDb, new byte[] {})).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("be1bdec0aa74b4dcb079943e70528096cca985f8"));
  }

  @Test
  public void hash_of_empty_struct_array_is_stable() throws Exception {
    given(struct = createStruct(valuesDb, "John", "Doe"));
    given(array = valuesDb.arrayBuilder(personType()).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
  }

  @Test
  public void hash_of_non_empty_struct_array_is_stable() throws Exception {
    given(struct = createStruct(valuesDb, "John", "Doe"));
    given(array = valuesDb.arrayBuilder(personType()).add(struct).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("d4ef59c09655067daa0c8a69b542221615e47c7f"));
  }

  @Test
  public void hash_of_empty_nothing_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(NOTHING).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
  }

  private static Struct createStruct(ValuesDb valuesDb, String firstName, String lastName)
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

  private static StructType personType() {
    return new StructType("Person", ImmutableMap.of("firstName", STRING, "lastName", STRING));
  }
}
