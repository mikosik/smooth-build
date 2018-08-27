package org.smoothbuild.db.hashed;

import static okio.ByteString.encodeUtf8;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;

import com.google.common.hash.HashCode;

import okio.ByteString;

public class HashedDbTest {
  private final ByteString bytes1 = ByteString.encodeUtf8("aaa");
  private final ByteString bytes2 = ByteString.encodeUtf8("bbb");
  private HashCode hash;
  private HashedDb hashedDb;
  private Marshaller marshaller;
  private HashCode hashId;
  private Unmarshaller unmarshaller;
  private MemoryFileSystem fileSystem;
  private ByteString byteString;

  @Before
  public void before() {
    given(fileSystem = new MemoryFileSystem());
    given(hashedDb = new HashedDb(fileSystem, Path.root(), new TempManager(fileSystem)));
  }

  @Test
  public void db_doesnt_contain_not_stored_data() throws Exception {
    when(hashedDb.contains(HashCode.fromInt(33)));
    thenReturned(false);
  }

  @Test
  public void db_contains_added_data() throws Exception {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).close();
    when(hashedDb.contains(marshaller.hash()));
    thenReturned(true);
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    given(hash = hashedDb.writeString("abc"));
    when(() -> hashedDb.readString(hash));
    thenReturned("abc");
  }

  @Test
  public void not_written_string_cannot_be_read_back() throws Exception {
    when(() -> hashedDb.readString(Hash.string("abc")));
    thenThrown(IOException.class);
  }

  @Test
  public void written_empty_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = hashedDb.writeHashes());
    when(() -> hashedDb.readHashes(hash));
    thenReturned(list());
  }

  @Test
  public void written_one_element_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = hashedDb.writeHashes(Hash.string("abc")));
    when(() -> hashedDb.readHashes(hash));
    thenReturned(list(Hash.string("abc")));
  }

  @Test
  public void written_two_elements_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = hashedDb.writeHashes(Hash.string("abc"), Hash.string("def")));
    when(() -> hashedDb.readHashes(hash));
    thenReturned(list(Hash.string("abc"), Hash.string("def")));
  }

  @Test
  public void not_written_sequence_of_hashes_cannot_be_read_back() throws Exception {
    when(() -> hashedDb.readHashes(Hash.string("abc")));
    thenThrown(IOException.class);
  }

  @Test
  public void corrupted_sequence_of_hashes_cannot_be_read_back() throws Exception {
    given(hash = hashedDb.writeString("12345"));
    when(() -> hashedDb.readHashes(hash));
    thenThrown(exception(new NotEnoughBytesException(20, 5)));
  }

  @Test
  public void written_data_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(byteString = encodeUtf8("abc"));
    given(marshaller.sink().write(byteString));
    given(marshaller).close();
    when(hashedDb.newUnmarshaller(marshaller.hash()).source().readUtf8());
    thenReturned("abc");
  }

  @Test
  public void written_empty_byte_array_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).close();
    when(hashedDb.newUnmarshaller(marshaller.hash()).source().readByteString());
    thenReturned(ByteString.of());
  }

  @Test
  public void written_data_at_given_hash_can_be_read_back() throws IOException {
    given(() -> hashId = Hash.integer(33));
    given(() -> marshaller = hashedDb.newMarshaller(hashId));
    given(() -> marshaller.sink().write(bytes1));
    given(() -> marshaller.close());
    when(() -> hashedDb.newUnmarshaller(marshaller.hash()).source().readByteString());
    thenReturned(bytes1);
  }

  @Test
  public void bytes_written_twice_can_be_read_back() throws IOException {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes1));
    given(() -> marshaller.close());
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes1));
    given(() -> marshaller.close());
    when(() -> hashedDb.newUnmarshaller(marshaller.hash()).source().readByteString());
    thenReturned(bytes1);
  }

  @Test
  public void storing_bytes_at_already_used_hash_is_ignored() throws IOException {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes1));
    given(() -> marshaller.close());
    given(() -> marshaller = hashedDb.newMarshaller(marshaller.hash()));
    given(() -> marshaller.sink().write(bytes2));
    given(() -> marshaller.close());
    when(hashedDb.newUnmarshaller(marshaller.hash()).source().readByteString());
    thenReturned(bytes1);
  }

  @Test
  public void hashes_for_different_data_are_different() throws IOException {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes1));
    given(() -> marshaller.close());
    given(() -> hashId = marshaller.hash());
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes2));
    given(() -> marshaller.close());
    when(marshaller.hash());
    thenReturned(not(hashId));
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    given(hashId = HashCode.fromInt(33));
    when(hashedDb).newUnmarshaller(hashId);
    thenThrown(exception(new IOException("Could not find " + hashId + " object.")));
  }

  @Test
  public void written_data_is_not_visible_until_close_is_invoked() throws Exception {
    given(() -> hashId = Hash.integer(17));
    given(() -> marshaller = hashedDb.newMarshaller(hashId));
    given(() -> marshaller.sink().write(new byte[1024 * 1024]));
    when(() -> hashedDb.newUnmarshaller(hashId));
    thenThrown(exception(new IOException("Could not find " + hashId + " object.")));
  }

  @Test
  public void reading_hash_when_db_value_has_too_few_bytes_causes_exception() throws IOException {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(new byte[1]));
    given(() -> marshaller.close());
    given(() -> hashId = marshaller.hash());
    when(() -> hashedDb.newUnmarshaller(hashId).readHash());
    thenThrown(exception(new NotEnoughBytesException(20, 1)));
  }

  @Test
  public void reading_hash_when_db_has_zero_bytes_causes_exception() throws IOException {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(new byte[0]));
    given(() -> marshaller.close());
    given(hashId = marshaller.hash());
    when(() -> hashedDb.newUnmarshaller(hashId).readHash());
    thenThrown(exception(new NotEnoughBytesException(20, 0)));
  }

  @Test
  public void trying_to_read_hash_when_db_has_zero_bytes_returns_null() throws IOException {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(new byte[0]));
    given(() -> marshaller.close());
    when(() -> hashedDb.newUnmarshaller(marshaller.hash()).tryReadHash());
    thenReturned(null);
  }

  @Test
  public void trying_to_read_hash_when_db_has_too_few_bytes_causes_exception() throws IOException {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(new byte[1]));
    given(() -> marshaller.close());
    given(hashId = marshaller.hash());
    when(() -> hashedDb.newUnmarshaller(hashId).tryReadHash());
    thenThrown(NotEnoughBytesException.class);
  }

  @Test
  public void trying_to_read_hash_twice_when_only_one_is_stored_returns_null_second_time()
      throws IOException {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(Hash.integer(17).asBytes()));
    given(() -> marshaller.close());
    given(() -> unmarshaller = hashedDb.newUnmarshaller(marshaller.hash()));
    given(() -> unmarshaller.tryReadHash());
    when(() -> unmarshaller.tryReadHash());
    thenReturned(null);
  }
}
