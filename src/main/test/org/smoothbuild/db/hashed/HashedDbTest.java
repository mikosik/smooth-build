package org.smoothbuild.db.hashed;

import static okio.ByteString.encodeUtf8;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.io.fs.base.Path.path;
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
  private HashedDb hashedDb;
  private Marshaller marshaller;
  private HashCode hash;
  private MemoryFileSystem fileSystem;
  private ByteString byteString;
  private byte[] bytes;

  @Before
  public void before() {
    given(fileSystem = new MemoryFileSystem());
    given(hashedDb = new HashedDb(fileSystem, Path.root(), new TempManager(fileSystem)));
  }

  @Test
  public void db_doesnt_contain_not_written_data() {
    when(hashedDb.contains(HashCode.fromInt(33)));
    thenReturned(false);
  }

  @Test
  public void db_contains_written_data() throws Exception {
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
  public void reading_not_written_value_fails() {
    given(hash = Hash.string("abc"));
    when(() -> hashedDb.source(hash));
    thenThrown(exception(new NoSuchDataException(hash)));
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
  public void not_written_sequence_of_hashes_cannot_be_read_back() {
    when(() -> hashedDb.readHashes(Hash.string("abc")));
    thenThrown(IOException.class);
  }

  @Test
  public void corrupted_sequence_of_hashes_cannot_be_read_back() throws Exception {
    given(hash = hashedDb.writeString("12345"));
    when(() -> hashedDb.readHashes(hash));
    thenThrown(IOException.class);
  }

  @Test
  public void written_data_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(byteString = encodeUtf8("abc"));
    given(marshaller.sink().write(byteString));
    given(marshaller).close();
    when(hashedDb.source(marshaller.hash()).readUtf8());
    thenReturned("abc");
  }

  @Test
  public void written_zero_length_data_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).close();
    when(hashedDb.source(marshaller.hash()).readByteString());
    thenReturned(ByteString.of());
  }


  @Test
  public void bytes_written_twice_can_be_read_back() {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes1));
    given(() -> marshaller.close());
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes1));
    given(() -> marshaller.close());
    when(() -> hashedDb.source(marshaller.hash()).readByteString());
    thenReturned(bytes1);
  }

  @Test
  public void hashes_for_different_data_are_different() {
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes1));
    given(() -> marshaller.close());
    given(() -> hash = marshaller.hash());
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes2));
    given(() -> marshaller.close());
    when(marshaller.hash());
    thenReturned(not(hash));
  }

  @Test
  public void written_data_is_not_visible_until_close_is_invoked() {
    given(() -> bytes = new byte[1024 * 1024]);
    given(() -> hash = Hash.bytes(bytes));
    given(() -> marshaller = hashedDb.newMarshaller());
    given(() -> marshaller.sink().write(bytes));
    when(() -> hashedDb.source(hash));
    thenThrown(exception(new IOException("No data at " + hash + ".")));
  }

  // tests for corrupted db

  @Test
  public void directory_with_name_equal_to_data_hash_causes_corrupted_exception_when_reading_data() {
    given(() -> hash = Hash.integer(33));
    given(() -> fileSystem.createDir(path(hash.toString())));
    when(() -> hashedDb.source(hash));
    thenThrown(exception(new CorruptedHashedDbException(
        "Corrupted HashedDb. '" + hash + "' is a directory not a data file.")));
  }

  @Test
  public void directory_with_name_equal_to_data_hash_causes_corrupted_exception_when_writing_data() {
    given(() -> hash = Hash.bytes(bytes1.toByteArray()));
    given(() -> fileSystem.createDir(path(hash.toString())));
    when(() -> hashedDb.newMarshaller().sink().write(bytes1).close());
    thenThrown(exception(new CorruptedHashedDbException(
        "Corrupted HashedDb. Cannot store data at '" + hash + "' as it is a directory.")));
  }
}
