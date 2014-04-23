package org.smoothbuild.db.taskoutputs;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.db.taskoutputs.TaskOutputsDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.util.Empty;
import org.smoothbuild.util.Streams;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskOutputsDbTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final HashedDb taskOutputsHashedDb = new HashedDb(new FakeFileSystem());
  private final TaskOutputsDb taskOutputsDb = new TaskOutputsDb(taskOutputsHashedDb, objectsDb);
  private final HashCode hash = Hash.string("abc");

  private final byte[] bytes = new byte[] {};
  private final Path path = path("file/path");

  private Message message;
  private SArray<SFile> fileArray;
  private SArray<SBlob> blobArray;
  private SArray<SString> stringArray;
  private SFile file;
  private SBlob blob;
  private SString stringValue;
  private final String string = "some string";

  @Test
  public void result_cache_does_not_contain_not_written_result() {
    when(taskOutputsDb.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_written_result() {
    given(taskOutputsDb).write(hash,
        new TaskOutput<>(objectsDb.string("result"), Empty.messageList()));
    when(taskOutputsDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_written_value_fails() throws Exception {
    when(taskOutputsDb).read(hash, STRING);
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    given(stringValue = objectsDb.string("abc"));
    given(message = new Message(ERROR, "message string"));
    given(taskOutputsDb).write(hash, new TaskOutput<>(stringValue, ImmutableList.of(message)));
    when(taskOutputsDb.read(hash, STRING).messages());
    thenReturned(contains(message));
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    given(file = objectsDb.file(path, bytes));
    given(fileArray = objectsDb.arrayBuilder(FILE_ARRAY).add(file).build());
    given(taskOutputsDb).write(hash, new TaskOutput<>(fileArray, Empty.messageList()));
    when(taskOutputsDb.read(hash, FILE_ARRAY).returnValue().iterator().next());
    thenReturned(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blobArray = objectsDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    given(taskOutputsDb).write(hash, new TaskOutput<>(blobArray, Empty.messageList()));
    when(taskOutputsDb.read(hash, BLOB_ARRAY).returnValue().iterator().next());
    thenReturned(blob);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    given(stringValue = objectsDb.string(string));
    given(stringArray = objectsDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    given(taskOutputsDb).write(hash, new TaskOutput<>(stringArray, Empty.messageList()));
    when(taskOutputsDb.read(hash, STRING_ARRAY).returnValue().iterator().next());
    thenReturned(stringValue);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    given(file = objectsDb.file(path, bytes));
    given(taskOutputsDb).write(hash, new TaskOutput<>(file, Empty.messageList()));
    when(taskOutputsDb.read(hash, FILE).returnValue());
    thenReturned(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(taskOutputsDb).write(hash, new TaskOutput<>(blob, Empty.messageList()));
    when(taskOutputsDb.read(hash, BLOB).returnValue());
    thenReturned(blob);
  }

  @Test
  public void writtend_string_can_be_read_back() throws Exception {
    given(stringValue = objectsDb.string(string));
    given(taskOutputsDb).write(hash, new TaskOutput<>(stringValue, Empty.messageList()));
    when(taskOutputsDb.read(hash, STRING).returnValue().value());
    thenReturned(string);
  }

  private static SBlob writeBlob(ObjectsDb objectsDb, byte[] bytes) throws IOException {
    BlobBuilder builder = objectsDb.blobBuilder();
    Streams.copy(new ByteArrayInputStream(bytes), builder.openOutputStream());
    return builder.build();
  }
}
