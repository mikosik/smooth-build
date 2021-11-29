package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsDecodeObjNodeException;
import static org.smoothbuild.db.object.obj.Helpers.wrapObjectDbExceptionAsDecodeObjNodeException;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.obj.Helpers.HashedDbCallable;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeException;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjSequenceException;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.TypeH;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public abstract class ObjectH {
  public static final String DATA_PATH = "data";

  private final MerkleRoot merkleRoot;
  private final ObjectHDb objectHDb;

  public ObjectH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    this.merkleRoot = merkleRoot;
    this.objectHDb = objectHDb;
  }

  protected MerkleRoot merkleRoot() {
    return merkleRoot;
  }

  protected ObjectHDb objectDb() {
    return objectHDb;
  }

  protected HashedDb hashedDb() {
    return objectHDb.hashedDb();
  }

  public Hash hash() {
    return merkleRoot.hash();
  }

  public Hash dataHash() {
    return merkleRoot.dataHash();
  }

  public SpecH spec() {
    return merkleRoot.spec();
  }

  public abstract TypeH type();

  public abstract String valToString();

  @Override
  public boolean equals(Object object) {
    return (object instanceof ObjectH that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return valToStringSafe() + "@" + hash();
  }

  protected <T> T readData(HashedDbCallable<T> reader) {
    return wrapHashedDbExceptionAsDecodeObjNodeException(hash(), spec(), DATA_PATH, reader);
  }

  protected <T> T readObj(String path, Hash hash, Class<T> clazz) {
    ObjectH obj = wrapObjectDbExceptionAsDecodeObjNodeException(
        hash(), spec(), path, () -> objectDb().get(hash));
    return castObj(obj, path, clazz);
  }

  protected <T> T readSequenceElementObj(String path, Hash hash, int i, int expectedSize,
      Class<T> clazz) {
    Hash elemHash = readSequenceElementHash(path, hash, i, expectedSize);
    ObjectH obj = wrapObjectDbExceptionAsDecodeObjNodeException(
        hash(), spec(), path, i, () -> objectDb().get(elemHash));
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

  protected ImmutableList<ObjectH> readSequenceObjs(String path, Hash hash) {
    var sequenceHashes = readSequenceHashes(path, hash);
    return readSequenceObjs(path, sequenceHashes);
  }

  private ImmutableList<ObjectH> readSequenceObjs(String path, ImmutableList<Hash> sequence) {
    Builder<ObjectH> builder = ImmutableList.builder();
    for (int i = 0; i < sequence.size(); i++) {
      int index = i;
      ObjectH obj = wrapObjectDbExceptionAsDecodeObjNodeException(hash(), spec(), path, index,
          () -> objectHDb.get(sequence.get(index)));
      builder.add(obj);
    }
    return builder.build();
  }

  private ImmutableList<Hash> readSequenceHashes(String path, Hash hash, int expectedSize) {
    ImmutableList<Hash> data = readSequenceHashes(path, hash);
    if (data.size() != expectedSize) {
      throw new UnexpectedObjSequenceException(hash(), spec(), path, expectedSize, data.size());
    }
    return data;
  }

  private ImmutableList<Hash> readSequenceHashes(String path, Hash hash) {
    return wrapHashedDbExceptionAsDecodeObjNodeException(hash(), spec(), path,
        () -> objectHDb.readSequence(hash));
  }

  protected static String sequenceToString(ImmutableList<? extends ObjectH> objects) {
    return toCommaSeparatedString(objects, ObjectH::valToStringSafe);
  }

  private <T> T castObj(ObjectH obj, String path, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      @SuppressWarnings("unchecked")
      T result = (T) obj;
      return result;
    } else {
      throw new UnexpectedObjNodeException(hash(), spec(), path, clazz, obj.getClass());
    }
  }


  private <T> T castObj(ObjectH obj, String path, int index, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      @SuppressWarnings("unchecked")
      T result = (T) obj;
      return result;
    } else {
      throw new UnexpectedObjNodeException(hash(), spec(), path, index, clazz, obj.getClass());
    }
  }

  private <T> ImmutableList<T> castSequence(
      ImmutableList<ObjectH> elems, String path, Class<T> clazz) {
    for (int i = 0; i < elems.size(); i++) {
      ObjectH elem = elems.get(i);
      if (!clazz.isInstance(elem)) {
        throw new UnexpectedObjNodeException(hash(), spec(), path, i, clazz, elem.getClass());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
  }

  private String valToStringSafe() {
    try {
      return valToString();
    } catch (DecodeObjNodeException e) {
      return "?Exception?@" + hash();
    }
  }
}
