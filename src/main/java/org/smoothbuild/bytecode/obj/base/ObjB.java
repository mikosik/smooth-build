package org.smoothbuild.bytecode.obj.base;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.bytecode.obj.Helpers.wrapHashedDbExcAsDecodeObjNodeException;
import static org.smoothbuild.bytecode.obj.Helpers.wrapObjDbExcAsDecodeObjNodeException;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Objects;

import org.smoothbuild.bytecode.obj.Helpers.HashedDbCallable;
import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeClassExc;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongSeqSizeExc;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.db.HashedDb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Bytecode object.
 * This class is thread-safe.
 */
public abstract class ObjB {
  public static final String DATA_PATH = "data";

  private final MerkleRoot merkleRoot;
  private final ObjDbImpl objDb;

  public ObjB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    this.merkleRoot = merkleRoot;
    this.objDb = objDb;
  }

  protected MerkleRoot merkleRoot() {
    return merkleRoot;
  }

  protected ObjDbImpl objDb() {
    return objDb;
  }

  protected HashedDb hashedDb() {
    return objDb.hashedDb();
  }

  protected TypingB typing() {
    return objDb.typing();
  }

  public Hash hash() {
    return merkleRoot.hash();
  }

  public Hash dataHash() {
    return merkleRoot.dataHash();
  }

  public CatB cat() {
    return merkleRoot.cat();
  }

  public abstract TypeB type();

  public abstract String objToString();

  @Override
  public boolean equals(Object object) {
    return (object instanceof ObjB that) && Objects.equals(hash(), that.hash());
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
    return wrapHashedDbExcAsDecodeObjNodeException(hash(), cat(), DATA_PATH, reader);
  }

  protected <T> T readObj(String path, Hash hash, Class<T> clazz) {
    var obj = wrapObjDbExcAsDecodeObjNodeException(
        hash(), cat(), path, () -> objDb().get(hash));
    return castObj(obj, path, clazz);
  }

  protected <T> T readSeqElemObj(String path, Hash hash, int i, int expectedSize, Class<T> clazz) {
    var obj = readSeqElemObj(path, hash, i, expectedSize);
    return castObj(obj, path, i, clazz);
  }

  protected ObjB readSeqElemObjWithType(
      String path, Hash hash, int i, int expectedSize, TypeB type) {
    var obj = readSeqElemObj(path, hash, i, expectedSize);
    return validateType(obj, DATA_PATH, i, type);
  }

  private ObjB readSeqElemObj(String path, Hash hash, int i, int expectedSize) {
    var elemHash = readSeqElemHash(path, hash, i, expectedSize);
    var obj = wrapObjDbExcAsDecodeObjNodeException(
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

  protected ImmutableList<ObjB> readSeqObjs(String path, Hash hash) {
    var seqHashes = readSeqHashes(path, hash);
    return readSeqObjs(path, seqHashes);
  }

  private ImmutableList<ObjB> readSeqObjs(String path, ImmutableList<Hash> seq) {
    Builder<ObjB> builder = ImmutableList.builder();
    for (int i = 0; i < seq.size(); i++) {
      int index = i;
      ObjB obj = wrapObjDbExcAsDecodeObjNodeException(hash(), cat(), path, index,
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
    return wrapHashedDbExcAsDecodeObjNodeException(hash(), cat(), path,
        () -> objDb.hashedDb().readSeq(hash));
  }

  protected static String objsToString(ImmutableList<? extends ObjB> objs) {
    return toCommaSeparatedString(objs, ObjB::valToStringSafe);
  }

  private <T> T castObj(ObjB obj, String path, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      @SuppressWarnings("unchecked")
      T result = (T) obj;
      return result;
    } else {
      throw new DecodeObjWrongNodeClassExc(hash(), cat(), path, clazz, obj.getClass());
    }
  }

  private <T> T castObj(ObjB obj, String path, int index, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      @SuppressWarnings("unchecked")
      T result = (T) obj;
      return result;
    } else {
      throw new DecodeObjWrongNodeClassExc(hash(), cat(), path, index, clazz, obj.getClass());
    }
  }

  private <T> ImmutableList<T> castSeq(ImmutableList<ObjB> elems, String path, Class<T> clazz) {
    for (int i = 0; i < elems.size(); i++) {
      ObjB elem = elems.get(i);
      if (!clazz.isInstance(elem)) {
        throw new DecodeObjWrongNodeClassExc(hash(), cat(), path, i, clazz, elem.getClass());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
  }

  protected ObjB validateType(ObjB obj, String path, int index, TypeB expectedT) {
    var objT = obj.type();
    if (!typing().isAssignable(expectedT, objT)) {
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
