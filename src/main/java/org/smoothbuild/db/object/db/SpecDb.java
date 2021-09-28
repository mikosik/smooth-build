package org.smoothbuild.db.object.db;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsDecodeSpecException;
import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsDecodeSpecNodeException;
import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.DEFINED_LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.EARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.INT;
import static org.smoothbuild.db.object.spec.base.SpecKind.NATIVE_LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.NULL;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;
import static org.smoothbuild.db.object.spec.base.SpecKind.SELECT;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.base.SpecKind.fromMarker;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.exc.DecodeSpecIllegalKindException;
import org.smoothbuild.db.object.exc.DecodeSpecNodeException;
import org.smoothbuild.db.object.exc.DecodeSpecRootException;
import org.smoothbuild.db.object.exc.ObjectDbException;
import org.smoothbuild.db.object.exc.UnexpectedSpecNodeException;
import org.smoothbuild.db.object.exc.UnexpectedSpecSequenceException;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.EArraySpec;
import org.smoothbuild.db.object.spec.expr.NullSpec;
import org.smoothbuild.db.object.spec.expr.RefSpec;
import org.smoothbuild.db.object.spec.expr.SelectSpec;
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
  public static final String DATA_PATH = "data";
  private static final int DATA_INDEX = 1;
  private static final int LAMBDA_RESULT_INDEX = 0;
  public static final String LAMBDA_RESULT_PATH = DATA_PATH + "[" + LAMBDA_RESULT_INDEX + "]";
  private static final int LAMBDA_PARAMS_INDEX = 1;
  public static final String LAMBDA_PARAMS_PATH = DATA_PATH + "[" + LAMBDA_PARAMS_INDEX + "]";


  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, Spec> specCache;

  private final BlobSpec blobSpec;
  private final BoolSpec boolSpec;
  private final IntSpec intSpec;
  private final NothingSpec nothingSpec;
  private final StrSpec strSpec;
  private final NullSpec nullSpec;

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
      this.nullSpec = cacheSpec(new NullSpec(writeBaseSpecRoot(NULL), nothingSpec));
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

  public CallSpec callSpec(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCallSpec(evaluationSpec));
  }

  public ConstSpec constSpec(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstSpec(evaluationSpec));
  }

  public EArraySpec eArraySpec(ValSpec elementSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newEArraySpec(elementSpec));
  }

  public SelectSpec selectSpec(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelectSpec(evaluationSpec));
  }

  public NullSpec nullSpec() {
    return nullSpec;
  }

  public RefSpec refSpec(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRefSpec(evaluationSpec));
  }

  public RecSpec recSpec(Iterable<? extends ValSpec> itemSpecs) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRecSpec(itemSpecs));
  }

  // methods for reading from db

  public Spec getSpec(Hash hash) {
    return requireNonNullElseGet(specCache.get(hash), () -> readSpec(hash));
  }

  private Spec getSpecOrChainException(Hash outerSpec, SpecKind specKind, Hash nodeHash,
      String path) {
    try {
      return getSpec(nodeHash);
    } catch (ObjectDbException e) {
      throw new DecodeSpecNodeException(outerSpec, specKind, path, e);
    }
  }

  private Spec getSpecOrChainException(Hash outerSpec, SpecKind specKind, Hash nodeHash,
      String path, int index) {
    try {
      return getSpec(nodeHash);
    } catch (ObjectDbException e) {
      throw new DecodeSpecNodeException(outerSpec, specKind, path, index, e);
    }
  }

  private Spec readSpec(Hash hash) {
    List<Hash> rootSequence = readSpecRootSequence(hash);
    SpecKind specKind = decodeSpecMarker(hash, rootSequence.get(0));
    return switch (specKind) {
      case BLOB, BOOL, INT, NOTHING, STRING, NULL -> {
        assertSpecRootSequenceSize(hash, specKind, rootSequence, 1);
        throw new RuntimeException(
            "Internal error: Spec with kind " + specKind + " should be found in cache.");
      }
      case ARRAY -> newArraySpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case CALL -> newCallSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case CONST -> newConstSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case EARRAY -> newEArraySpec(hash, getDataAsArraySpec(hash, rootSequence, specKind));
      case SELECT -> newSelectSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case REF -> newRefSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
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
      throw new DecodeSpecIllegalKindException(hash, marker);
    }
    return specKind;
  }

  private static void assertSpecRootSequenceSize(
      Hash hash, SpecKind specKind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeSpecRootException(hash, specKind, hashes.size(), expectedSize);
    }
  }

  private ValSpec getDataAsValSpec(Hash hash, List<Hash> rootSequence, SpecKind specKind) {
    return getDataAsSpecCastedTo(hash, rootSequence, specKind, ValSpec.class);
  }

  private ArraySpec getDataAsArraySpec(Hash hash, List<Hash> rootSequence, SpecKind specKind) {
    return getDataAsSpecCastedTo(hash, rootSequence, specKind, ArraySpec.class);
  }

  private <T extends Spec> T getDataAsSpecCastedTo(Hash hash, List<Hash> rootSequence,
      SpecKind specKind, Class<T> expectedSpecClass) {
    Spec dataAsSpec = getDataAsSpec(hash, specKind, rootSequence);
    if (expectedSpecClass.isInstance(dataAsSpec)) {
      @SuppressWarnings("unchecked")
      T result = (T) dataAsSpec;
      return result;
    } else {
      throw new UnexpectedSpecNodeException(
          hash, specKind, DATA_PATH, expectedSpecClass, dataAsSpec.getClass());
    }
  }

  private Spec getDataAsSpec(Hash hash, SpecKind specKind, List<Hash> rootSequence) {
    assertSpecRootSequenceSize(hash, specKind, rootSequence, 2);
    return getSpecOrChainException(hash, specKind, rootSequence.get(DATA_INDEX), DATA_PATH);
  }

  private Spec readLambdaSpec(Hash hash, List<Hash> rootSequence, SpecKind specKind) {
    assertSpecRootSequenceSize(hash, specKind, rootSequence, 2);
    Hash dataHash = rootSequence.get(DATA_INDEX);
    List<Hash> data = readSequenceHashes(hash, dataHash, specKind, DATA_PATH);
    if (data.size() != 2) {
      throw new UnexpectedSpecSequenceException(hash, specKind, DATA_PATH, 2, data.size());
    }
    Spec result = getSpecOrChainException(
        hash, specKind, data.get(LAMBDA_RESULT_INDEX), LAMBDA_RESULT_PATH);
    Spec parameters = getSpecOrChainException(
        hash, specKind, data.get(LAMBDA_PARAMS_INDEX), LAMBDA_PARAMS_PATH);
    if (!(result instanceof ValSpec resultSpec)) {
      throw new UnexpectedSpecNodeException(
          hash, specKind, LAMBDA_RESULT_PATH, ValSpec.class, result.getClass());
    }
    if (!(parameters instanceof RecSpec parametersSpec)) {
      throw new UnexpectedSpecNodeException(
          hash, specKind, LAMBDA_PARAMS_PATH, RecSpec.class, parameters.getClass());
    }
    return switch (specKind) {
      case DEFINED_LAMBDA -> newDefinedLambdaSpec(hash, resultSpec, parametersSpec);
      case NATIVE_LAMBDA -> newNativeLambdaSpec(hash, resultSpec, parametersSpec);
      default -> throw new RuntimeException("Cannot happen.");
    };
  }

  private RecSpec readRecord(Hash hash, List<Hash> rootSequence) {
    assertSpecRootSequenceSize(hash, RECORD, rootSequence, 2);
    ImmutableList<ValSpec> items = readRecSpecItemSpecs(hash, rootSequence.get(DATA_INDEX));
    return newRecSpec(hash, items);
  }

  private ImmutableList<ValSpec> readRecSpecItemSpecs(Hash hash, Hash itemSpecsHash) {
    var builder = ImmutableList.<ValSpec>builder();
    var itemSpecHashes = readSequenceHashes(hash, itemSpecsHash, RECORD, DATA_PATH);
    for (int i = 0; i < itemSpecHashes.size(); i++) {
      Spec spec = getSpecOrChainException(hash, RECORD, itemSpecHashes.get(i), DATA_PATH, i);
      if (spec instanceof ValSpec valSpec) {
        builder.add(valSpec);
      } else {
        throw new UnexpectedSpecNodeException(
            hash, RECORD, "data", i, ValSpec.class, spec.getClass());
      }
    }
    return builder.build();
  }

  // methods for creating Val Spec-s

  private ArraySpec newArraySpec(ValSpec elementSpec) throws HashedDbException {
    var hash = writeArraySpecRoot(elementSpec);
    return newArraySpec(hash, elementSpec);
  }

  private ArraySpec newArraySpec(Hash hash, ValSpec elementSpec) {
    return cacheSpec(new ArraySpec(hash, elementSpec));
  }

  private DefinedLambdaSpec newDefinedLambdaSpec(ValSpec result, RecSpec parameters)
      throws HashedDbException {
    var hash = writeLambdaSpecRoot(DEFINED_LAMBDA, result, parameters);
    return newDefinedLambdaSpec(hash, result, parameters);
  }

  private DefinedLambdaSpec newDefinedLambdaSpec(Hash hash, ValSpec result, RecSpec parameters) {
    return cacheSpec(new DefinedLambdaSpec(hash, result, parameters));
  }

  private NativeLambdaSpec newNativeLambdaSpec(ValSpec result, RecSpec parameters)
      throws HashedDbException {
    var hash = writeLambdaSpecRoot(NATIVE_LAMBDA, result, parameters);
    return newNativeLambdaSpec(hash, result, parameters);
  }

  private NativeLambdaSpec newNativeLambdaSpec(Hash hash, ValSpec result, RecSpec parameters) {
    return cacheSpec(new NativeLambdaSpec(hash, result, parameters));
  }

  private RecSpec newRecSpec(Iterable<? extends ValSpec> itemSpecs) throws HashedDbException {
    var hash = writeRecSpecRoot(itemSpecs);
    return newRecSpec(hash, itemSpecs);
  }

  private RecSpec newRecSpec(Hash hash, Iterable<? extends ValSpec> itemSpecs) {
    return cacheSpec(new RecSpec(hash, itemSpecs));
  }

  // methods for creating Expr Spec-s

  private CallSpec newCallSpec(ValSpec evaluationSpec) throws HashedDbException {
    var hash = writeExprSpecRoot(CALL, evaluationSpec);
    return newCallSpec(hash, evaluationSpec);
  }

  private CallSpec newCallSpec(Hash hash, ValSpec evaluationSpec) {
    return cacheSpec(new CallSpec(hash, evaluationSpec));
  }

  private ConstSpec newConstSpec(ValSpec evaluationSpec) throws HashedDbException {
    var hash = writeExprSpecRoot(CONST, evaluationSpec);
    return newConstSpec(hash, evaluationSpec);
  }

  private ConstSpec newConstSpec(Hash hash, ValSpec evaluationSpec) {
    return cacheSpec(new ConstSpec(hash, evaluationSpec));
  }

  private EArraySpec newEArraySpec(ValSpec elementSpec) throws HashedDbException {
    var evaluationSpec = arraySpec(elementSpec);
    var hash = writeExprSpecRoot(EARRAY, evaluationSpec);
    return newEArraySpec(hash, evaluationSpec);
  }

  private EArraySpec newEArraySpec(Hash hash, ArraySpec evaluationSpec) {
    return cacheSpec(new EArraySpec(hash, evaluationSpec));
  }

  private SelectSpec newSelectSpec(ValSpec evaluationSpec) throws HashedDbException {
    var hash = writeExprSpecRoot(SELECT, evaluationSpec);
    return newSelectSpec(hash, evaluationSpec);
  }

  private SelectSpec newSelectSpec(Hash hash, ValSpec evaluationSpec) {
    return cacheSpec(new SelectSpec(hash, evaluationSpec));
  }

  private RefSpec newRefSpec(ValSpec evaluationSpec) throws HashedDbException {
    var hash = writeExprSpecRoot(REF, evaluationSpec);
    return newRefSpec(hash, evaluationSpec);
  }

  private RefSpec newRefSpec(Hash hash, ValSpec evaluationSpec) {
    return cacheSpec(new RefSpec(hash, evaluationSpec));
  }

  private <T extends Spec> T cacheSpec(T spec) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(specCache.putIfAbsent(spec.hash(), spec), spec);
    return result;
  }

  // Methods for writing Val spec root

  private Hash writeArraySpecRoot(Spec elementSpec) throws HashedDbException {
    return writeNonBaseSpecRoot(ARRAY, elementSpec.hash());
  }

  private Hash writeLambdaSpecRoot(SpecKind lambdaKind, ValSpec result, RecSpec parameters)
      throws HashedDbException {
    var hash = hashedDb.writeSequence(result.hash(), parameters.hash());
    return writeNonBaseSpecRoot(lambdaKind, hash);
  }

  private Hash writeRecSpecRoot(Iterable<? extends ValSpec> itemSpecs)
      throws HashedDbException {
    var itemsHash = hashedDb.writeSequence(map(itemSpecs, Spec::hash));
    return writeNonBaseSpecRoot(RECORD, itemsHash);
  }

  // Helper methods for writing roots

  private Hash writeExprSpecRoot(SpecKind specKind, Spec evaluationSpec) throws HashedDbException {
    return writeNonBaseSpecRoot(specKind, evaluationSpec.hash());
  }

  private Hash writeNonBaseSpecRoot(SpecKind specKind, Hash elements) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(specKind.marker()), elements);
  }

  private Hash writeBaseSpecRoot(SpecKind specKind) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(specKind.marker()));
  }

  // Helper methods for reading

  private ImmutableList<Hash> readSequenceHashes(Hash hash, Hash sequenceHash, SpecKind specKind,
      String path) {
    return wrapHashedDbExceptionAsDecodeSpecNodeException(
        hash, specKind, path, () -> hashedDb.readSequence(sequenceHash));
  }
}
