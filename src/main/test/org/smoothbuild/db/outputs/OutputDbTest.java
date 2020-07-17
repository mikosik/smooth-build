package org.smoothbuild.db.outputs;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.outputs.OutputDbException.corruptedValueException;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.SObject;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.type.ArrayType;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class OutputDbTest extends TestingContext {
  private final Hash hash = Hash.of("abc");
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private Path path = path("file/path");

  private Array array;
  private Tuple file;
  private Blob blob;
  private Bool boolValue;
  private SString stringValue;
  private final String string = "some string";

  @Test
  public void outputdb_does_not_contain_not_written_result() throws Exception {
    assertThat(outputDb().contains(hash))
        .isFalse();
  }

  @Test
  public void outputdb_contains_written_result() throws Exception {
    outputDb().write(hash, new Output(string("result"), emptyMessageArray()));
    assertThat(outputDb().contains(hash))
        .isTrue();
  }

  @Test
  public void outputdb_is_corrupted_when_task_hash_points_to_directory() throws Exception {
    path = OutputDb.toPath(hash);
    outputDbFileSystem().sink(path.appendPart("file"));

    assertCall(() -> outputDb().contains(hash))
        .throwsException(corruptedValueException(hash, path + " is directory not a file."));
  }

  @Test
  public void reading_not_written_value_fails() {
    assertCall(() -> outputDb().read(hash, stringType()))
        .throwsException(OutputDbException.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    stringValue = string("abc");
    SObject message = errorMessage("error message");
    Array messages = array(message);
    outputDb().write(hash, new Output(stringValue, messages));

    assertThat(outputDb().read(hash, stringType()).messages())
        .isEqualTo(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    file = file(path, bytes);
    array = arrayBuilder(objectFactory().fileType()).add(file).build();
    outputDb().write(hash, new Output(array, emptyMessageArray()));
    ArrayType arrayType = arrayType(objectFactory().fileType());

    assertThat(((Array) outputDb().read(hash, arrayType).value()).asIterable(Tuple.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    blob = blob(bytes);
    array = arrayBuilder(blobType()).add(blob).build();
    outputDb().write(hash, new Output(array, emptyMessageArray()));
    ArrayType arrayType = arrayType(blobType());

    assertThat(((Array) outputDb().read(hash, arrayType).value()).asIterable(Blob.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    boolValue = bool(true);
    array = arrayBuilder(boolType()).add(boolValue).build();
    outputDb().write(hash, new Output(array, emptyMessageArray()));
    ArrayType arrayType = arrayType(boolType());

    assertThat(((Array) outputDb().read(hash, arrayType).value()).asIterable(Bool.class))
        .containsExactly(boolValue);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    stringValue = string(string);
    array = arrayBuilder(stringType()).add(stringValue).build();
    outputDb().write(hash, new Output(array, emptyMessageArray()));
    ArrayType arrayType = arrayType(stringType());

    assertThat(((Array) outputDb().read(hash, arrayType).value()).asIterable(SString.class))
        .containsExactly(stringValue);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    file = file(path, bytes);
    outputDb().write(hash, new Output(file, emptyMessageArray()));

    assertThat(outputDb().read(hash, objectFactory().fileType()).value())
        .isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    blob = blob(bytes);
    outputDb().write(hash, new Output(blob, emptyMessageArray()));

    assertThat(outputDb().read(hash, blobType()).value())
        .isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    boolValue = bool(true);
    outputDb().write(hash, new Output(boolValue, emptyMessageArray()));

    assertThat(((Bool) outputDb().read(hash, boolType()).value()).jValue())
        .isTrue();
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    stringValue = string(string);
    outputDb().write(hash, new Output(stringValue, emptyMessageArray()));
    assertThat(((SString) outputDb().read(hash, stringType()).value()).jValue())
        .isEqualTo(string);
  }
}
