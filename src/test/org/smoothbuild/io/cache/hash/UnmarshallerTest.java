package org.smoothbuild.io.cache.hash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.cache.hash.HashCodes.toPath;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.DataOutputStream;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.io.cache.hash.err.CorruptedBoolError;
import org.smoothbuild.io.cache.hash.err.CorruptedEnumValue;
import org.smoothbuild.io.cache.hash.err.IllegalPathInObjectError;
import org.smoothbuild.io.cache.hash.err.NoObjectWithGivenHashError;
import org.smoothbuild.io.cache.hash.err.TooFewBytesToUnmarshallValue;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Hashed;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.FakeHashed;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class UnmarshallerTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  HashedDb hashedDb = new HashedDb(fileSystem);

  @Test
  public void marshalled_hashed_list_can_be_unmarshalled() {
    Hashed hashed1 = new FakeHashed("abc");
    Hashed hashed2 = new FakeHashed("def");

    Marshaller marshaller = new Marshaller();
    marshaller.write(ImmutableList.of(hashed1, hashed2));
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      List<HashCode> actual = unmarshaller.readHashCodeList();
      assertThat(actual).isEqualTo(ImmutableList.of(hashed1.hash(), hashed2.hash()));
    }
  }

  @Test
  public void marshalled_path_can_be_unmarshalled() {
    Path path = path("my/path");

    Marshaller marshaller = new Marshaller();
    marshaller.write(path);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      Path actual = unmarshaller.readPath();
      assertThat(actual).isEqualTo(path);
    }
  }

  @Test
  public void marshalled_hash_can_be_unmarshalled() {
    HashCode myHash = Hash.function().hashInt(33);

    Marshaller marshaller = new Marshaller();
    marshaller.write(myHash);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      HashCode actual = unmarshaller.readHash();
      assertThat(actual).isEqualTo(myHash);
    }
  }

  @Test
  public void marshalled_false_bool_value_can_be_unmarshalled() {
    boolean myBool = false;
    Marshaller marshaller = new Marshaller();
    marshaller.write(myBool);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      boolean actual = unmarshaller.readBool();
      assertThat(actual).isEqualTo(myBool);
    }
  }

  @Test
  public void marshalled_true_bool_value_can_be_unmarshalled() {
    boolean myBool = true;
    Marshaller marshaller = new Marshaller();
    marshaller.write(myBool);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      boolean actual = unmarshaller.readBool();
      assertThat(actual).isEqualTo(myBool);
    }
  }

  @Test
  public void unmarshalling_corrupted_bool_throws_exception() {
    Marshaller marshaller = new Marshaller();
    marshaller.write((byte) 7);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash)) {
      unmarshaller.readBool();
      fail("exception should be thrown");
    } catch (CorruptedBoolError e) {
      // expected
    }
  }

  @Test
  public void marshalled_byte_can_be_unmarshalled() {
    byte myByte = 123;
    Marshaller marshaller = new Marshaller();
    marshaller.write(myByte);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      byte actual = unmarshaller.readByte();
      assertThat(actual).isEqualTo(myByte);
    }
  }

  @Test
  public void marshalled_int_can_be_unmarshalled() {
    int myInt = 0x12345678;
    Marshaller marshaller = new Marshaller();
    marshaller.write(myInt);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      int actual = unmarshaller.readInt();
      assertThat(actual).isEqualTo(myInt);
    }
  }

  @Test
  public void marshalled_enum_can_be_unmarshalled() throws Exception {
    String value1 = "abc";
    String value2 = "def";
    String value3 = "ghi";
    EnumValues<String> enumValues = new EnumValues<String>(value1, value2, value3);

    Marshaller marshaller = new Marshaller();
    marshaller.write(enumValues.valueToByte(value2));
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      String actual = unmarshaller.readEnum(enumValues);
      assertThat(actual).isEqualTo(value2);
    }
  }

  @Test
  public void unmarshalling_enum_throws_corrupted_enum_exception_when_db_is_corrupted()
      throws Exception {
    String value1 = "abc";
    String value2 = "def";
    String value3 = "ghi";
    EnumValues<String> enumValues = new EnumValues<String>(value1, value2, value3);

    Marshaller marshaller = new Marshaller();
    marshaller.write((byte) 100);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash)) {
      unmarshaller.readEnum(enumValues);
      fail("exception should be thrown");
    } catch (CorruptedEnumValue e) {
      // expected
    }
  }

  @Test
  public void marshalled_all_type_of_objects_can_be_unmarshalled() {
    HashCode myHash = Hash.function().hashInt(33);
    Path path = path("my/path");
    byte myByte = 123;
    int myInt = 0x12345667;

    Marshaller marshaller = new Marshaller();
    marshaller.write(myHash);
    marshaller.write(path);
    marshaller.write(myByte);
    marshaller.write(myInt);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      HashCode actualHash = unmarshaller.readHash();
      Path actualPath = unmarshaller.readPath();
      byte actualByte = unmarshaller.readByte();
      int actualInt = unmarshaller.readInt();

      assertThat(actualHash).isEqualTo(myHash);
      assertThat(actualPath).isEqualTo(path);
      assertThat(actualByte).isEqualTo(myByte);
      assertThat(actualInt).isEqualTo(myInt);
    }
  }

  @Test
  public void too_short_hash_in_db_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    try (DataOutputStream outputStream =
        new DataOutputStream(fileSystem.openOutputStream(objectPath))) {
      outputStream.write(new byte[Hash.size() - 1]);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readHash();
        fail("exception should be thrown");
      } catch (TooFewBytesToUnmarshallValue e) {
        // expected
      }
    }
  }

  @Test
  public void too_short_path_in_db_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    try (DataOutputStream outputStream =
        new DataOutputStream(fileSystem.openOutputStream(objectPath))) {
      int size = 10;
      outputStream.writeInt(size + 1);
      outputStream.write(new byte[size]);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readPath();
        fail("exception should be thrown");
      } catch (TooFewBytesToUnmarshallValue e) {
        // expected
      }
    }
  }

  @Test
  public void halfed_size_of_path_in_db_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    try (DataOutputStream outputStream =
        new DataOutputStream(fileSystem.openOutputStream(objectPath))) {
      outputStream.write(new byte[3]);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readPath();
        fail("exception should be thrown");
      } catch (TooFewBytesToUnmarshallValue e) {
        // expected
      }
    }
  }

  @Test
  public void illegal_path_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    String illegalPathValue = "/";

    try (DataOutputStream outputStream =
        new DataOutputStream(fileSystem.openOutputStream(objectPath))) {
      byte[] bytes = illegalPathValue.getBytes();
      outputStream.writeInt(bytes.length);
      outputStream.write(bytes);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readPath();
        fail("exception should be thrown");
      } catch (IllegalPathInObjectError e) {
        // expected
      }
    }
  }

  @Test
  public void too_short_int_in_db_causes_exception() throws Exception {
    HashCode objectHash = HashCode.fromInt(33);
    Path objectPath = toPath(objectHash);
    try (DataOutputStream outputStream =
        new DataOutputStream(fileSystem.openOutputStream(objectPath))) {
      outputStream.write(new byte[3]);
    }

    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, objectHash)) {
      try {
        unmarshaller.readInt();
        fail("exception should be thrown");
      } catch (TooFewBytesToUnmarshallValue e) {
        // expected
      }
    }
  }

  @Test
  public void unmarshallling_not_stored_value_fails() throws Exception {
    try {
      new Unmarshaller(hashedDb, HashCode.fromInt(33));
      fail("exception should be thrown");
    } catch (NoObjectWithGivenHashError e) {
      // expected
    }
  }
}
