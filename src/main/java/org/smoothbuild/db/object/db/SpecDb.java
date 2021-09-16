package org.smoothbuild.db.object.db;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsDecodeSpecException;
import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.DEFINED_LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.EARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.FIELD_READ;
import static org.smoothbuild.db.object.spec.base.SpecKind.INT;
import static org.smoothbuild.db.object.spec.base.SpecKind.NATIVE_LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.NULL;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.base.SpecKind.fromMarker;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.exc.DecodeSpecException;
import org.smoothbuild.db.object.exc.DecodeSpecRootException;
import org.smoothbuild.db.object.exc.ObjectDbException;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.EArraySpec;
import org.smoothbuild.db.object.spec.expr.FieldReadSpec;
import org.smoothbuild.db.object.spec.expr.NullSpec;
import org.smoothbuild.db.object.spec.expr.RefSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class SpecDb {
  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, Spec> specCache;

  private final BlobSpec blobSpec;
  private final BoolSpec boolSpec;
  private final IntSpec intSpec;
  private final NothingSpec nothingSpec;
  private final StrSpec strSpec;
  private final CallSpec callSpec;
  private final ConstSpec constSpec;
  private final EArraySpec eArraySpec;
  private final FieldReadSpec fieldReadSpec;
  private final NullSpec nullSpec;
  private final RefSpec refSpec;

  public SpecDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.specCache = new ConcurrentHashMap<>();

    try {
      // Val-s
      this.blobSpec = cacheSpec(new BlobSpec(writeBaseSpecRoot(BLOB)));
      this.boolSpec = cacheSpec(new BoolSpec(writeBaseSpecRoot(BOOL)));
      this.intSpec = cacheSpec(new IntSpec(writeBaseSpecRoot(INT)));
      this.nothingSpec = cacheSpec(new NothingSpec(writeBaseSpecRoot(NOTHING)));
      this.strSpec = cacheSpec(new StrSpec(writeBaseSpecRoot(STRING)));
      // Expr-s
      this.callSpec = cacheSpec(new CallSpec(writeBaseSpecRoot(CALL)));
      this.constSpec = cacheSpec(new ConstSpec(writeBaseSpecRoot(CONST)));
      this.eArraySpec = cacheSpec(new EArraySpec(writeBaseSpecRoot(EARRAY)));
      this.fieldReadSpec = cacheSpec(new FieldReadSpec(writeBaseSpecRoot(FIELD_READ)));
      this.nullSpec = cacheSpec(new NullSpec(writeBaseSpecRoot(NULL)));
      this.refSpec = cacheSpec(new RefSpec(writeBaseSpecRoot(REF)));
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  // methods for getting Val-s specs

  public ArraySpec arraySpec(ValSpec elementSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArraySpec(elementSpec));
  }

  public BlobSpec blobSpec() {
    return blobSpec;
  }

  public BoolSpec boolSpec() {
    return boolSpec;
  }

  public DefinedLambdaSpec definedLambdaSpec(ValSpec result, RecSpec parameters) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newDefinedLambdaSpec(result, parameters));
  }

  public IntSpec intSpec() {
    return intSpec;
  }

  public NativeLambdaSpec nativeLambdaSpec(ValSpec result, RecSpec parameters) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newNativeLambdaSpec(result, parameters));
  }

  public NothingSpec nothingSpec() {
    return nothingSpec;
  }

  public StrSpec strSpec() {
    return strSpec;
  }

  // methods for getting Expr-s specs

  public CallSpec callSpec() {
    return callSpec;
  }

  public ConstSpec constSpec() {
    return constSpec;
  }

  public EArraySpec eArraySpec() {
    return eArraySpec;
  }

  public FieldReadSpec fieldReadSpec() {
    return fieldReadSpec;
  }

  public NullSpec nullSpec() {
    return nullSpec;
  }

  public RefSpec refSpec() {
    return refSpec;
  }

  public RecSpec recSpec(Iterable<? extends ValSpec> itemSpecs) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRecSpec(itemSpecs));
  }

  // methods for reading from db

  public Spec getSpec(Hash hash) {
    return requireNonNullElseGet(specCache.get(hash), () -> readSpec(hash));
  }

  private Spec getSpecOrChainException(Hash outerSpec, Hash specHash) {
    try {
      return getSpec(specHash);
    } catch (ObjectDbException e) {
      throw new DecodeSpecException(outerSpec);
    }
  }

  private Spec readSpec(Hash hash) {
    List<Hash> rootSequence = readSpecRootSequence(hash);
    SpecKind specKind = decodeSpecMarker(hash, rootSequence.get(0));
    return switch (specKind) {
      case BLOB, BOOL, INT, NOTHING, STRING, CALL, CONST, EARRAY, FIELD_READ, NULL, REF -> {
        assertSpecRootSequenceSize(hash, specKind, rootSequence, 1);
        throw new RuntimeException(
            "Internal error: Spec with kind " + specKind + " should be found in cache.");
      }
      case ARRAY -> readArraySpec(hash, rootSequence);
      case DEFINED_LAMBDA, NATIVE_LAMBDA -> readLambdaSpec(hash, rootSequence, specKind);
      case RECORD -> readRecord(hash, rootSequence);
    };
  }

  private List<Hash> readSpecRootSequence(Hash hash) {
    List<Hash> hashes = wrapHashedDbExceptionAsDecodeSpecException(
        hash, () -> hashedDb.readSequence(hash));
    int sequenceSize = hashes.size();
    if (sequenceSize != 1 && sequenceSize != 2) {
      throw new DecodeSpecRootException(hash, sequenceSize);
    }
    return hashes;
  }

  private SpecKind decodeSpecMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExceptionAsDecodeSpecException(
        hash, () -> hashedDb.readByte(markerHash));
    SpecKind specKind = fromMarker(marker);
    if (specKind == null) {
      throw new DecodeSpecException(hash,
          "It has illegal SpecKind marker = " + marker + ".");
    }
    return specKind;
  }

  private static void assertSpecRootSequenceSize(
      Hash hash, SpecKind specKind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeSpecException(hash,
          "Its specKind == " + specKind + " but its merkle root has "
              + hashes.size() + " children when " + expectedSize + " is expected.");
    }
  }

  private ArraySpec readArraySpec(Hash hash, List<Hash> rootSequence) {
    assertSpecRootSequenceSize(hash, ARRAY, rootSequence, 2);
    Spec elementSpec = getSpecOrChainException(hash, rootSequence.get(1));
    if (elementSpec instanceof ValSpec valSpec) {
      return newArraySpec(hash, valSpec);
    } else {
      throw new DecodeSpecException(hash, "It is ARRAY Spec which element Spec is "
          + elementSpec.name() + " but should be Spec of some Val.");
    }
  }

  private Spec readLambdaSpec(Hash hash, List<Hash> rootSequence, SpecKind specKind) {
    assertSpecRootSequenceSize(hash, specKind, rootSequence, 2);
    List<Hash> data = wrapHashedDbExceptionAsDecodeSpecException(
        hash, () -> hashedDb.readSequence(rootSequence.get(1)));
    if (data.size() != 2) {
      throw new DecodeSpecException(hash, "It is " + specKind
          + " Spec which data sequence contains " + data.size()
          + " elements but should contains 2.");
    }
    Spec result = getSpecOrChainException(hash, data.get(0));
    Spec parameters = getSpecOrChainException(hash, data.get(1));
    if (!(result instanceof ValSpec resultSpec)) {
      throw new DecodeSpecException(hash, "It is " + specKind + " Spec which result spec is "
          + result.name() + " but should be instance of ValSpec.");
    }
    if (!(parameters instanceof RecSpec parametersSpec)) {
      throw new DecodeSpecException(hash, "It is " + specKind
          + " Spec which parameters spec is " + parameters.name()
          + " but should be instance of RecSpec.");
    }
    return switch (specKind) {
      case DEFINED_LAMBDA -> newDefinedLambdaSpec(hash, resultSpec, parametersSpec);
      case NATIVE_LAMBDA -> newNativeLambdaSpec(hash, resultSpec, parametersSpec);
      default -> throw new RuntimeException("Cannot happen.");
    };
  }

  private RecSpec readRecord(Hash hash, List<Hash> rootSequence) {
    assertSpecRootSequenceSize(hash, RECORD, rootSequence, 2);
    ImmutableList<Spec> items = readRecSpecItemSpecs(rootSequence.get(1), hash);
    return newRecSpec(hash, items);
  }

  private ImmutableList<Spec> readRecSpecItemSpecs(Hash hash, Hash parentHash) {
    var builder = ImmutableList.<Spec>builder();
    List<Hash> itemSpecHashes = readRecSpecItemSpecHashes(hash, parentHash);
    for (int i = 0; i < itemSpecHashes.size(); i++) {
      try {
        builder.add(getSpec(itemSpecHashes.get(i)));
      } catch (ObjectDbException e) {
        throw new DecodeSpecException(parentHash, "Its specKind == RECORD "
            + "but reading item spec at index " + i + " caused error.", e);
      }
    }
    return builder.build();
  }

  private List<Hash> readRecSpecItemSpecHashes(Hash hash, Hash parentHash) {
    try {
      return hashedDb.readSequence(hash);
    } catch (HashedDbException e) {
      throw new DecodeSpecException(parentHash,
          "Its specKind == RECORD but reading its item specs caused error.", e);
    }
  }

  // methods for creating Spec-s

  private ArraySpec newArraySpec(ValSpec elementSpec) throws HashedDbException {
    Hash hash = writeArraySpecRoot(elementSpec);
    return newArraySpec(hash, elementSpec);
  }

  private ArraySpec newArraySpec(Hash hash, ValSpec elementSpec) {
    return cacheSpec(new ArraySpec(hash, elementSpec));
  }

  private DefinedLambdaSpec newDefinedLambdaSpec(ValSpec result, RecSpec parameters)
      throws HashedDbException {
    Hash hash = writeLambdaSpecRoot(DEFINED_LAMBDA, result, parameters);
    return newDefinedLambdaSpec(hash, result, parameters);
  }

  private DefinedLambdaSpec newDefinedLambdaSpec(Hash hash, ValSpec result, RecSpec parameters) {
    return cacheSpec(new DefinedLambdaSpec(hash, result, parameters));
  }

  private NativeLambdaSpec newNativeLambdaSpec(ValSpec result, RecSpec parameters)
      throws HashedDbException {
    Hash hash = writeLambdaSpecRoot(NATIVE_LAMBDA, result, parameters);
    return newNativeLambdaSpec(hash, result, parameters);
  }

  private NativeLambdaSpec newNativeLambdaSpec(Hash hash, ValSpec result, RecSpec parameters) {
    return cacheSpec(new NativeLambdaSpec(hash, result, parameters));
  }

  private RecSpec newRecSpec(Iterable<? extends ValSpec> itemSpecs) throws HashedDbException {
    Hash hash = writeRecSpecRoot(itemSpecs);
    return newRecSpec(hash, itemSpecs);
  }

  private RecSpec newRecSpec(Hash hash, Iterable<? extends Spec> itemSpecs) {
    return cacheSpec(new RecSpec(hash, itemSpecs));
  }

  private <T extends Spec> T cacheSpec(T spec) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(specCache.putIfAbsent(spec.hash(), spec), spec);
    return result;
  }

  // Methods for writing specs

  private Hash writeArraySpecRoot(Spec elementSpec) throws HashedDbException {
    return writeNonBaseSpecRoot(ARRAY, elementSpec.hash());
  }

  private Hash writeLambdaSpecRoot(SpecKind lambdaKind, ValSpec result, RecSpec parameters)
      throws HashedDbException {
    Hash hash = hashedDb.writeSequence(result.hash(), parameters.hash());
    return writeNonBaseSpecRoot(lambdaKind, hash);
  }

  private Hash writeRecSpecRoot(Iterable<? extends ValSpec> itemSpecs)
      throws HashedDbException {
    Hash itemsHash = hashedDb.writeSequence(map(itemSpecs, Spec::hash));
    return writeNonBaseSpecRoot(RECORD, itemsHash);
  }

  private Hash writeNonBaseSpecRoot(SpecKind specKind, Hash elements) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(specKind.marker()), elements);
  }

  private Hash writeBaseSpecRoot(SpecKind specKind) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(specKind.marker()));
  }
}
