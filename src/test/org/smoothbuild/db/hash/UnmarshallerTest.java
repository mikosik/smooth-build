package org.smoothbuild.db.hash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.db.hash.HashCodes.toPath;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;

import java.io.DataOutputStream;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.smoothbuild.db.hash.err.CorruptedBoolError;
import org.smoothbuild.db.hash.err.IllegalPathInObjectError;
import org.smoothbuild.db.hash.err.NoObjectWithGivenHashError;
import org.smoothbuild.db.hash.err.TooFewBytesToUnmarshallValue;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

import com.google.common.hash.HashCode;

public class UnmarshallerTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  HashedDb hashedDb = new HashedDb(fileSystem);

  @Test
  public void marshalled_path_can_be_unmarshalled() {
    Path path = path("my/path");

    Marshaller marshaller = new Marshaller();
    marshaller.addPath(path);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    Path actual = unmarshaller.readPath();
    unmarshaller.close();

    assertThat(actual).isEqualTo(path);
  }

  @Test
  public void marshalled_hash_can_be_unmarshalled() {
    HashCode myHash = Hash.function().hashInt(33);

    Marshaller marshaller = new Marshaller();
    marshaller.write(myHash);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    HashCode actual = unmarshaller.readHash();
    unmarshaller.close();

    assertThat(actual).isEqualTo(myHash);
  }

  @Test
  public void marshalled_false_bool_value_can_be_unmarshalled() {
    boolean myBool = false;
    Marshaller marshaller = new Marshaller();
    marshaller.write(myBool);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    boolean actual = unmarshaller.readBool();
    unmarshaller.close();

    assertThat(actual).isEqualTo(myBool);
  }

  @Test
  public void marshalled_true_bool_value_can_be_unmarshalled() {
    boolean myBool = true;
    Marshaller marshaller = new Marshaller();
    marshaller.write(myBool);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    boolean actual = unmarshaller.readBool();
    unmarshaller.close();

    assertThat(actual).isEqualTo(myBool);
  }

  @Test
  public void unmarshalling_corrupted_bool_throws_exception() {
    Marshaller marshaller = new Marshaller();
    marshaller.write((byte) 7);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash)) {
      unmarshaller.readBool();
    } catch (ErrorMessageException e) {
      assertThat(e, containsInstanceOf(CorruptedBoolError.class));
    }
  }

  @Test
  public void marshalled_byte_can_be_unmarshalled() {
    byte myByte = 123;
    Marshaller marshaller = new Marshaller();
    marshaller.write(myByte);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    byte actual = unmarshaller.readByte();
    unmarshaller.close();

    assertThat(actual).isEqualTo(myByte);
  }

  @Test
  public void marshalled_int_can_be_unmarshalled() {
    int myInt = 0x12345678;
    Marshaller marshaller = new Marshaller();
    marshaller.write(myInt);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    int actual = unmarshaller.readInt();
    unmarshaller.close();

    assertThat(actual).isEqualTo(myInt);
  }

  @Test
  public void marshalled_all_type_of_objects_can_be_unmarshalled() {
    HashCode myHash = Hash.function().hashInt(33);
    Path path = path("my/path");
    byte myByte = 123;
    int myInt = 0x12345667;

    Marshaller marshaller = new Marshaller();
    marshaller.write(myHash);
    marshaller.addPath(path);
    marshaller.write(myByte);
    marshaller.write(myInt);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    HashCode actualHash = unmarshaller.readHash();
    Path actualPath = unmarshaller.readPath();
    byte actualByte = unmarshaller.readByte();
    int actualInt = unmarshaller.readInt();
    unmarshaller.close();

    assertThat(actualHash).isEqualTo(myHash);
    assertThat(actualPath).isEqualTo(path);
    assertThat(actualByte).isEqualTo(myByte);
    assertThat(actualInt).isEqualTo(myInt);
  }

  @Test
  public void too_short_hash_in_db_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    try (DataOutputStream outputStream = new DataOutputStream(
        fileSystem.openOutputStream(objectPath))) {
      outputStream.write(new byte[Hash.size() - 1]);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readHash();
      } catch (ErrorMessageException e) {
        assertThat(containsInstanceOf(TooFewBytesToUnmarshallValue.class).matches(e)).isTrue();
      }
    }
  }

  @Test
  public void too_short_path_in_db_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    try (DataOutputStream outputStream = new DataOutputStream(
        fileSystem.openOutputStream(objectPath))) {
      int size = 10;
      outputStream.writeInt(size + 1);
      outputStream.write(new byte[size]);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readPath();
      } catch (ErrorMessageException e) {
        assertThat(containsInstanceOf(TooFewBytesToUnmarshallValue.class).matches(e)).isTrue();
      }
    }
  }

  @Test
  public void halfed_size_of_path_in_db_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    try (DataOutputStream outputStream = new DataOutputStream(
        fileSystem.openOutputStream(objectPath))) {
      outputStream.write(new byte[3]);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readPath();
      } catch (ErrorMessageException e) {
        assertThat(containsInstanceOf(TooFewBytesToUnmarshallValue.class).matches(e)).isTrue();
      }
    }
  }

  @Test
  public void illegal_path_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    String illegalPathValue = "/";

    try (DataOutputStream outputStream = new DataOutputStream(
        fileSystem.openOutputStream(objectPath))) {
      byte[] bytes = illegalPathValue.getBytes();
      outputStream.writeInt(bytes.length);
      outputStream.write(bytes);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readPath();
      } catch (ErrorMessageException e) {
        assertThat(containsInstanceOf(IllegalPathInObjectError.class).matches(e)).isTrue();
      }
    }
  }

  @Test
  public void too_short_int_in_db_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    try (DataOutputStream outputStream = new DataOutputStream(
        fileSystem.openOutputStream(objectPath))) {
      outputStream.write(new byte[3]);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readInt();
      } catch (ErrorMessageException e) {
        assertThat(containsInstanceOf(TooFewBytesToUnmarshallValue.class).matches(e)).isTrue();
      }
    }
  }

  @Test
  public void unmarshallling_not_stored_value_fails() throws Exception {
    try {
      new Unmarshaller(hashedDb, HashCode.fromInt(33));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      MatcherAssert.assertThat(e, containsInstanceOf(NoObjectWithGivenHashError.class));
    }
  }
}
