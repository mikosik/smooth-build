package org.smoothbuild.db.outputs;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.MessagesDb;
import org.smoothbuild.lang.message.TestingMessagesDb;
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.TestingValueFactory;
import org.smoothbuild.lang.value.ValueFactory;
import org.smoothbuild.task.base.Output;

import com.google.common.hash.HashCode;

import okio.ByteString;

public class OutputsDbTest {
  private HashedDb hashedDbValues;
  private HashedDb hashedDbOutputs;
  private TypesDb typesDb;
  private ValuesDb valuesDb;
  private TestingRuntimeTypes types;
  private ValueFactory valueFactory;
  private MessagesDb messagesDb;
  private OutputsDb outputsDb;
  private final HashCode hash = Hash.string("abc");

  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private final Path path = path("file/path");

  private Message message;
  private Array array;
  private Struct file;
  private Blob blob;
  private SString stringValue;
  private final String string = "some string";

  @Before
  public void before() {
    hashedDbValues = new TestingHashedDb();
    hashedDbOutputs = new TestingHashedDb();
    typesDb = new TypesDb(hashedDbValues);
    valuesDb = new ValuesDb(hashedDbValues, typesDb);
    types = new TestingRuntimeTypes(typesDb);
    valueFactory = new TestingValueFactory(types, valuesDb);
    messagesDb = new TestingMessagesDb(valuesDb, types);
    outputsDb = new OutputsDb(hashedDbOutputs, valuesDb, messagesDb, typesDb);
  }

  @Test
  public void result_cache_does_not_contain_not_written_result() {
    when(outputsDb.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_written_result() {
    given(outputsDb).write(hash, new Output(valuesDb.string("result"), list()));
    when(outputsDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_written_value_fails() throws Exception {
    when(outputsDb).read(hash, typesDb.string());
    thenThrown(OutputsDbException.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string("abc"));
    given(message = messagesDb.error("message string"));
    given(outputsDb).write(hash, new Output(stringValue, list(message)));
    when(outputsDb.read(hash, typesDb.string()).messages());
    thenReturned(contains(message));
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    given(file = file(valueFactory, path, bytes));
    given(array = valuesDb.arrayBuilder(types.file()).add(file).build());
    given(outputsDb).write(hash, new Output(array, list()));
    when(((Array) outputsDb.read(hash, typesDb.array(types.file())).result())
        .asIterable(Struct.class).iterator().next());
    thenReturned(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    given(blob = writeBlob(valuesDb, bytes));
    given(array = valuesDb.arrayBuilder(typesDb.blob()).add(blob).build());
    given(outputsDb).write(hash, new Output(array, list()));
    when(((Array) outputsDb.read(hash, typesDb.array(typesDb.blob())).result())
        .asIterable(Blob.class).iterator().next());
    thenReturned(blob);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string(string));
    given(array = valuesDb.arrayBuilder(typesDb.string()).add(stringValue).build());
    given(outputsDb).write(hash, new Output(array, list()));
    when(((Array) outputsDb.read(hash, typesDb.array(typesDb.string())).result())
        .asIterable(SString.class).iterator().next());
    thenReturned(stringValue);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    given(file = file(valueFactory, path, bytes));
    given(outputsDb).write(hash, new Output(file, list()));
    when(outputsDb.read(hash, types.file()).result());
    thenReturned(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    given(blob = writeBlob(valuesDb, bytes));
    given(outputsDb).write(hash, new Output(blob, list()));
    when(outputsDb.read(hash, typesDb.blob()).result());
    thenReturned(blob);
  }

  @Test
  public void writtend_string_can_be_read_back() throws Exception {
    given(stringValue = valuesDb.string(string));
    given(outputsDb).write(hash, new Output(stringValue, list()));
    when(((SString) outputsDb.read(hash, typesDb.string()).result()).data());
    thenReturned(string);
  }

  private static Blob writeBlob(ValuesDb valuesDb, ByteString bytes) throws IOException {
    BlobBuilder builder = valuesDb.blobBuilder();
    builder.sink().write(bytes);
    return builder.build();
  }
}
