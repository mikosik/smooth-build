package org.smoothbuild.db.record.db;

import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.db.record.spec.SpecKind.ARRAY;
import static org.smoothbuild.db.record.spec.SpecKind.BLOB;
import static org.smoothbuild.db.record.spec.SpecKind.BOOL;
import static org.smoothbuild.db.record.spec.SpecKind.NOTHING;
import static org.smoothbuild.db.record.spec.SpecKind.SPEC;
import static org.smoothbuild.db.record.spec.SpecKind.STRING;
import static org.smoothbuild.db.record.spec.SpecKind.TUPLE;
import static org.smoothbuild.db.record.spec.SpecKind.specKindMarkedWith;
import static org.smoothbuild.util.Iterables.map;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
import org.smoothbuild.db.record.spec.SpecSpec;
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
  private SpecSpec specSpec;

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
      this.specSpec = new SpecSpec(writeSpecSpecRoot(), hashedDb, this);
      this.blobSpec = new BlobSpec(writeBasicSpecRoot(specSpec, BLOB), hashedDb, this);
      this.boolSpec = new BoolSpec(writeBasicSpecRoot(specSpec, BOOL), hashedDb, this);
      this.nothingSpec = new NothingSpec(writeBasicSpecRoot(specSpec, NOTHING), hashedDb, this);
      this.stringSpec = new StringSpec(writeBasicSpecRoot(specSpec, STRING), hashedDb, this);

      cacheSpec(specSpec);
      cacheSpec(blobSpec);
      cacheSpec(boolSpec);
      cacheSpec(nothingSpec);
      cacheSpec(stringSpec);
    } catch (HashedDbException e) {
      throw new RecordDbException(e);
    }
  }

  // methods for creating non-spec records or its builders

  public ArrayBuilder arrayBuilder(Spec elementSpec) {
    return new ArrayBuilder(arraySpec(elementSpec), this);
  }

  public BlobBuilder blobBuilder() {
    return Helpers.wrapException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool bool(boolean value) {
    return Helpers.wrapException(() -> newBool(value));
  }

  public RString string(String string) {
    return Helpers.wrapException(() -> newString(string));
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
    return Helpers.wrapException(() -> newTuple(tupleSpec, elementsList));
  }

  public Record get(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
      if (hashes.size() == 1) {
          // If Merkle tree root has only one child then it must
          // be SpecSpec record. getSpec() will verify it.
          return getSpec(hash);
      } else {
        Spec spec = getSpecOrWrapException(hashes.get(0), hash);
        Hash dataHash = hashes.get(1);
        return spec.newJObject(new MerkleRoot(hash, spec, dataHash));
      }
    } catch (HashedDbException e) {
      throw new RecordDbException(hash, e);
    }
  }

  // methods for returning spec records

  public ArraySpec arraySpec(Spec elementSpec) {
    return cacheSpec(Helpers.wrapException(() -> newArraySpec(elementSpec)));
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
    return cacheSpec(Helpers.wrapException(() -> newTupleSpec(elementSpecs)));
  }

  public SpecSpec specSpec() {
    return specSpec;
  }

  private Spec getSpecOrWrapException(Hash specHash, Hash parentHash) {
    try {
      return getSpec(specHash);
    } catch (RecordDbException e) {
      throw new RecordDbException(parentHash, e);
    }
  }

  public Spec getSpec(MerkleRoot merkleRoot) {
    Spec spec = specCache.get(merkleRoot.hash());
    if (spec != null) {
      return spec;
    } else {
      return readSpec(merkleRoot.hash(), merkleRoot.dataHash());
    }
  }

  private Spec getSpec(Hash hash) {
    Spec spec = specCache.get(hash);
    if (spec != null) {
      return spec;
    } else {
      try {
        List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
        if (hashes.size() == 1) {
          if (!specSpec.hash().equals(hash)) {
            throw new RecordDbException(hash, "Expected record which spec == SPEC."
                + " Its Merkle tree has only one child (so the record itself should be == SPEC) "
                + "but it has a different hash.");
          }
          return specSpec;
        } else {
          Hash specHash = hashes.get(0);
          if (!specSpec.hash().equals(specHash)) {
            throw new RecordDbException(hash, "Expected record which spec == SPEC"
                + " but its Merkle tree's first child is not SPEC.");
          }
          Hash dataHash = hashes.get(1);
          return readSpec(hash, dataHash);
        }
      } catch (HashedDbException e) {
        throw new RecordDbException(hash, e);
      }
    }
  }

  private Spec readSpec(Hash hash, Hash dataHash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(dataHash, 1, 2);
      byte marker = hashedDb.readByte(hashes.get(0));
      SpecKind specKind = specKindMarkedWith(marker);
      if (specKind == null) {
        throw new RecordDbException(hash,
            "Its spec == SPEC but its data has illegal SpecKind marker = " + marker + ".");
      }
      return switch (specKind) {
        case SPEC -> throw new RuntimeException(
            "Shouldn't happen. SPEC case is handled in method that called us.");
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
          Spec elementSpec = getSpecOrWrapException(hashes.get(1), hash);
          yield cacheSpec(newArraySpec(elementSpec, dataHash));
        }
        case TUPLE -> {
          assertSize(hash, TUPLE, hashes, 2);
          ImmutableList<Spec> elements = readTupleSpecElementSpecs(hashes.get(1), hash);
          yield cacheSpec(newTupleSpec(elements, dataHash));
        }
      };
    } catch (HashedDbException e) {
      throw new RecordDbException(hash, e);
    }
  }

  private static void assertSize(Hash hash, SpecKind specKind, List<Hash> hashes,
      int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new RecordDbException(hash,
          "Its spec == SPEC and specKind == " + specKind + " but its dataHash has "
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
        throw new RecordDbException(parentHash, "Its spec == SPEC, its specKind == TUPLE "
            + "but reading element spec at index " + i + " caused error.", e);
      }
    }
    return builder.build();
  }

  private List<Hash> readTupleSpecElementSpecHashes(Hash hash, Hash parentHash) {
    try {
      return hashedDb.readHashes(hash);
    } catch (HashedDbException e) {
      throw new RecordDbException(parentHash,
          "Its spec == SPEC, its specKind == TUPLE but reading its element specs caused error.", e);
    }
  }

  private <T extends Spec> T cacheSpec(T spec) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(specCache.putIfAbsent(spec.hash(), spec), spec);
    return result;
  }

  // methods for creating spec's records

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

  private ArraySpec newArraySpec(Spec elementSpec) throws
      HashedDbException {
    Hash dataHash = writeArraySpecData(elementSpec);
    return newArraySpec(elementSpec, dataHash);
  }

  private ArraySpec newArraySpec(Spec elementSpec, Hash dataHash) throws
      HashedDbException {
    return new ArraySpec(writeRoot(specSpec, dataHash), elementSpec, hashedDb, this);
  }

  private TupleSpec newTupleSpec(Iterable<? extends Spec> elementSpecs) throws HashedDbException {
    Hash dataHash = writeTupleSpecData(elementSpecs);
    return newTupleSpec(elementSpecs, dataHash);
  }

  private TupleSpec newTupleSpec(Iterable<? extends Spec> elementSpecs, Hash dataHash)
      throws HashedDbException {
    return new TupleSpec(writeRoot(specSpec, dataHash), elementSpecs, hashedDb, this);
  }

  // methods for writing Merkle node(s) to HashedDb

  private MerkleRoot writeBasicSpecRoot(SpecSpec specSpec, SpecKind specKind) throws
      HashedDbException {
    return writeRoot(specSpec, writeBasicSpecData(specKind));
  }

  private MerkleRoot writeSpecSpecRoot() throws HashedDbException {
    Hash dataHash = writeBasicSpecData(SPEC);
    Hash hash = hashedDb.writeHashes(dataHash);
    return new MerkleRoot(hash, null, dataHash);
  }

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

  private Hash writeArraySpecData(Spec elementSpec) throws HashedDbException {
    return writeNonBasicSpecData(ARRAY, elementSpec.hash());
  }

  private Hash writeTupleSpecData(Iterable<? extends Spec> elementSpecs)
      throws HashedDbException {
    Hash elementsHash = hashedDb.writeHashes(map(elementSpecs, Spec::hash));
    return writeNonBasicSpecData(TUPLE, elementsHash);
  }

  private Hash writeNonBasicSpecData(SpecKind specKind, Hash elements) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeByte(specKind.marker()), elements);
  }

  private Hash writeBasicSpecData(SpecKind specKind) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeByte(specKind.marker()));
  }
}
