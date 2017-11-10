package org.smoothbuild.db.values;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.util.Streams.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class ValueHashTest {
  private ValuesDb valuesDb;
  private SString sstring;
  private Blob blob;
  private SFile file;
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
  public void hash_of_empty_file_is_stable() throws Exception {
    given(file = createFile(valuesDb, "", new byte[] {}));
    when(() -> file.hash());
    thenReturned(HashCode.fromString("43b1e995dbead10e335145327cf24f8d0ec38f88"));
  }

  @Test
  public void hash_of_some_file_is_stable() throws Exception {
    given(file = createFile(valuesDb, "abc", new byte[] { 1, 2, 3 }));
    when(() -> file.hash());
    thenReturned(HashCode.fromString("f4ca6ed218869153e4ef2d8ac0d913127f340f79"));
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
  public void hash_of_empty_file_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(FILE).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
  }

  @Test
  public void hash_of_non_empty_file_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(FILE).add(createFile(valuesDb, "", new byte[] {})).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("bc3752dfc291fcf61ec7c65a02f83fb3c7576f4e"));
  }

  @Test
  public void hash_of_empty_nothing_array_is_stable() throws Exception {
    given(array = valuesDb.arrayBuilder(NOTHING).build());
    when(() -> array.hash());
    thenReturned(HashCode.fromString("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
  }

  private static SFile createFile(ValuesDb valuesDb, String path, byte[] content) throws Exception {
    return valuesDb.file(valuesDb.string(path), createBlob(valuesDb, content));
  }

  private static Blob createBlob(ValuesDb valuesDb, byte[] content) throws Exception {
    BlobBuilder blobBuilder = valuesDb.blobBuilder();
    writeAndClose(blobBuilder, content);
    return blobBuilder.build();
  }
}
