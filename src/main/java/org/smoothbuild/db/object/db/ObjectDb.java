package org.smoothbuild.db.object.db;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.EARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.FIELD_READ;
import static org.smoothbuild.db.object.spec.base.SpecKind.INT;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.base.SpecKind.TUPLE;
import static org.smoothbuild.db.object.spec.base.SpecKind.fromMarker;
import static org.smoothbuild.util.Lists.map;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.obj.expr.FieldRead;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.EArraySpec;
import org.smoothbuild.db.object.spec.expr.FieldReadSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;

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
  private ConstSpec constSpec;
  private FieldReadSpec fieldReadSpec;
  private CallSpec callSpec;
  private EArraySpec eArraySpec;

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

      cacheSpec(blobSpec);
      cacheSpec(boolSpec);
      cacheSpec(intSpec);
      cacheSpec(nothingSpec);
      cacheSpec(strSpec);
      cacheSpec(callSpec);
      cacheSpec(constSpec);
      cacheSpec(eArraySpec);
      cacheSpec(fieldReadSpec);
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  // methods for creating objects or object builders

  public ArrayBuilder arrayBuilder(ValSpec elementSpec) {
    return new ArrayBuilder(arrayS(elementSpec), this);
  }

  public BlobBuilder blobBuilder() {
    return wrapHashedDbExceptionAsObjectDbException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool boolVal(boolean value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newBoolV(value));
  }

  public Int intVal(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newIntV(value));
  }

  public Str strVal(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newStrV(value));
  }

  public Tuple tupleVal(TupleSpec tupleSpec, Iterable<? extends Obj> elements) {
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
    return wrapHashedDbExceptionAsObjectDbException(() -> newTupleV(tupleSpec, elementsList));
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

  public FieldRead fieldReadExpr(Expr tuple, Int index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newFieldReadExpr(tuple, index));
  }

  // generic getter

  public Obj get(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 2);
      Spec spec = getSpecOrChainException(
          hashes.get(0), e -> new DecodeObjException(hash, e));
      Hash dataHash = hashes.get(1);
      return spec.newObj(new MerkleRoot(hash, spec, dataHash));
    } catch (HashedDbException e) {
      throw new DecodeObjException(hash, e);
    }
  }

  // methods for returning specs

  public ArraySpec arrayS(ValSpec elementSpec) {
    return cacheSpec(wrapHashedDbExceptionAsObjectDbException(() -> newArrayS(elementSpec)));
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

  public CallSpec callS() {
    return callSpec;
  }

  public ConstSpec constS() {
    return constSpec;
  }

  public EArraySpec eArrayS() {
    return eArraySpec;
  }

  public FieldReadSpec fieldReadS() {
    return fieldReadSpec;
  }

  public TupleSpec tupleS(Iterable<? extends ValSpec> elementSpecs) {
    return cacheSpec(wrapHashedDbExceptionAsObjectDbException(() -> newTupleS(elementSpecs)));
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
        throw new DecodeSpecException(hash,
            "It has illegal SpecKind marker = " + marker + ".");
      }
      return switch (specKind) {
        case BLOB, BOOL, INT, NOTHING, STRING, CALL, CONST, EARRAY, FIELD_READ -> {
          assertSize(hash, specKind, hashes, 1);
          throw new RuntimeException(
              "Internal error: Spec with kind " + specKind + " should be found in cache.");
        }
        case ARRAY -> {
          assertSize(hash, ARRAY, hashes, 2);
          Spec elementSpec = getSpecOrChainException(
              hashes.get(1), e -> new DecodeSpecException(hash));
          if (elementSpec instanceof ValSpec valSpec) {
            yield cacheSpec(newArrayS(hash, valSpec));
          } else {
            throw new DecodeSpecException(hash, "It is ARRAY Spec which element Spec is "
                + elementSpec.name() + " but should be Spec of some Val.");
          }
        }
        case TUPLE -> {
          assertSize(hash, TUPLE, hashes, 2);
          ImmutableList<Spec> elements = readTupleSpecElementSpecs(hashes.get(1), hash);
          yield cacheSpec(newTupleS(hash, elements));
        }
      };
    } catch (HashedDbException e) {
      throw new DecodeSpecException(hash, e);
    }
  }

  private static void assertSize(Hash hash, SpecKind specKind, List<Hash> hashes,
      int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeSpecException(hash,
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
        throw new DecodeSpecException(parentHash, "Its specKind == TUPLE "
            + "but reading element spec at index " + i + " caused error.", e);
      }
    }
    return builder.build();
  }

  private List<Hash> readTupleSpecElementSpecHashes(Hash hash, Hash parentHash) {
    try {
      return hashedDb.readHashes(hash);
    } catch (HashedDbException e) {
      throw new DecodeSpecException(parentHash,
          "Its specKind == TUPLE but reading its element specs caused error.", e);
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
    var root = writeRoot(this.constSpec, data);
    return this.constSpec.newObj(root);
  }

  public EArray newEArrayExpr(Iterable<? extends Expr> elements) throws HashedDbException {
    var data = writeEarrayData(elements);
    var root = writeRoot(eArraySpec, data);
    return eArraySpec.newObj(root);
  }

  public FieldRead newFieldReadExpr(Expr tuple, Int index) throws HashedDbException {
    var data = writeFieldReadData(tuple, index);
    var root = writeRoot(fieldReadSpec, data);
    return fieldReadSpec.newObj(root);
  }

  // methods for creating Val Obj-s

  public Array newArrayV(ArraySpec spec, Iterable<? extends Obj> elements)
      throws HashedDbException {
    var data = writeArrayData(elements);
    var root = writeRoot(spec, data);
    return spec.newObj(root);
  }

  public Blob newBlobV(Hash dataHash) throws HashedDbException {
    var root = writeRoot(blobSpec, dataHash);
    return blobSpec.newObj(root);
  }

  private Bool newBoolV(boolean value) throws HashedDbException {
    var data = writeBoolData(value);
    var root = writeRoot(boolSpec, data);
    return boolSpec.newObj(root);
  }

  private Int newIntV(BigInteger value) throws HashedDbException {
    var data = writeIntData(value);
    var root = writeRoot(intSpec, data);
    return intSpec.newObj(root);
  }

  private Str newStrV(String string) throws HashedDbException {
    var data = writeStrData(string);
    var root = writeRoot(strSpec, data);
    return strSpec.newObj(root);
  }

  private Tuple newTupleV(TupleSpec spec, List<?extends Obj> objects) throws HashedDbException {
    var data = writeTupleData(objects);
    var root = writeRoot(spec, data);
    return spec.newObj(root);
  }

  // methods for creating Spec-s

  private ArraySpec newArrayS(ValSpec elementSpec) throws HashedDbException {
    Hash hash = writeArraySpecRoot(elementSpec);
    return newArrayS(hash, elementSpec);
  }

  private ArraySpec newArrayS(Hash hash, ValSpec elementSpec) {
    return new ArraySpec(hash, elementSpec, this);
  }

  private TupleSpec newTupleS(Iterable<? extends ValSpec> elementSpecs) throws HashedDbException {
    Hash hash = writeTupleSpecRoot(elementSpecs);
    return newTupleS(hash, elementSpecs);
  }

  private TupleSpec newTupleS(Hash hash, Iterable<? extends Spec> elementSpecs) {
    return new TupleSpec(hash, elementSpecs, this);
  }

  // method for writing Merkle-root to HashedDb

  private MerkleRoot writeRoot(Spec spec, Hash dataHash) throws HashedDbException {
    Hash hash = hashedDb.writeHashes(spec.hash(), dataHash);
    return new MerkleRoot(hash, spec, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(Expr function, Iterable<? extends Expr> arguments)
      throws HashedDbException {
    Hash argumentSequenceHash = writeSequence(arguments);
    return hashedDb.writeHashes(function.hash(), argumentSequenceHash);
  }

  private Hash writeConstData(Val val) {
    return val.hash();
  }

  private Hash writeEarrayData(Iterable<? extends Expr> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeFieldReadData(Expr tuple, Int index) throws HashedDbException {
    return hashedDb.writeHashes(tuple.hash(), index.hash());
  }

  // methods for writing data of Val-s

  private Hash writeArrayData(Iterable<? extends Obj> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeStrData(String string) throws HashedDbException {
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

  public ImmutableList<Hash> readSequence(Hash hash) throws HashedDbException {
    return hashedDb().readHashes(hash);
  }

  private Hash writeArraySpecRoot(Spec elementSpec) throws HashedDbException {
    return writeNonBaseSpecRoot(ARRAY, elementSpec.hash());
  }

  private Hash writeTupleSpecRoot(Iterable<? extends ValSpec> elementSpecs)
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

  // TODO visible for classes from db.object package tree until creating Obj is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
