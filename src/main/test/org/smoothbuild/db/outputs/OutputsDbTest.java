package org.smoothbuild.db.outputs;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.util.Streams;

import com.google.common.hash.HashCode;

public class OutputsDbTest {
  private final ValuesDb valuesDb = memoryValuesDb();
  private final FileSystem fileSystem = new MemoryFileSystem();
  private final HashedDb hashedDb = new HashedDb(fileSystem, Path.root(), new TempManager(
      fileSystem));
  private final OutputsDb outputsDb = new OutputsDb(hashedDb, valuesDb);
  private final HashCode hash = Hash.string("abc");

  private final byte[] bytes = new byte[] {};
  private final Path path = path("file/path");

  private Message message;
  private Array array;
  private SFile file;
  private Blob blob;
  private SString stringValue;
  private final String string = "some string";

  @Test
  public void result_cache_does_not_contain_not_written_result() {
    when(outputsDb.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_written_result() {
    given(outputsDb).write(hash, new Output(valuesDb.string("result"), asList()));
    when(outputsDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_written_value_fails() throws Exception {
    when(outputsDb).read(hash, STRING);
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string("abc"));
    given(message = new ErrorMessage("message string"));
    given(outputsDb).write(hash, new Output(stringValue, asList(message)));
    when(outputsDb.read(hash, STRING).messages());
    thenReturned(contains(message));
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    given(file = file(valuesDb, path, bytes));
    given(array = valuesDb.arrayBuilder(FILE).add(file).build());
    given(outputsDb).write(hash, new Output(array, asList()));
    when(((Array) outputsDb.read(hash, FILE_ARRAY).result()).asIterable(SFile.class).iterator()
        .next());
    thenReturned(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    given(blob = writeBlob(valuesDb, bytes));
    given(array = valuesDb.arrayBuilder(Types.BLOB).add(blob).build());
    given(outputsDb).write(hash, new Output(array, asList()));
    when(((Array) outputsDb.read(hash, BLOB_ARRAY).result()).asIterable(Blob.class).iterator()
        .next());
    thenReturned(blob);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string(string));
    given(array = valuesDb.arrayBuilder(STRING).add(stringValue).build());
    given(outputsDb).write(hash, new Output(array, asList()));
    when(((Array) outputsDb.read(hash, STRING_ARRAY).result()).asIterable(SString.class).iterator()
        .next());
    thenReturned(stringValue);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    given(file = file(valuesDb, path, bytes));
    given(outputsDb).write(hash, new Output(file, asList()));
    when(outputsDb.read(hash, FILE).result());
    thenReturned(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    given(blob = writeBlob(valuesDb, bytes));
    given(outputsDb).write(hash, new Output(blob, asList()));
    when(outputsDb.read(hash, BLOB).result());
    thenReturned(blob);
  }

  @Test
  public void writtend_string_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string(string));
    given(outputsDb).write(hash, new Output(stringValue, asList()));
    when(((SString) outputsDb.read(hash, STRING).result()).value());
    thenReturned(string);
  }

  private static Blob writeBlob(ValuesDb valuesDb, byte[] bytes) throws IOException {
    BlobBuilder builder = valuesDb.blobBuilder();
    Streams.copy(new ByteArrayInputStream(bytes), builder);
    return builder.build();
  }
}
