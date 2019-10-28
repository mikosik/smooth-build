package org.smoothbuild.db.outputs;

import static org.smoothbuild.db.outputs.OutputsDbException.corruptedValueException;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class OutputsDbTest extends TestingContext {
  private final Hash hash = Hash.of("abc");
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private Path path = path("file/path");

  private SObject message;
  private Array messages;
  private Array array;
  private Struct file;
  private Blob blob;
  private Bool boolValue;
  private SString stringValue;
  private final String string = "some string";

  @Test
  public void outputsdb_does_not_contain_not_written_result() {
    when(outputsDb().contains(hash));
    thenReturned(false);
  }

  @Test
  public void outputsdb_contains_written_result() {
    given(outputsDb()).write(hash, new Output(string("result"), emptyMessageArray()));
    when(outputsDb().contains(hash));
    thenReturned(true);
  }

  @Test
  public void outputsdb_is_corrupted_when_task_hash_points_to_directory() {
    given(path = OutputsDb.toPath(hash));
    given(() -> outputsDbFileSystem().sink(path.append(path("file"))));
    when(() -> outputsDb().contains(hash));
    thenThrown(exception(corruptedValueException(hash, path + " is directory not a file.")));
  }

  @Test
  public void reading_not_written_value_fails() {
    when(outputsDb()).read(hash, stringType());
    thenThrown(OutputsDbException.class);
  }

  @Test
  public void written_messages_can_be_read_back() {
    given(stringValue = string("abc"));
    given(message = errorMessage("error message"));
    given(messages = array(message));
    given(outputsDb()).write(hash, new Output(stringValue, messages));
    when(outputsDb().read(hash, stringType()).messages());
    thenReturned(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() {
    given(file = file(path, bytes));
    given(array = arrayBuilder(types().file()).add(file).build());
    given(outputsDb()).write(hash, new Output(array, emptyMessageArray()));
    when(((Array) outputsDb().read(hash, arrayType(types().file())).result())
        .asIterable(Struct.class).iterator().next());
    thenReturned(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    given(blob = blob(bytes));
    given(array = arrayBuilder(blobType()).add(blob).build());
    given(outputsDb()).write(hash, new Output(array, emptyMessageArray()));
    when(((Array) outputsDb().read(hash, arrayType(blobType())).result())
        .asIterable(Blob.class).iterator().next());
    thenReturned(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() {
    given(boolValue = bool(true));
    given(array = arrayBuilder(boolType()).add(boolValue).build());
    given(outputsDb()).write(hash, new Output(array, emptyMessageArray()));
    when(((Array) outputsDb().read(hash, arrayType(boolType())).result())
        .asIterable(Bool.class).iterator().next());
    thenReturned(boolValue);
  }

  @Test
  public void written_string_array_can_be_read_back() {
    given(stringValue = string(string));
    given(array = arrayBuilder(stringType()).add(stringValue).build());
    given(outputsDb()).write(hash, new Output(array, emptyMessageArray()));
    when(((Array) outputsDb().read(hash, arrayType(stringType())).result())
        .asIterable(SString.class).iterator().next());
    thenReturned(stringValue);
  }

  @Test
  public void written_file_can_be_read_back() {
    given(file = file(path, bytes));
    given(outputsDb()).write(hash, new Output(file, emptyMessageArray()));
    when(outputsDb().read(hash, types().file()).result());
    thenReturned(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    given(blob = blob(bytes));
    given(outputsDb()).write(hash, new Output(blob, emptyMessageArray()));
    when(outputsDb().read(hash, blobType()).result());
    thenReturned(blob);
  }

  @Test
  public void written_bool_can_be_read_back() {
    given(boolValue = bool(true));
    given(outputsDb()).write(hash, new Output(boolValue, emptyMessageArray()));
    when(((Bool) outputsDb().read(hash, boolType()).result()).data());
    thenReturned(true);
  }

  @Test
  public void written_string_can_be_read_back() {
    given(stringValue = string(string));
    given(outputsDb()).write(hash, new Output(stringValue, emptyMessageArray()));
    when(((SString) outputsDb().read(hash, stringType()).result()).data());
    thenReturned(string);
  }
}
