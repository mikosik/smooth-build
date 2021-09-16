package org.smoothbuild.db.object.db;

import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.nonNullObjRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.nullObjRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;
import static org.smoothbuild.util.Lists.map;

import java.math.BigInteger;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.hashed.exc.NoSuchDataException;
import org.smoothbuild.db.object.exc.DecodeObjSpecException;
import org.smoothbuild.db.object.exc.NoSuchObjException;
import org.smoothbuild.db.object.exc.ObjectDbException;
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
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjectDb {
  private final HashedDb hashedDb;
  private final SpecDb specDb;

  public ObjectDb(HashedDb hashedDb, SpecDb specDb) {
    this.hashedDb = hashedDb;
    this.specDb = specDb;
  }

  // methods for creating objects or object builders

  public ArrayBuilder arrayBuilder(ValSpec elementSpec) {
    return new ArrayBuilder(specDb.arraySpec(elementSpec), this);
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
    Spec spec = getSpecOrChainException(hash, hashes.get(0));
    if (spec.equals(specDb.nullSpec())) {
      if (hashes.size() != 1) {
        throw nullObjRootException(hash, hashes.size());
      }
      return spec.newObj(new MerkleRoot(hash, spec, null), this);
    } else {
      if (hashes.size() != 2) {
        throw nonNullObjRootException(hash, hashes.size());
      }
      Hash dataHash = hashes.get(1);
      return spec.newObj(new MerkleRoot(hash, spec, dataHash), this);
    }
  }

  private Spec getSpecOrChainException(Hash hash, Hash specHash) {
    try {
      return specDb.getSpec(specHash);
    } catch (ObjectDbException e) {
      throw new DecodeObjSpecException(hash, e);
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

  // methods for creating Expr Obj-s

  public Call newCallExpr(Expr function, Iterable<? extends Expr> arguments)
      throws HashedDbException {
    var data = writeCallData(function, arguments);
    var root = writeRoot(specDb.callSpec(), data);
    return specDb.callSpec().newObj(root, this);
  }

  public Const newConstExpr(Val val) throws HashedDbException {
    var data = writeConstData(val);
    var root = writeRoot(specDb.constSpec(), data);
    return specDb.constSpec().newObj(root, this);
  }

  public EArray newEArrayExpr(Iterable<? extends Expr> elements) throws HashedDbException {
    var data = writeEarrayData(elements);
    var root = writeRoot(specDb.eArraySpec(), data);
    return specDb.eArraySpec().newObj(root, this);
  }

  public FieldRead newFieldReadExpr(Expr rec, Int index) throws HashedDbException {
    var data = writeFieldReadData(rec, index);
    var root = writeRoot(specDb.fieldReadSpec(), data);
    return specDb.fieldReadSpec().newObj(root, this);
  }

  public Null newNullExpr() throws HashedDbException {
    var root = writeRoot(specDb.nullSpec());
    return specDb.nullSpec().newObj(root, this);
  }

  public Ref newRefExpr(BigInteger value) throws HashedDbException {
    var data = writeRefData(value);
    var root = writeRoot(specDb.refSpec(), data);
    return specDb.refSpec().newObj(root, this);
  }

  // methods for creating Val Obj-s

  public Array newArrayVal(ArraySpec spec, Iterable<? extends Obj> elements)
      throws HashedDbException {
    var data = writeArrayData(elements);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  public Blob newBlobVal(Hash dataHash) throws HashedDbException {
    var root = writeRoot(specDb.blobSpec(), dataHash);
    return specDb.blobSpec().newObj(root, this);
  }

  private Bool newBoolVal(boolean value) throws HashedDbException {
    var data = writeBoolData(value);
    var root = writeRoot(specDb.boolSpec(), data);
    return specDb.boolSpec().newObj(root, this);
  }

  private DefinedLambda newDefinedLambdaVal(
      DefinedLambdaSpec spec, Expr body, List<Expr> defaultArguments) throws HashedDbException {
    var data = writeDefinedLambdaData(body, defaultArguments);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Int newIntVal(BigInteger value) throws HashedDbException {
    var data = writeIntData(value);
    var root = writeRoot(specDb.intSpec(), data);
    return specDb.intSpec().newObj(root, this);
  }

  private NativeLambda newNativeLambdaVal(
      NativeLambdaSpec spec, Str classBinaryName, Blob nativeJar, List<Expr> defaultArguments)
      throws HashedDbException {
    var data = writeNativeLambdaData(classBinaryName, nativeJar, defaultArguments);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Str newStrVal(String string) throws HashedDbException {
    var data = writeStrData(string);
    var root = writeRoot(specDb.strSpec(), data);
    return specDb.strSpec().newObj(root, this);
  }

  private Rec newRecVal(RecSpec spec, List<?extends Obj> objects) throws HashedDbException {
    var data = writeRecData(objects);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
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

  // TODO visible for classes from db.object package tree until creating Obj is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
