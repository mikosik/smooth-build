package org.smoothbuild.db.object.db;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.db.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.db.DecodeObjRootException.nonNullObjRootException;
import static org.smoothbuild.db.object.db.DecodeObjRootException.nullObjRootException;
import static org.smoothbuild.db.object.db.DecodeObjRootException.wrongSizeOfRootSequenceException;
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

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.obj.expr.FieldRead;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.DefinedLambda;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.NativeLambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
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
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;

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
  private CallSpec callSpec;
  private ConstSpec constSpec;
  private EArraySpec eArraySpec;
  private FieldReadSpec fieldReadSpec;
  private NullSpec nullSpec;
  private RefSpec refSpec;

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
      // Val-s
      this.blobSpec = new BlobSpec(writeBaseSpecRoot(BLOB), this);
      this.boolSpec = new BoolSpec(writeBaseSpecRoot(BOOL), this);
      this.intSpec = new IntSpec(writeBaseSpecRoot(INT), this);
      this.nothingSpec = new NothingSpec(writeBaseSpecRoot(NOTHING), this);
      this.strSpec = new StrSpec(writeBaseSpecRoot(STRING), this);
      // Expr-s
      this.callSpec = new CallSpec(writeBaseSpecRoot(CALL), this);
      this.constSpec = new ConstSpec(writeBaseSpecRoot(CONST), this);
      this.eArraySpec = new EArraySpec(writeBaseSpecRoot(EARRAY), this);
      this.fieldReadSpec = new FieldReadSpec(writeBaseSpecRoot(FIELD_READ), this);
      this.nullSpec = new NullSpec(writeBaseSpecRoot(NULL), this);
      this.refSpec = new RefSpec(writeBaseSpecRoot(REF), this);

      cacheSpec(blobSpec);
      cacheSpec(boolSpec);
      cacheSpec(intSpec);
      cacheSpec(nothingSpec);
      cacheSpec(strSpec);
      cacheSpec(callSpec);
      cacheSpec(constSpec);
      cacheSpec(eArraySpec);
      cacheSpec(fieldReadSpec);
      cacheSpec(nullSpec);
      cacheSpec(refSpec);
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  // methods for creating objects or object builders

  public ArrayBuilder arrayBuilder(ValSpec elementSpec) {
    return new ArrayBuilder(arraySpec(elementSpec), this);
  }

  public BlobBuilder blobBuilder() {
    return wrapHashedDbExceptionAsObjectDbException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool boolVal(boolean value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newBoolVal(value));
  }

  public DefinedLambda definedLambdaVal(DefinedLambdaSpec spec, Expr body,
      List<Expr> defaultArguments) {
    checkDefaultArgumentsCountMatchParametersCount("DefinedLambdaSpec", defaultArguments, spec);
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newDefinedLambdaVal(spec, body, defaultArguments));
  }

  public Int intVal(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newIntVal(value));
  }

  public NativeLambda nativeLambdaVal(
      NativeLambdaSpec spec, Str classBinaryName, Blob nativeJar, List<Expr> defaultArguments) {
    checkDefaultArgumentsCountMatchParametersCount("NativeLambdaSpec", defaultArguments, spec);
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newNativeLambdaVal(spec, classBinaryName, nativeJar, defaultArguments));
  }

  private static void checkDefaultArgumentsCountMatchParametersCount(
      String specName, List<Expr> defaultArguments, LambdaSpec spec) {
    int parameterCount = spec.parameters().items().size();
    if (parameterCount != defaultArguments.size()) {
      throw new IllegalArgumentException(specName + " specifies " + parameterCount
          + " parameters but defaultArguments provides " + defaultArguments.size() + " arguments.");
    }
  }

  public Str strVal(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newStrVal(value));
  }

  public Rec recVal(RecSpec recSpec, Iterable<? extends Obj> items) {
    List<Obj> itemList = ImmutableList.copyOf(items);
    var specs = recSpec.items();
    if (specs.size() != itemList.size()) {
      throw new IllegalArgumentException("recSpec specifies " + specs.size() +
          " items but provided " + itemList.size() + ".");
    }
    for (int i = 0; i < specs.size(); i++) {
      Spec specifiedSpec = specs.get(i);
      Spec elementSpec = itemList.get(i).spec();
      if (!specifiedSpec.equals(elementSpec)) {
        throw new IllegalArgumentException("recSpec specifies item at index " + i
            + " with spec " + specifiedSpec + " but provided item has spec " + elementSpec
            + " at that index.");
      }
    }
    return wrapHashedDbExceptionAsObjectDbException(() -> newRecVal(recSpec, itemList));
  }

  // methods for creating expr-s

  public Call callExpr(Expr function, Iterable<? extends Expr> arguments) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCallExpr(function, arguments));
  }

  public Const constExpr(Val val) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstExpr(val));
  }

  public EArray eArrayExpr(Iterable<? extends Expr> elements) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newEArrayExpr(elements));
  }

  public FieldRead fieldReadExpr(Expr rec, Int index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newFieldReadExpr(rec, index));
  }

  public Null nullExpr() {
    return wrapHashedDbExceptionAsObjectDbException(this::newNullExpr);
  }

  public Ref refExpr(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRefExpr(value));
  }

  // generic getter

  public Obj get(Hash hash) {
    List<Hash> hashes = decodeRootSequence(hash);
    if (hashes.size() != 1 && hashes.size() != 2) {
      throw wrongSizeOfRootSequenceException(hash, hashes.size());
    }
    Spec spec = getSpecOrChainException(
        hashes.get(0), e -> new DecodeObjSpecException(hash, e));
    if (spec.equals(nullSpec)) {
      if (hashes.size() != 1) {
        throw nullObjRootException(hash, hashes.size());
      }
      return spec.newObj(new MerkleRoot(hash, spec, null));
    } else {
      if (hashes.size() != 2) {
        throw nonNullObjRootException(hash, hashes.size());
      }
      Hash dataHash = hashes.get(1);
      return spec.newObj(new MerkleRoot(hash, spec, dataHash));
    }
  }

  private List<Hash> decodeRootSequence(Hash hash) {
    try {
      return hashedDb.readSequence(hash);
    } catch (NoSuchDataException e) {
      throw new NoSuchObjException(hash, e);
    } catch (HashedDbException e) {
      throw cannotReadRootException(hash, e);
    }
  }

  // methods for returning Val-s specs

  public ArraySpec arraySpec(ValSpec elementSpec) {
    return cacheSpec(wrapHashedDbExceptionAsObjectDbException(() -> newArraySpec(elementSpec)));
  }

  public BlobSpec blobSpec() {
    return blobSpec;
  }

  public BoolSpec boolSpec() {
    return boolSpec;
  }

  public DefinedLambdaSpec definedLambdaSpec(ValSpec result, RecSpec parameters) {
    return cacheSpec(
        wrapHashedDbExceptionAsObjectDbException(() -> newDefinedLambdaSpec(result, parameters)));
  }

  public IntSpec intSpec() {
    return intSpec;
  }

  public NativeLambdaSpec nativeLambdaSpec(ValSpec result, RecSpec parameters) {
    return cacheSpec(
        wrapHashedDbExceptionAsObjectDbException(() -> newNativeLambdaSpec(result, parameters)));
  }

  public NothingSpec nothingSpec() {
    return nothingSpec;
  }

  public StrSpec strSpec() {
    return strSpec;
  }

  // methods for returning Expr-s specs

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
    return cacheSpec(wrapHashedDbExceptionAsObjectDbException(() -> newRecSpec(itemSpecs)));
  }

  private Spec getSpecOrChainException(
      Hash specHash, Function<ObjectDbException, RuntimeException> exceptionChainer) {
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
    List<Hash> hashes = wrapHashedDbExceptionAsDecodeSpecException(
        hash, () -> hashedDb.readSequence(hash));
    int sequenceSize = hashes.size();
    if (sequenceSize != 1 && sequenceSize != 2) {
      throw new DecodeSpecRootException(hash, sequenceSize);
    }
    byte marker = wrapHashedDbExceptionAsDecodeSpecException(
        hash, () -> hashedDb.readByte(hashes.get(0)));
    SpecKind specKind = fromMarker(marker);
    if (specKind == null) {
      throw new DecodeSpecException(hash,
          "It has illegal SpecKind marker = " + marker + ".");
    }
    return switch (specKind) {
      case BLOB, BOOL, INT, NOTHING, STRING, CALL, CONST, EARRAY, FIELD_READ, NULL, REF -> {
        assertSize(hash, specKind, hashes, 1);
        throw new RuntimeException(
            "Internal error: Spec with kind " + specKind + " should be found in cache.");
      }
      case ARRAY -> {
        assertSize(hash, ARRAY, hashes, 2);
        Spec elementSpec = getSpecOrChainException(
            hashes.get(1), e -> new DecodeSpecException(hash));
        if (elementSpec instanceof ValSpec valSpec) {
          yield cacheSpec(newArraySpec(hash, valSpec));
        } else {
          throw new DecodeSpecException(hash, "It is ARRAY Spec which element Spec is "
              + elementSpec.name() + " but should be Spec of some Val.");
        }
      }
      case DEFINED_LAMBDA, NATIVE_LAMBDA -> {
        assertSize(hash, specKind, hashes, 2);
        List<Hash> data = wrapHashedDbExceptionAsDecodeSpecException(
            hash, () -> hashedDb.readSequence(hashes.get(1)));
        if (data.size() != 2) {
          throw new DecodeSpecException(hash, "It is " + specKind
              + " Spec which data sequence contains " + data.size()
              + " elements but should contains 2.");
        }
        Spec result = getSpecOrChainException(data.get(0), e -> new DecodeSpecException(hash));
        Spec parameters = getSpecOrChainException(data.get(1), e -> new DecodeSpecException(hash));
        if (!(result instanceof ValSpec resultSpec)) {
          throw new DecodeSpecException(hash, "It is " + specKind + " Spec which result spec is "
              + result.name() + " but should be instance of ValSpec.");
        }
        if (!(parameters instanceof RecSpec parametersSpec)) {
          throw new DecodeSpecException(hash, "It is " + specKind
              + " Spec which parameters spec is " + parameters.name()
              + " but should be instance of RecSpec.");
        }
        yield switch (specKind) {
          case DEFINED_LAMBDA -> cacheSpec(newDefinedLambdaSpec(hash, resultSpec, parametersSpec));
          case NATIVE_LAMBDA -> cacheSpec(newNativeLambdaSpec(hash, resultSpec, parametersSpec));
          default -> throw new RuntimeException("Cannot happen.");
        };
      }
      case RECORD -> {
        assertSize(hash, RECORD, hashes, 2);
        ImmutableList<Spec> items = readRecSpecItemSpecs(hashes.get(1), hash);
        yield cacheSpec(newRecSpec(hash, items));
      }
    };
  }

  private static void assertSize(Hash hash, SpecKind specKind, List<Hash> hashes,
      int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeSpecException(hash,
          "Its specKind == " + specKind + " but its merkle root has "
              + hashes.size() + " children when " + expectedSize + " is expected.");
    }
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

  private <T extends Spec> T cacheSpec(T spec) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(specCache.putIfAbsent(spec.hash(), spec), spec);
    return result;
  }

  // methods for creating Expr Obj-s

  public Call newCallExpr(Expr function, Iterable<? extends Expr> arguments)
      throws HashedDbException {
    var data = writeCallData(function, arguments);
    var root = writeRoot(callSpec, data);
    return callSpec.newObj(root);
  }

  public Const newConstExpr(Val val) throws HashedDbException {
    var data = writeConstData(val);
    var root = writeRoot(constSpec, data);
    return constSpec.newObj(root);
  }

  public EArray newEArrayExpr(Iterable<? extends Expr> elements) throws HashedDbException {
    var data = writeEarrayData(elements);
    var root = writeRoot(eArraySpec, data);
    return eArraySpec.newObj(root);
  }

  public FieldRead newFieldReadExpr(Expr rec, Int index) throws HashedDbException {
    var data = writeFieldReadData(rec, index);
    var root = writeRoot(fieldReadSpec, data);
    return fieldReadSpec.newObj(root);
  }

  public Null newNullExpr() throws HashedDbException {
    var root = writeRoot(nullSpec);
    return nullSpec.newObj(root);
  }

  public Ref newRefExpr(BigInteger value) throws HashedDbException {
    var data = writeRefData(value);
    var root = writeRoot(refSpec, data);
    return this.refSpec.newObj(root);
  }

  // methods for creating Val Obj-s

  public Array newArrayVal(ArraySpec spec, Iterable<? extends Obj> elements)
      throws HashedDbException {
    var data = writeArrayData(elements);
    var root = writeRoot(spec, data);
    return spec.newObj(root);
  }

  public Blob newBlobVal(Hash dataHash) throws HashedDbException {
    var root = writeRoot(blobSpec, dataHash);
    return blobSpec.newObj(root);
  }

  private Bool newBoolVal(boolean value) throws HashedDbException {
    var data = writeBoolData(value);
    var root = writeRoot(boolSpec, data);
    return boolSpec.newObj(root);
  }

  private DefinedLambda newDefinedLambdaVal(
      DefinedLambdaSpec spec, Expr body, List<Expr> defaultArguments) throws HashedDbException {
    var data = writeDefinedLambdaData(body, defaultArguments);
    var root = writeRoot(spec, data);
    return spec.newObj(root);
  }

  private Int newIntVal(BigInteger value) throws HashedDbException {
    var data = writeIntData(value);
    var root = writeRoot(intSpec, data);
    return intSpec.newObj(root);
  }

  private NativeLambda newNativeLambdaVal(
      NativeLambdaSpec spec, Str classBinaryName, Blob nativeJar, List<Expr> defaultArguments)
      throws HashedDbException {
    var data = writeNativeLambdaData(classBinaryName, nativeJar, defaultArguments);
    var root = writeRoot(spec, data);
    return spec.newObj(root);
  }

  private Str newStrVal(String string) throws HashedDbException {
    var data = writeStrData(string);
    var root = writeRoot(strSpec, data);
    return strSpec.newObj(root);
  }

  private Rec newRecVal(RecSpec spec, List<?extends Obj> objects) throws HashedDbException {
    var data = writeRecData(objects);
    var root = writeRoot(spec, data);
    return spec.newObj(root);
  }

  // methods for creating Spec-s

  private ArraySpec newArraySpec(ValSpec elementSpec) throws HashedDbException {
    Hash hash = writeArraySpecRoot(elementSpec);
    return newArraySpec(hash, elementSpec);
  }

  private ArraySpec newArraySpec(Hash hash, ValSpec elementSpec) {
    return new ArraySpec(hash, elementSpec, this);
  }

  private DefinedLambdaSpec newDefinedLambdaSpec(ValSpec result, RecSpec parameters)
      throws HashedDbException {
    Hash hash = writeLambdaSpecRoot(DEFINED_LAMBDA, result, parameters);
    return newDefinedLambdaSpec(hash, result, parameters);
  }

  private DefinedLambdaSpec newDefinedLambdaSpec(Hash hash, ValSpec result, RecSpec parameters) {
    return new DefinedLambdaSpec(hash, result, parameters, this);
  }

  private NativeLambdaSpec newNativeLambdaSpec(ValSpec result, RecSpec parameters)
      throws HashedDbException {
    Hash hash = writeLambdaSpecRoot(NATIVE_LAMBDA, result, parameters);
    return newNativeLambdaSpec(hash, result, parameters);
  }

  private NativeLambdaSpec newNativeLambdaSpec(Hash hash, ValSpec result, RecSpec parameters) {
    return new NativeLambdaSpec(hash, result, parameters, this);
  }

  private RecSpec newRecSpec(Iterable<? extends ValSpec> itemSpecs) throws HashedDbException {
    Hash hash = writeRecSpecRoot(itemSpecs);
    return newRecSpec(hash, itemSpecs);
  }

  private RecSpec newRecSpec(Hash hash, Iterable<? extends Spec> itemSpecs) {
    return new RecSpec(hash, itemSpecs, this);
  }

  // method for writing Merkle-root to HashedDb

  private MerkleRoot writeRoot(Spec spec) throws HashedDbException {
    Hash hash = hashedDb.writeSequence(spec.hash());
    return new MerkleRoot(hash, spec, null);
  }

  private MerkleRoot writeRoot(Spec spec, Hash dataHash) throws HashedDbException {
    Hash hash = hashedDb.writeSequence(spec.hash(), dataHash);
    return new MerkleRoot(hash, spec, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(Expr function, Iterable<? extends Expr> arguments)
      throws HashedDbException {
    Hash argumentSequenceHash = writeSequence(arguments);
    return hashedDb.writeSequence(function.hash(), argumentSequenceHash);
  }

  private Hash writeConstData(Val val) {
    return val.hash();
  }

  private Hash writeEarrayData(Iterable<? extends Expr> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeFieldReadData(Expr rec, Int index) throws HashedDbException {
    return hashedDb.writeSequence(rec.hash(), index.hash());
  }

  private Hash writeRefData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  // methods for writing data of Val-s

  private Hash writeArrayData(Iterable<? extends Obj> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeDefinedLambdaData(Expr body, List<Expr> defaultArguments)
      throws HashedDbException {
    Hash defaultArgumentsHash = writeSequence(defaultArguments);
    return hashedDb.writeSequence(body.hash(), defaultArgumentsHash);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeNativeLambdaData(
      Str classBinaryName, Blob nativeJar, List<Expr> defaultArguments) throws HashedDbException {
    Hash nativeHash = hashedDb.writeSequence(classBinaryName.hash(), nativeJar.hash());
    Hash defaultArgumentsHash = writeSequence(defaultArguments);
    return hashedDb.writeSequence(nativeHash, defaultArgumentsHash);
  }

  private Hash writeStrData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeRecData(List<? extends Obj> items) throws HashedDbException {
    return writeSequence(items);
  }

  // helpers

  private Hash writeSequence(Iterable<? extends Obj> objs) throws HashedDbException {
    var hashes = map(objs, Obj::hash);
    return hashedDb.writeSequence(hashes);
  }

  public ImmutableList<Hash> readSequence(Hash hash) throws HashedDbException {
    return hashedDb().readSequence(hash);
  }

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

  // TODO visible for classes from db.object package tree until creating Obj is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
