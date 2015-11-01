package org.smoothbuild.db.outputs;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.ERROR;
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
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashException;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.util.Empty;
import org.smoothbuild.util.Streams;

import com.google.common.hash.HashCode;

public class OutputsDbTest {
  private final ValuesDb valuesDb = valuesDb();
  private final HashedDb taskOutputsHashedDb = new HashedDb(new MemoryFileSystem());
  private final OutputsDb outputsDb = new OutputsDb(taskOutputsHashedDb, valuesDb);
  private final HashCode hash = Hash.string("abc");

  private final byte[] bytes = new byte[] {};
  private final Path path = path("file/path");

  private Message message;
  private Array<SFile> fileArray;
  private Array<Blob> blobArray;
  private Array<SString> stringArray;
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
    given(outputsDb).write(hash, new Output(valuesDb.string("result"), Empty.messageList()));
    when(outputsDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_written_value_fails() throws Exception {
    when(outputsDb).read(hash, STRING);
    thenThrown(NoObjectWithGivenHashException.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string("abc"));
    given(message = new Message(ERROR, "message string"));
    given(outputsDb).write(hash, new Output(stringValue, asList(message)));
    when(outputsDb.read(hash, STRING).messages());
    thenReturned(contains(message));
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    given(file = file(valuesDb, path, bytes));
    given(fileArray = valuesDb.arrayBuilder(SFile.class).add(file).build());
    given(outputsDb).write(hash, new Output(fileArray, Empty.messageList()));
    when(((Iterable<?>) outputsDb.read(hash, FILE_ARRAY).result()).iterator().next());
    thenReturned(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    given(blob = writeBlob(valuesDb, bytes));
    given(blobArray = valuesDb.arrayBuilder(Blob.class).add(blob).build());
    given(outputsDb).write(hash, new Output(blobArray, Empty.messageList()));
    when(((Iterable<?>) outputsDb.read(hash, BLOB_ARRAY).result()).iterator().next());
    thenReturned(blob);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string(string));
    given(stringArray = valuesDb.arrayBuilder(SString.class).add(stringValue).build());
    given(outputsDb).write(hash, new Output(stringArray, Empty.messageList()));
    when(((Iterable<?>) outputsDb.read(hash, STRING_ARRAY).result()).iterator().next());
    thenReturned(stringValue);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    given(file = file(valuesDb, path, bytes));
    given(outputsDb).write(hash, new Output(file, Empty.messageList()));
    when(outputsDb.read(hash, FILE).result());
    thenReturned(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    given(blob = writeBlob(valuesDb, bytes));
    given(outputsDb).write(hash, new Output(blob, Empty.messageList()));
    when(outputsDb.read(hash, BLOB).result());
    thenReturned(blob);
  }

  @Test
  public void writtend_string_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string(string));
    given(outputsDb).write(hash, new Output(stringValue, Empty.messageList()));
    when(((SString) outputsDb.read(hash, STRING).result()).value());
    thenReturned(string);
  }

  private static Blob writeBlob(ValuesDb valuesDb, byte[] bytes) throws IOException {
    BlobBuilder builder = valuesDb.blobBuilder();
    Streams.copy(new ByteArrayInputStream(bytes), builder.openOutputStream());
    return builder.build();
  }
}
