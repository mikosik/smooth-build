package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsDecodeObjNodeException;
import static org.smoothbuild.db.object.obj.Helpers.wrapObjectDbExceptionAsDecodeObjNodeException;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.obj.Helpers.HashedDbCallable;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeCatExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongSeqSizeExc;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.TypeH;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Object.
 * This class is thread-safe.
 */
public abstract class ObjH {
  public static final String DATA_PATH = "data";

  private final MerkleRoot merkleRoot;
  private final ObjDb objDb;

  public ObjH(MerkleRoot merkleRoot, ObjDb objDb) {
    this.merkleRoot = merkleRoot;
    this.objDb = objDb;
  }

  protected MerkleRoot merkleRoot() {
    return merkleRoot;
  }

  protected ObjDb objDb() {
    return objDb;
  }

  protected HashedDb hashedDb() {
    return objDb.hashedDb();
  }

  public Hash hash() {
    return merkleRoot.hash();
  }

  public Hash dataHash() {
    return merkleRoot.dataHash();
  }

  public CatH cat() {
    return merkleRoot.cat();
  }

  public abstract TypeH type();

  public abstract String objToString();

  @Override
  public boolean equals(Object object) {
    return (object instanceof ObjH that) && Objects.equals(hash(), that.hash());
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
    return wrapHashedDbExceptionAsDecodeObjNodeException(hash(), cat(), DATA_PATH, reader);
  }

  protected <T> T readObj(String path, Hash hash, Class<T> clazz) {
    var obj = wrapObjectDbExceptionAsDecodeObjNodeException(
        hash(), cat(), path, () -> objDb().get(hash));
    return castObj(obj, path, clazz);
  }

  protected <T> T readSeqElemObj(String path, Hash hash, int i, int expectedSize, Class<T> clazz) {
    var objH = readSeqElemObj(path, hash, i, expectedSize);
    return castObj(objH, path, i, clazz);
  }

  protected ObjH readSeqElemObj(String path, Hash hash, int i, int expectedSize, TypeH type) {
    var objH = readSeqElemObj(path, hash, i, expectedSize);
    return validateType(objH, DATA_PATH, i, type);
  }

  private ObjH readSeqElemObj(String path, Hash hash, int i, int expectedSize) {
    var elemHash = readSeqElemHash(path, hash, i, expectedSize);
    var obj = wrapObjectDbExceptionAsDecodeObjNodeException(
        hash(), cat(), path, i, () -> objDb().get(elemHash));
    return obj;
  }

  protected Hash readSeqElemHash(String path, Hash hash, int i, int expectedSize) {
    checkElementIndex(i, expectedSize);
    return readSeqHashes(path, hash, expectedSize)
        .get(i);
  }

  protected <T> ImmutableList<T> readSeqObjs(
      String path, Hash hash, int expectedSize, Class<T> clazz) {
    var seqHashes = readSeqHashes(path, hash, expectedSize);
    var objs = readSeqObjs(path, seqHashes);
    return castSeq(objs, path, clazz);
  }

  protected <T> ImmutableList<T> readSeqObjs(String path, Hash hash, Class<T> clazz) {
    var objs = readSeqObjs(path, hash);
    return castSeq(objs, path, clazz);
  }

  protected ImmutableList<ObjH> readSeqObjs(String path, Hash hash) {
    var seqHashes = readSeqHashes(path, hash);
    return readSeqObjs(path, seqHashes);
  }

  private ImmutableList<ObjH> readSeqObjs(String path, ImmutableList<Hash> seq) {
    Builder<ObjH> builder = ImmutableList.builder();
    for (int i = 0; i < seq.size(); i++) {
      int index = i;
      ObjH obj = wrapObjectDbExceptionAsDecodeObjNodeException(hash(), cat(), path, index,
          () -> objDb.get(seq.get(index)));
      builder.add(obj);
    }
    return builder.build();
  }

  private ImmutableList<Hash> readSeqHashes(String path, Hash hash, int expectedSize) {
    ImmutableList<Hash> data = readSeqHashes(path, hash);
    if (data.size() != expectedSize) {
      throw new DecodeObjWrongSeqSizeExc(hash(), cat(), path, expectedSize, data.size());
    }
    return data;
  }

  private ImmutableList<Hash> readSeqHashes(String path, Hash hash) {
    return wrapHashedDbExceptionAsDecodeObjNodeException(hash(), cat(), path,
        () -> objDb.readSeq(hash));
  }

  protected static String seqToString(ImmutableList<? extends ObjH> objects) {
    return toCommaSeparatedString(objects, ObjH::valToStringSafe);
  }

  private <T> T castObj(ObjH obj, String path, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      @SuppressWarnings("unchecked")
      T result = (T) obj;
      return result;
    } else {
      throw new DecodeObjWrongNodeCatExc(hash(), cat(), path, clazz, obj.getClass());
    }
  }

  private <T> T castObj(ObjH obj, String path, int index, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      @SuppressWarnings("unchecked")
      T result = (T) obj;
      return result;
    } else {
      throw new DecodeObjWrongNodeCatExc(hash(), cat(), path, index, clazz, obj.getClass());
    }
  }

  private <T> ImmutableList<T> castSeq(ImmutableList<ObjH> elems, String path, Class<T> clazz) {
    for (int i = 0; i < elems.size(); i++) {
      ObjH elem = elems.get(i);
      if (!clazz.isInstance(elem)) {
        throw new DecodeObjWrongNodeCatExc(hash(), cat(), path, i, clazz, elem.getClass());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
  }

  protected ObjH validateType(ObjH obj, String path, int index, TypeH expectedT) {
    var objT = obj.type();
    if (!objT.equals(expectedT)) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), path, index, expectedT, objT);
    }
    return obj;
  }

  private String valToStringSafe() {
    try {
      return objToString();
    } catch (DecodeObjNodeExc e) {
      return "?Exception?@" + hash();
    }
  }
}
