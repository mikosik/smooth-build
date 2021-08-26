package org.smoothbuild.db.object.db;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.db.Helpers.wrapException;
import static org.smoothbuild.db.object.spec.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.SpecKind.INT;
import static org.smoothbuild.db.object.spec.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.SpecKind.TUPLE;
import static org.smoothbuild.db.object.spec.SpecKind.fromMarker;
import static org.smoothbuild.util.Lists.map;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.BlobBuilder;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Int;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.BlobSpec;
import org.smoothbuild.db.object.spec.BoolSpec;
import org.smoothbuild.db.object.spec.IntSpec;
import org.smoothbuild.db.object.spec.NothingSpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.SpecKind;
import org.smoothbuild.db.object.spec.StrSpec;
import org.smoothbuild.db.object.spec.TupleSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjectDb {
  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, Spec> specCache;

  /**
   * Following fields are effectively immutable - they are set only once in {@link #initialize()}
   * which is invoked before instance of ObjectDb is returned from factory method.
   */

  private BlobSpec blobSpec;
  private BoolSpec boolSpec;
  private IntSpec intSpec;
  private NothingSpec nothingSpec;
  private StrSpec strSpec;

  public static ObjectDb objectDb(HashedDb hashedDb) {
      ObjectDb objectDb = new ObjectDb(hashedDb);
      objectDb.initialize();
      return objectDb;
  }

  private ObjectDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.specCache = new ConcurrentHashMap<>();
  }

  private void initialize() {
    try {
      this.blobSpec = new BlobSpec(writeBaseSpecRoot(BLOB), hashedDb, this);
      this.boolSpec = new BoolSpec(writeBaseSpecRoot(BOOL), hashedDb, this);
      this.intSpec = new IntSpec(writeBaseSpecRoot(INT), hashedDb, this);
      this.nothingSpec = new NothingSpec(writeBaseSpecRoot(NOTHING), hashedDb, this);
      this.strSpec = new StrSpec(writeBaseSpecRoot(STRING), hashedDb, this);

      cacheSpec(blobSpec);
      cacheSpec(boolSpec);
      cacheSpec(intSpec);
      cacheSpec(nothingSpec);
      cacheSpec(strSpec);
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  // methods for creating objects or object builders

  public ArrayBuilder arrayBuilder(Spec elementSpec) {
    return new ArrayBuilder(arrayS(elementSpec), this);
  }

  public BlobBuilder blobBuilder() {
    return wrapException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool boolV(boolean value) {
    return wrapException(() -> newBoolV(value));
  }

  public Int intV(BigInteger value) {
    return wrapException(() -> newIntV(value));
  }

  public Str strV(String string) {
    return wrapException(() -> newStrV(string));
  }

  public Tuple tupleV(TupleSpec tupleSpec, Iterable<? extends Obj> elements) {
    List<Obj> elementsList = ImmutableList.copyOf(elements);
    var specs = tupleSpec.elementSpecs();
    if (specs.size() != elementsList.size()) {
      throw new IllegalArgumentException("tupleSpec specifies " + specs.size() +
          " elements but provided " + elementsList.size() + ".");
    }
    for (int i = 0; i < specs.size(); i++) {
      Spec specifiedSpec = specs.get(i);
      Spec elementSpec = elementsList.get(i).spec();
      if (!specifiedSpec.equals(elementSpec)) {
        throw new IllegalArgumentException("tupleSpec specifies element at index " + i
            + " with spec " + specifiedSpec + " but provided element has spec " + elementSpec
            + " at that index.");
      }
    }
    return wrapException(() -> newTupleV(tupleSpec, elementsList));
  }

  public Obj get(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 2);
      Spec spec = getSpecOrChainException(
          hashes.get(0), e -> new CannotDecodeObjectException(hash, e));
      Hash dataHash = hashes.get(1);
      return spec.newObj(new MerkleRoot(hash, spec, dataHash));
    } catch (HashedDbException e) {
      throw new CannotDecodeObjectException(hash, e);
    }
  }

  // methods for returning specs

  public ArraySpec arrayS(Spec elementSpec) {
    return cacheSpec(wrapException(() -> newArrayS(elementSpec)));
  }

  public BlobSpec blobS() {
    return blobSpec;
  }

  public BoolSpec boolS() {
    return boolSpec;
  }

  public IntSpec intS() {
    return intSpec;
  }

  public NothingSpec nothingS() {
    return nothingSpec;
  }

  public StrSpec strS() {
    return strSpec;
  }

  public TupleSpec tupleS(Iterable<? extends Spec> elementSpecs) {
    return cacheSpec(wrapException(() -> newTupleS(elementSpecs)));
  }

  private Spec getSpecOrChainException(
      Hash specHash, Function<Exception, RuntimeException> exceptionChainer) {
    try {
      return getSpec(specHash);
    } catch (ObjectDbException e) {
      throw exceptionChainer.apply(e);
    }
  }

  public Spec getSpec(Hash hash) {
    return requireNonNullElseGet(specCache.get(hash), () -> readSpec(hash));
  }

  private Spec readSpec(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
      byte marker = hashedDb.readByte(hashes.get(0));
      SpecKind specKind = fromMarker(marker);
      if (specKind == null) {
        throw new CannotDecodeSpecException(hash,
            "It has illegal SpecKind marker = " + marker + ".");
      }
      return switch (specKind) {
        case BLOB, BOOL, INT, NOTHING, STRING -> {
          assertSize(hash, specKind, hashes, 1);
          throw new RuntimeException(
              "Internal error: Spec with kind " + specKind + " should be found in cache.");
        }
        case ARRAY -> {
          assertSize(hash, ARRAY, hashes, 2);
          Spec elementSpec = getSpecOrChainException(
              hashes.get(1), e -> new CannotDecodeSpecException(hash));
          yield cacheSpec(newArrayS(hash, elementSpec));
        }
        case TUPLE -> {
          assertSize(hash, TUPLE, hashes, 2);
          ImmutableList<Spec> elements = readTupleSpecElementSpecs(hashes.get(1), hash);
          yield cacheSpec(newTupleS(hash, elements));
        }
      };
    } catch (HashedDbException e) {
      throw new CannotDecodeSpecException(hash, e);
    }
  }

  private static void assertSize(Hash hash, SpecKind specKind, List<Hash> hashes,
      int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new CannotDecodeSpecException(hash,
          "Its specKind == " + specKind + " but its merkle root has "
              + hashes.size() + " children when " + expectedSize + " is expected.");
    }
  }

  private ImmutableList<Spec> readTupleSpecElementSpecs(Hash hash, Hash parentHash) {
    var builder = ImmutableList.<Spec>builder();
    List<Hash> elementSpecHashes = readTupleSpecElementSpecHashes(hash, parentHash);
    for (int i = 0; i < elementSpecHashes.size(); i++) {
      try {
        builder.add(getSpec(elementSpecHashes.get(i)));
      } catch (ObjectDbException e) {
        throw new CannotDecodeSpecException(parentHash, "Its specKind == TUPLE "
            + "but reading element spec at index " + i + " caused error.", e);
      }
    }
    return builder.build();
  }

  private List<Hash> readTupleSpecElementSpecHashes(Hash hash, Hash parentHash) {
    try {
      return hashedDb.readHashes(hash);
    } catch (HashedDbException e) {
      throw new CannotDecodeSpecException(parentHash,
          "Its specKind == TUPLE but reading its element specs caused error.", e);
    }
  }

  private <T extends Spec> T cacheSpec(T spec) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(specCache.putIfAbsent(spec.hash(), spec), spec);
    return result;
  }

  // methods for creating Obj-s

  public Array newArrayV(ArraySpec spec, Iterable<? extends Obj> elements)
      throws HashedDbException {
    return spec.newObj(writeRoot(spec, writeArrayData(elements)));
  }

  public Blob newBlobV(Hash dataHash) throws HashedDbException {
    return blobSpec.newObj(writeRoot(blobSpec, dataHash));
  }

  private Bool newBoolV(boolean value) throws HashedDbException {
    return boolSpec.newObj(writeRoot(boolSpec, writeBoolData(value)));
  }

  private Int newIntV(BigInteger value) throws HashedDbException {
    return intSpec.newObj(writeRoot(intSpec, writeIntData(value)));
  }

  private Str newStrV(String string) throws HashedDbException {
    return strSpec.newObj(writeRoot(strSpec, writeStringData(string)));
  }

  private Tuple newTupleV(TupleSpec spec, List<?extends Obj> objects) throws HashedDbException {
    return spec.newObj(writeRoot(spec, writeTupleData(objects)));
  }

  // methods for creating Spec-s

  private ArraySpec newArrayS(Spec elementSpec) throws HashedDbException {
    Hash hash = writeArraySpecRoot(elementSpec);
    return newArrayS(hash, elementSpec);
  }

  private ArraySpec newArrayS(Hash hash, Spec elementSpec) {
    return new ArraySpec(hash, elementSpec, hashedDb, this);
  }

  private TupleSpec newTupleS(Iterable<? extends Spec> elementSpecs) throws HashedDbException {
    Hash hash = writeTupleSpecRoot(elementSpecs);
    return newTupleS(hash, elementSpecs);
  }

  private TupleSpec newTupleS(Hash hash, Iterable<? extends Spec> elementSpecs) {
    return new TupleSpec(hash, elementSpecs, hashedDb, this);
  }

  // methods for writing Merkle node(s) to HashedDb

  private MerkleRoot writeRoot(Spec spec, Hash dataHash) throws HashedDbException {
    Hash hash = hashedDb.writeHashes(spec.hash(), dataHash);
    return new MerkleRoot(hash, spec, dataHash);
  }

  private Hash writeArrayData(Iterable<? extends Obj> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(List<? extends Obj> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  // helpers

  private Hash writeSequence(Iterable<? extends Obj> objs) throws HashedDbException {
    var hashes = map(objs, Obj::hash);
    return hashedDb.writeHashes(hashes);
  }

  private Hash writeArraySpecRoot(Spec elementSpec) throws HashedDbException {
    return writeNonBaseSpecRoot(ARRAY, elementSpec.hash());
  }

  private Hash writeTupleSpecRoot(Iterable<? extends Spec> elementSpecs)
      throws HashedDbException {
    Hash elementsHash = hashedDb.writeHashes(map(elementSpecs, Spec::hash));
    return writeNonBaseSpecRoot(TUPLE, elementsHash);
  }

  private Hash writeNonBaseSpecRoot(SpecKind specKind, Hash elements) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeByte(specKind.marker()), elements);
  }

  private Hash writeBaseSpecRoot(SpecKind specKind) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeByte(specKind.marker()));
  }
}
