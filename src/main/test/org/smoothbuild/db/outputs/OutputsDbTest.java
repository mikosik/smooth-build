package org.smoothbuild.db.outputs;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.db.values.ValueCreators.array;
import static org.smoothbuild.testing.db.values.ValueCreators.errorMessage;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
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
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.TestingValueFactory;
import org.smoothbuild.lang.value.Value;
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
  private OutputsDb outputsDb;
  private final HashCode hash = Hash.string("abc");

  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private final Path path = path("file/path");

  private Value message;
  private Array messages;
  private Array array;
  private Struct file;
  private Blob blob;
  private Bool boolValue;
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
    outputsDb = new OutputsDb(hashedDbOutputs, valuesDb, types);
  }

  @Test
  public void result_cache_does_not_contain_not_written_result() {
    when(outputsDb.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_written_result() {
    given(outputsDb).write(hash, new Output(valuesDb.string("result"), emptyMessageArray()));
    when(outputsDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_written_value_fails() {
    when(outputsDb).read(hash, typesDb.string());
    thenThrown(OutputsDbException.class);
  }

  @Test
  public void written_messages_can_be_read_back() {
    given(stringValue = valuesDb.string("abc"));
    given(message = errorMessage(hashedDbValues, "error message"));
    given(messages = array(hashedDbValues, message));
    given(outputsDb).write(hash, new Output(stringValue, messages));
    when(outputsDb.read(hash, typesDb.string()).messages());
    thenReturned(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() {
    given(file = file(valueFactory, path, bytes));
    given(array = valuesDb.arrayBuilder(types.file()).add(file).build());
    given(outputsDb).write(hash, new Output(array, emptyMessageArray()));
    when(((Array) outputsDb.read(hash, typesDb.array(types.file())).result())
        .asIterable(Struct.class).iterator().next());
    thenReturned(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    given(blob = writeBlob(valuesDb, bytes));
    given(array = valuesDb.arrayBuilder(typesDb.blob()).add(blob).build());
    given(outputsDb).write(hash, new Output(array, emptyMessageArray()));
    when(((Array) outputsDb.read(hash, typesDb.array(typesDb.blob())).result())
        .asIterable(Blob.class).iterator().next());
    thenReturned(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() {
    given(boolValue = valuesDb.bool(true));
    given(array = valuesDb.arrayBuilder(typesDb.bool()).add(boolValue).build());
    given(outputsDb).write(hash, new Output(array, emptyMessageArray()));
    when(((Array) outputsDb.read(hash, typesDb.array(typesDb.bool())).result())
        .asIterable(Bool.class).iterator().next());
    thenReturned(boolValue);
  }

  @Test
  public void written_string_array_can_be_read_back() {
    given(stringValue = valuesDb.string(string));
    given(array = valuesDb.arrayBuilder(typesDb.string()).add(stringValue).build());
    given(outputsDb).write(hash, new Output(array, emptyMessageArray()));
    when(((Array) outputsDb.read(hash, typesDb.array(typesDb.string())).result())
        .asIterable(SString.class).iterator().next());
    thenReturned(stringValue);
  }

  @Test
  public void written_file_can_be_read_back() {
    given(file = file(valueFactory, path, bytes));
    given(outputsDb).write(hash, new Output(file, emptyMessageArray()));
    when(outputsDb.read(hash, types.file()).result());
    thenReturned(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    given(blob = writeBlob(valuesDb, bytes));
    given(outputsDb).write(hash, new Output(blob, emptyMessageArray()));
    when(outputsDb.read(hash, typesDb.blob()).result());
    thenReturned(blob);
  }

  @Test
  public void written_bool_can_be_read_back() {
    given(boolValue = valuesDb.bool(true));
    given(outputsDb).write(hash, new Output(boolValue, emptyMessageArray()));
    when(((Bool) outputsDb.read(hash, typesDb.bool()).result()).data());
    thenReturned(true);
  }

  @Test
  public void written_string_can_be_read_back() {
    given(stringValue = valuesDb.string(string));
    given(outputsDb).write(hash, new Output(stringValue, emptyMessageArray()));
    when(((SString) outputsDb.read(hash, typesDb.string()).result()).data());
    thenReturned(string);
  }

  private static Blob writeBlob(ValuesDb valuesDb, ByteString bytes) throws IOException {
    BlobBuilder builder = valuesDb.blobBuilder();
    builder.sink().write(bytes);
    return builder.build();
  }

  private Array emptyMessageArray() {
    return array(hashedDbValues, types.message());
  }
}
