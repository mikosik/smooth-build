package org.smoothbuild.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.db.HashCodes.toPath;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;

import java.io.DataOutputStream;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.smoothbuild.db.err.IllegalPathInObjectError;
import org.smoothbuild.db.err.NoObjectWithGivenHashError;
import org.smoothbuild.db.err.TooFewBytesToUnmarshallValue;
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
    marshaller.addHash(myHash);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    HashCode actual = unmarshaller.readHash();
    unmarshaller.close();

    assertThat(actual).isEqualTo(myHash);
  }

  @Test
  public void marshalled_int_can_be_unmarshalled() {
    int myInt = 0x12345678;
    Marshaller marshaller = new Marshaller();
    marshaller.addInt(myInt);
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
    int myInt = 0x12345667;

    Marshaller marshaller = new Marshaller();
    marshaller.addHash(myHash);
    marshaller.addPath(path);
    marshaller.addInt(myInt);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);
    HashCode actualHash = unmarshaller.readHash();
    Path actualPath = unmarshaller.readPath();
    int actualInt = unmarshaller.readInt();
    unmarshaller.close();

    assertThat(actualHash).isEqualTo(myHash);
    assertThat(actualPath).isEqualTo(path);
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
