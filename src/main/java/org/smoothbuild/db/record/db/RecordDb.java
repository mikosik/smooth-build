package org.smoothbuild.db.record.db;

import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.record.db.Helpers.wrapException;
import static org.smoothbuild.db.record.spec.SpecKind.ARRAY;
import static org.smoothbuild.db.record.spec.SpecKind.BLOB;
import static org.smoothbuild.db.record.spec.SpecKind.BOOL;
import static org.smoothbuild.db.record.spec.SpecKind.NOTHING;
import static org.smoothbuild.db.record.spec.SpecKind.STRING;
import static org.smoothbuild.db.record.spec.SpecKind.TUPLE;
import static org.smoothbuild.db.record.spec.SpecKind.specKindMarkedWith;
import static org.smoothbuild.util.Iterables.map;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.ArrayBuilder;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.BlobBuilder;
import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.db.record.spec.ArraySpec;
import org.smoothbuild.db.record.spec.BlobSpec;
import org.smoothbuild.db.record.spec.BoolSpec;
import org.smoothbuild.db.record.spec.NothingSpec;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.db.record.spec.SpecKind;
import org.smoothbuild.db.record.spec.StringSpec;
import org.smoothbuild.db.record.spec.TupleSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class RecordDb {
  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, Spec> specCache;

  /**
   * Following fields are effectively immutable - they are set only once in {@link #initialize()}
   * which is invoked before instance of RecordDb is returned from factory method.
   */

  private BoolSpec boolSpec;
  private BlobSpec blobSpec;
  private NothingSpec nothingSpec;
  private StringSpec stringSpec;

  public static RecordDb recordDb(HashedDb hashedDb) {
      RecordDb recordDb = new RecordDb(hashedDb);
      recordDb.initialize();
      return recordDb;
  }

  private RecordDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.specCache = new ConcurrentHashMap<>();
  }

  private void initialize() {
    try {
      this.blobSpec = new BlobSpec(writeBasicSpecRoot(BLOB), hashedDb, this);
      this.boolSpec = new BoolSpec(writeBasicSpecRoot(BOOL), hashedDb, this);
      this.nothingSpec = new NothingSpec(writeBasicSpecRoot(NOTHING), hashedDb, this);
      this.stringSpec = new StringSpec(writeBasicSpecRoot(STRING), hashedDb, this);

      cacheSpec(blobSpec);
      cacheSpec(boolSpec);
      cacheSpec(nothingSpec);
      cacheSpec(stringSpec);
    } catch (HashedDbException e) {
      throw new RecordDbException(e);
    }
  }

  // methods for creating records or record builders

  public ArrayBuilder arrayBuilder(Spec elementSpec) {
    return new ArrayBuilder(arraySpec(elementSpec), this);
  }

  public BlobBuilder blobBuilder() {
    return wrapException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool bool(boolean value) {
    return wrapException(() -> newBool(value));
  }

  public RString string(String string) {
    return wrapException(() -> newString(string));
  }

  public Tuple tuple(TupleSpec tupleSpec, Iterable<? extends Record> elements) {
    List<Record> elementsList = ImmutableList.copyOf(elements);
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

  public Record get(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 2);
      Spec spec = getSpecOrChainException(
          hashes.get(0), e -> new CannotDecodeRecordException(hash, e));
      Hash dataHash = hashes.get(1);
      return spec.newJObject(new MerkleRoot(hash, spec, dataHash));
    } catch (HashedDbException e) {
      throw new CannotDecodeRecordException(hash, e);
    }
  }

  // methods for returning specs

  public ArraySpec arraySpec(Spec elementSpec) {
    return cacheSpec(wrapException(() -> newArraySpec(elementSpec)));
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
    } catch (RecordDbException e) {
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
      } catch (RecordDbException e) {
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

  public Array newArray(ArraySpec spec, Iterable<? extends Record> elements)
      throws HashedDbException {
    return spec.newJObject(writeRoot(spec, writeArrayData(elements)));
  }

  public Blob newBlob(Hash dataHash) throws HashedDbException {
    return blobSpec.newJObject(writeRoot(blobSpec, dataHash));
  }

  private Bool newBool(boolean value) throws HashedDbException {
    return boolSpec.newJObject(writeRoot(boolSpec, writeBoolData(value)));
  }

  private RString newString(String string) throws HashedDbException {
    return stringSpec.newJObject(writeRoot(stringSpec, writeStringData(string)));
  }

  private Tuple newTuple(TupleSpec spec, List<?extends Record> records) throws HashedDbException {
    return spec.newJObject(writeRoot(spec, writeTupleData(records)));
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

  private Hash writeArrayData(Iterable<? extends Record> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(List<? extends Record> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeSequence(Iterable<? extends Record> records) throws HashedDbException {
    Hash[] hashes = stream(records)
        .map(Record::hash)
        .toArray(Hash[]::new);
    return hashedDb.writeHashes(hashes);
  }

  private Hash writeArraySpecRoot(Spec elementSpec) throws HashedDbException {
    return writeNonBasicSpecRoot(ARRAY, elementSpec.hash());
  }

  private Hash writeTupleSpecRoot(Iterable<? extends Spec> elementSpecs)
      throws HashedDbException {
    Hash elementsHash = hashedDb.writeHashes(map(elementSpecs, Spec::hash));
    return writeNonBasicSpecRoot(TUPLE, elementsHash);
  }

  private Hash writeNonBasicSpecRoot(SpecKind specKind, Hash elements) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeByte(specKind.marker()), elements);
  }

  private Hash writeBasicSpecRoot(SpecKind specKind) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeByte(specKind.marker()));
  }
}
