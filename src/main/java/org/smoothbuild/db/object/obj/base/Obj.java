package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsDecodeObjNodeException;
import static org.smoothbuild.db.object.obj.Helpers.wrapObjectDbExceptionAsDecodeObjNodeException;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.obj.Helpers.HashedDbCallable;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeException;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjSequenceException;
import org.smoothbuild.db.object.type.base.ObjType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public abstract class Obj {
  public static final String DATA_PATH = "data";

  private final MerkleRoot merkleRoot;
  private final ObjectDb objectDb;

  public Obj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    this.merkleRoot = merkleRoot;
    this.objectDb = objectDb;
  }

  protected MerkleRoot merkleRoot() {
    return merkleRoot;
  }

  protected ObjectDb objectDb() {
    return objectDb;
  }

  protected HashedDb hashedDb() {
    return objectDb.hashedDb();
  }

  public Hash hash() {
    return merkleRoot.hash();
  }

  public Hash dataHash() {
    return merkleRoot.dataHash();
  }

  public ObjType type() {
    return merkleRoot.type();
  }

  public abstract String valueToString();

  @Override
  public boolean equals(Object object) {
    return (object instanceof Obj that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return valueToStringSafe() + "@" + hash();
  }

  protected <T> T readData(HashedDbCallable<T> reader) {
    return wrapHashedDbExceptionAsDecodeObjNodeException(hash(), type(), DATA_PATH, reader);
  }

  protected <T> T readObj(String path, Hash hash, Class<T> clazz) {
    Obj obj = wrapObjectDbExceptionAsDecodeObjNodeException(
        hash(), type(), path, () -> objectDb().get(hash));
    return castObj(obj, path, clazz);
  }

  protected <T> T readSequenceElementObj(String path, Hash hash, int i, int expectedSize,
      Class<T> clazz) {
    Hash elementHash = readSequenceElementHash(path, hash, i, expectedSize);
    Obj obj = wrapObjectDbExceptionAsDecodeObjNodeException(
        hash(), type(), path, i, () -> objectDb().get(elementHash));
    return castObj(obj, path, i, clazz);
  }

  protected Hash readSequenceElementHash(String path, Hash hash, int i, int expectedSize) {
    checkElementIndex(i, expectedSize);
    return readSequenceHashes(path, hash, expectedSize)
        .get(i);
  }

  protected <T> ImmutableList<T> readSequenceObjs(String path, Hash hash, int expectedSize,
      Class<T> clazz) {
    var sequenceHashes = readSequenceHashes(path, hash, expectedSize);
    var objs = readSequenceObjs(path, sequenceHashes);
    return castSequence(objs, path, clazz);
  }

  protected <T> ImmutableList<T> readSequenceObjs(String path, Hash hash, Class<T> clazz) {
    var objs = readSequenceObjs(path, hash);
    return castSequence(objs, path, clazz);
  }

  protected ImmutableList<Obj> readSequenceObjs(String path, Hash hash) {
    var sequenceHashes = readSequenceHashes(path, hash);
    return readSequenceObjs(path, sequenceHashes);
  }

  private ImmutableList<Obj> readSequenceObjs(String path, ImmutableList<Hash> sequence) {
    Builder<Obj> builder = ImmutableList.builder();
    for (int i = 0; i < sequence.size(); i++) {
      int index = i;
      Obj obj = wrapObjectDbExceptionAsDecodeObjNodeException(hash(), type(), path, index,
          () -> objectDb.get(sequence.get(index)));
      builder.add(obj);
    }
    return builder.build();
  }

  private ImmutableList<Hash> readSequenceHashes(String path, Hash hash, int expectedSize) {
    ImmutableList<Hash> data = readSequenceHashes(path, hash);
    if (data.size() != expectedSize) {
      throw new UnexpectedObjSequenceException(hash(), type(), path, expectedSize, data.size());
    }
    return data;
  }

  private ImmutableList<Hash> readSequenceHashes(String path, Hash hash) {
    return wrapHashedDbExceptionAsDecodeObjNodeException(hash(), type(), path,
        () -> objectDb.readSequence(hash));
  }

  protected static String sequenceToString(ImmutableList<? extends Obj> objects) {
    return toCommaSeparatedString(objects, Obj::valueToStringSafe);
  }

  private <T> T castObj(Obj obj, String path, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      @SuppressWarnings("unchecked")
      T result = (T) obj;
      return result;
    } else {
      throw new UnexpectedObjNodeException(hash(), type(), path, clazz, obj.getClass());
    }
  }


  private <T> T castObj(Obj obj, String path, int index, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      @SuppressWarnings("unchecked")
      T result = (T) obj;
      return result;
    } else {
      throw new UnexpectedObjNodeException(hash(), type(), path, index, clazz, obj.getClass());
    }
  }

  private <T> ImmutableList<T> castSequence(
      ImmutableList<Obj> elements, String path, Class<T> clazz) {
    for (int i = 0; i < elements.size(); i++) {
      Obj element = elements.get(i);
      if (!clazz.isInstance(element)) {
        throw new UnexpectedObjNodeException(hash(), type(), path, i, clazz, element.getClass());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elements;
    return result;
  }

  private String valueToStringSafe() {
    try {
      return valueToString();
    } catch (DecodeObjNodeException e) {
      return "?Exception?@" + hash();
    }
  }
}
