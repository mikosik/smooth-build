package org.smoothbuild.db.hashed;

import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.After;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.CorruptedBoolError;
import org.smoothbuild.db.hashed.err.CorruptedEnumValue;
import org.smoothbuild.db.hashed.err.IllegalPathInObjectError;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.db.hashed.err.TooFewBytesToUnmarshallValue;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

import com.google.common.hash.HashCode;

public class UnmarshallerTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final FakeFileSystem fileSystem = new FakeFileSystem();
  private final HashedDb hashedDb = new HashedDb(fileSystem);
  private SString hashed1;
  private SString hashed2;
  private Marshaller marshaller;
  private Unmarshaller unmarshaller;
  private Path path;
  private HashCode hash;
  private byte myByte;
  private int myInt;
  private EnumValues<String> enumValues;

  @After
  public void after() {
    if (unmarshaller != null) {
      unmarshaller.close();
    }
  }

  @Test
  public void marshalled_hashed_list_can_be_unmarshalled() {
    given(hashed1 = objectsDb.string("abc"));
    given(hashed2 = objectsDb.string("def"));
    given(marshaller = new Marshaller());
    given(marshaller).write(asList(hashed1, hashed2));
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller.readHashList());
    thenReturned(asList(hashed1.hash(), hashed2.hash()));
  }

  @Test
  public void marshalled_path_can_be_unmarshalled() {
    given(path = path("my/path"));
    given(marshaller = new Marshaller());
    given(marshaller).write(path);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readPath();
    thenReturned(path);
  }

  @Test
  public void too_short_path_in_db_causes_exception() throws Exception {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(10);
    given(marshaller).write(0x12345678);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readPath();
    thenThrown(TooFewBytesToUnmarshallValue.class);
  }

  @Test
  public void halfed_size_of_path_in_db_causes_exception() throws Exception {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write((byte) 1);
    given(marshaller).write((byte) 1);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readPath();
    thenThrown(TooFewBytesToUnmarshallValue.class);
  }

  @Test
  public void illegal_path_causes_exception() throws Exception {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write("/");
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readPath();
    thenThrown(IllegalPathInObjectError.class);
  }

  @Test
  public void marshalled_hash_can_be_unmarshalled() {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(hash);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readHash();
    thenReturned(hash);
  }

  @Test
  public void too_short_hash_in_db_causes_exception() {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(0x12345678);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readHash();
    thenThrown(TooFewBytesToUnmarshallValue.class);
  }

  @Test
  public void marshalled_false_bool_value_can_be_unmarshalled() {
    given(marshaller = new Marshaller());
    given(marshaller).write(false);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readBool();
    thenReturned(false);
  }

  @Test
  public void marshalled_true_bool_value_can_be_unmarshalled() {
    given(marshaller = new Marshaller());
    given(marshaller).write(true);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readBool();
    thenReturned(true);
  }

  @Test
  public void unmarshalling_corrupted_bool_throws_exception() {
    given(marshaller = new Marshaller());
    given(marshaller).write(33);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readBool();
    thenThrown(CorruptedBoolError.class);
  }

  @Test
  public void marshalled_byte_can_be_unmarshalled() {
    given(myByte = 123);
    given(marshaller = new Marshaller());
    given(marshaller).write(myByte);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readByte();
    thenReturned((byte) 123);
  }

  @Test
  public void marshalled_int_can_be_unmarshalled() {
    given(myInt = 0x12345678);
    given(marshaller = new Marshaller());
    given(marshaller).write(myInt);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readInt();
    thenReturned(myInt);
  }

  @Test
  public void too_short_int_in_db_causes_exception() throws Exception {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write((byte) 1);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readHash();
    thenThrown(TooFewBytesToUnmarshallValue.class);
  }

  @Test
  public void marshalled_enum_can_be_unmarshalled() throws Exception {
    given(enumValues = new EnumValues<>("abc", "def", "ghi"));
    given(marshaller = new Marshaller());
    given(marshaller).write(enumValues.valueToByte("def"));
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readEnum(enumValues);
    thenReturned("def");
  }

  @Test
  public void unmarshalling_enum_throws_corrupted_enum_exception_when_db_is_corrupted()
      throws Exception {
    given(enumValues = new EnumValues<>("abc", "def", "ghi"));
    given(marshaller = new Marshaller());
    given(marshaller).write((byte) 100);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readEnum(enumValues);
    thenThrown(CorruptedEnumValue.class);
  }

  @Test
  public void marshalled_all_type_of_objects_can_be_unmarshalled() {
    given(myInt = 0x12345678);
    given(myByte = 123);
    given(path = path("my/path"));
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(myInt);
    given(marshaller).write(myByte);
    given(marshaller).write(path);
    given(marshaller).write(hash);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readInt();
    thenReturned(myInt);
    when(unmarshaller).readByte();
    thenReturned(myByte);
    when(unmarshaller).readPath();
    thenReturned(path);
    when(unmarshaller).readHash();
    thenReturned(hash);
  }

  @SuppressWarnings("resource")
  @Test(expected = NoObjectWithGivenHashError.class)
  public void unmarshallling_not_stored_value_fails() throws Exception {
    new Unmarshaller(hashedDb, Hash.string("abc"));
  }
}
