package org.smoothbuild.db.object.db;

import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.db.Helpers.wrapException;
import static org.smoothbuild.db.object.spec.SpecKind.ANY;
import static org.smoothbuild.db.object.spec.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.SpecKind.TUPLE;
import static org.smoothbuild.db.object.spec.SpecKind.specKindMarkedWith;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.object.base.Any;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.BlobBuilder;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.spec.AnySpec;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.BlobSpec;
import org.smoothbuild.db.object.spec.BoolSpec;
import org.smoothbuild.db.object.spec.NothingSpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.SpecKind;
import org.smoothbuild.db.object.spec.StringSpec;
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

  private AnySpec anySpec;
  private BoolSpec boolSpec;
  private BlobSpec blobSpec;
  private NothingSpec nothingSpec;
  private StringSpec stringSpec;

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
      this.anySpec = new AnySpec(writeBaseSpecRoot(ANY), hashedDb, this);
      this.blobSpec = new BlobSpec(writeBaseSpecRoot(BLOB), hashedDb, this);
      this.boolSpec = new BoolSpec(writeBaseSpecRoot(BOOL), hashedDb, this);
      this.nothingSpec = new NothingSpec(writeBaseSpecRoot(NOTHING), hashedDb, this);
      this.stringSpec = new StringSpec(writeBaseSpecRoot(STRING), hashedDb, this);

      cacheSpec(blobSpec);
      cacheSpec(boolSpec);
      cacheSpec(nothingSpec);
      cacheSpec(stringSpec);
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  // methods for creating objects or object builders

  public ArrayBuilder arrayBuilder(Spec elementSpec) {
    return new ArrayBuilder(arraySpec(elementSpec), this);
  }

  public BlobBuilder blobBuilder() {
    return wrapException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Any any(Hash wrappedHash) {
    return wrapException(() -> newAny(wrappedHash));
  }

  public Bool bool(boolean value) {
    return wrapException(() -> newBool(value));
  }

  public Str string(String string) {
    return wrapException(() -> newString(string));
  }

  public Tuple tuple(TupleSpec tupleSpec, Iterable<? extends Obj> elements) {
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
    return wrapException(() -> newTuple(tupleSpec, elementsList));
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

  public ArraySpec arraySpec(Spec elementSpec) {
    return cacheSpec(wrapException(() -> newArraySpec(elementSpec)));
  }

  public AnySpec anySpec() {
    return anySpec;
  }

  public BlobSpec blobSpec() {
    return blobSpec;
  }

  public BoolSpec boolSpec() {
    return boolSpec;
  }

  public NothingSpec nothingSpec() {
    return nothingSpec;
  }

  public StringSpec stringSpec() {
    return stringSpec;
  }

  public TupleSpec tupleSpec(Iterable<? extends Spec> elementSpecs) {
    return cacheSpec(wrapException(() -> newTupleSpec(elementSpecs)));
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
      SpecKind specKind = specKindMarkedWith(marker);
      if (specKind == null) {
        throw new CannotDecodeSpecException(hash,
            "It has illegal SpecKind marker = " + marker + ".");
      }
      return switch (specKind) {
        case ANY -> {
          assertSize(hash, ANY, hashes, 1);
          yield anySpec;
        }
        case BOOL -> {
          assertSize(hash, BOOL, hashes, 1);
          yield boolSpec;
        }
        case STRING -> {
          assertSize(hash, STRING, hashes, 1);
          yield stringSpec;
        }
        case BLOB -> {
          assertSize(hash, BLOB, hashes, 1);
          yield blobSpec;
        }
        case NOTHING -> {
          assertSize(hash, NOTHING, hashes, 1);
          yield nothingSpec;
        }
        case ARRAY -> {
          assertSize(hash, ARRAY, hashes, 2);
          Spec elementSpec = getSpecOrChainException(
              hashes.get(1), e -> new CannotDecodeSpecException(hash));
          yield cacheSpec(newArraySpec(hash, elementSpec));
        }
        case TUPLE -> {
          assertSize(hash, TUPLE, hashes, 2);
          ImmutableList<Spec> elements = readTupleSpecElementSpecs(hashes.get(1), hash);
          yield cacheSpec(newTupleSpec(hash, elements));
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

  // methods for creating spec instances

  public Array newArray(ArraySpec spec, Iterable<? extends Obj> elements)
      throws HashedDbException {
    return spec.newObj(writeRoot(spec, writeArrayData(elements)));
  }

  public Any newAny(Hash wrappedHash) throws HashedDbException {
    return anySpec.newObj(writeRoot(anySpec, writeAnyData(wrappedHash)));
  }

  public Blob newBlob(Hash dataHash) throws HashedDbException {
    return blobSpec.newObj(writeRoot(blobSpec, dataHash));
  }

  private Bool newBool(boolean value) throws HashedDbException {
    return boolSpec.newObj(writeRoot(boolSpec, writeBoolData(value)));
  }

  private Str newString(String string) throws HashedDbException {
    return stringSpec.newObj(writeRoot(stringSpec, writeStringData(string)));
  }

  private Tuple newTuple(TupleSpec spec, List<?extends Obj> objects) throws HashedDbException {
    return spec.newObj(writeRoot(spec, writeTupleData(objects)));
  }

  private ArraySpec newArraySpec(Spec elementSpec) throws HashedDbException {
    Hash hash = writeArraySpecRoot(elementSpec);
    return newArraySpec(hash, elementSpec);
  }

  private ArraySpec newArraySpec(Hash hash, Spec elementSpec) {
    return new ArraySpec(hash, elementSpec, hashedDb, this);
  }

  private TupleSpec newTupleSpec(Iterable<? extends Spec> elementSpecs) throws HashedDbException {
    Hash hash = writeTupleSpecRoot(elementSpecs);
    return newTupleSpec(hash, elementSpecs);
  }

  private TupleSpec newTupleSpec(Hash hash, Iterable<? extends Spec> elementSpecs) {
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

  private Hash writeAnyData(Hash wrappedHash) throws HashedDbException {
    return hashedDb.writeHashes(wrappedHash);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(List<? extends Obj> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeSequence(Iterable<? extends Obj> objects) throws HashedDbException {
    Hash[] hashes = stream(objects)
        .map(Obj::hash)
        .toArray(Hash[]::new);
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
