package org.smoothbuild.db.object.db;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.nonNullObjRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.nullObjRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;
import static org.smoothbuild.util.Lists.allMatchOtherwise;
import static org.smoothbuild.util.Lists.map;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import org.smoothbuild.db.object.obj.expr.ArrayExpr;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Invoke;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.RecExpr;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.SelectSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;

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

  public Lambda lambdaVal(LambdaSpec spec, Expr body, RecExpr defaultArguments) {
    if (!Objects.equals(spec.result(), body.evaluationSpec())) {
      throw new IllegalArgumentException("`spec` specifies result as " + spec.result().name()
          + " but body.evaluationSpec() is " + body.evaluationSpec().name() + ".");
    }
    verifyArguments(spec, defaultArguments, "Default arguments");
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newLambdaVal(spec, body, defaultArguments));
  }

  public Int intVal(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newIntVal(value));
  }

  public Str strVal(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newStrVal(value));
  }

  public Struc_ structVal(StructSpec structSpec, Rec rec) {
    checkArgument(Objects.equals(structSpec.rec(), rec.spec()));
    return wrapHashedDbExceptionAsObjectDbException(() -> newStructVal(structSpec, rec));
  }

  public Rec recVal(RecSpec recSpec, Iterable<? extends Obj> items) {
    List<Obj> itemList = ImmutableList.copyOf(items);
    var specs = recSpec.items();

    allMatchOtherwise(specs, itemList, (s, i) -> Objects.equals(s, i.spec()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "recSpec specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("recSpec specifies item at index " + i
              + " with spec " + specs.get(i).name() + " but provided item has spec "
              + itemList.get(i).spec().name() + " at that index.");
        }
    );

    return wrapHashedDbExceptionAsObjectDbException(() -> newRecVal(recSpec, itemList));
  }

  // methods for creating expr-s

  public Call callExpr(Expr function, RecExpr arguments) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCallExpr(function, arguments));
  }

  public Const constExpr(Val val) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstExpr(val));
  }

  public ArrayExpr arrayExpr(Iterable<? extends Expr> elements) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArrayExpr(elements));
  }

  public RecExpr eRecExpr(Iterable<? extends Expr> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newERecExpr(items));
  }

  public Select selectExpr(Expr rec, Int index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelectExpr(rec, index));
  }

  public Invoke invokeExpr(Blob jarFile, Str classBinaryName, ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newInvokeExpr(jarFile, classBinaryName, evaluationSpec));
  }

  public Null nullExpr() {
    return wrapHashedDbExceptionAsObjectDbException(this::newNullExpr);
  }

  public Ref refExpr(BigInteger value, ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRefExpr(evaluationSpec, value));
  }

  // generic getter

  public Obj get(Hash rootHash) {
    List<Hash> hashes = decodeRootSequence(rootHash);
    if (hashes.size() != 1 && hashes.size() != 2) {
      throw wrongSizeOfRootSequenceException(rootHash, hashes.size());
    }
    Spec spec = getSpecOrChainException(rootHash, hashes.get(0));
    if (spec.equals(specDb.nullSpec())) {
      if (hashes.size() != 1) {
        throw nullObjRootException(rootHash, hashes.size());
      }
      return spec.newObj(new MerkleRoot(rootHash, spec, null), this);
    } else {
      if (hashes.size() != 2) {
        throw nonNullObjRootException(rootHash, hashes.size());
      }
      Hash dataHash = hashes.get(1);
      return spec.newObj(new MerkleRoot(rootHash, spec, dataHash), this);
    }
  }

  private Spec getSpecOrChainException(Hash rootHash, Hash specHash) {
    try {
      return specDb.getSpec(specHash);
    } catch (ObjectDbException e) {
      throw new DecodeObjSpecException(rootHash, e);
    }
  }

  private List<Hash> decodeRootSequence(Hash rootHash) {
    try {
      return hashedDb.readSequence(rootHash);
    } catch (NoSuchDataException e) {
      throw new NoSuchObjException(rootHash, e);
    } catch (HashedDbException e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  // methods for creating Expr Obj-s

  private Call newCallExpr(Expr function, RecExpr arguments)
      throws HashedDbException {
    var lambdaSpec = functionEvaluationSpec(function);
    verifyArguments(lambdaSpec, arguments, "Arguments");
    var spec = specDb.callSpec(lambdaSpec.result());
    var data = writeCallData(function, arguments);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private static void verifyArguments(LambdaSpec lambdaSpec, RecExpr arguments, String name) {
    if (!Objects.equals(lambdaSpec.parameters(), arguments.evaluationSpec())) {
      throw new IllegalArgumentException((name + " evaluation spec %s should be equal to "
          + "function evaluation spec parameters %s.")
              .formatted(arguments.evaluationSpec().name(), lambdaSpec.parameters().name()));
    }
  }

  private LambdaSpec functionEvaluationSpec(Expr function) {
    if (function.evaluationSpec() instanceof LambdaSpec lambdaSpec) {
      return lambdaSpec;
    } else {
      throw new IllegalArgumentException("`function` component doesn't evaluate to Function.");
    }
  }

  private Const newConstExpr(Val val) throws HashedDbException {
    var spec = specDb.constSpec(val.spec());
    var data = writeConstData(val);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private ArrayExpr newArrayExpr(Iterable<? extends Expr> elements) throws HashedDbException {
    ValSpec elementSpec = elementSpec(elements);
    var spec = specDb.arrayExprSpec(elementSpec);
    var data = writeArrayExprData(elements);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private ValSpec elementSpec(Iterable<? extends Expr> elements) {
    Optional<ValSpec> elementSpec = stream(elements)
        .map(expr -> expr.spec().evaluationSpec())
        .reduce((spec1, spec2) -> {
          if (spec1.equals(spec2)) {
            return spec1;
          } else {
            throw new IllegalArgumentException("Element evaluation specs are not equal "
                + spec1.name() + " != " + spec2.name() + ".");
          }
        });
    Spec spec = elementSpec.orElse(specDb.nothingSpec());
    if (spec instanceof ValSpec valSpec) {
      return valSpec;
    } else {
      throw new IllegalArgumentException(
          "Element specs should be ValSpec but was " + spec.getClass().getCanonicalName());
    }
  }

  private RecExpr newERecExpr(Iterable<? extends Expr> items) throws HashedDbException {
    var itemSpecs = map(items, Expr::evaluationSpec);
    var spec = specDb.recExprSpec(itemSpecs);
    var data = writeERecData(items);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Select newSelectExpr(Expr rec, Int index) throws HashedDbException {
    var spec = selectSpec(rec, index);
    var data = writeSelectData(rec, index);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private SelectSpec selectSpec(Expr rec, Int index) {
    if (rec.spec().evaluationSpec() instanceof RecSpec recSpec) {
      var items = recSpec.items();
      int intIndex = index.jValue().intValue();
      checkElementIndex(intIndex, items.size());
      var fieldSpec = items.get(intIndex);
      return specDb.selectSpec(fieldSpec);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private Invoke newInvokeExpr(Blob jarFile, Str classBinaryName, ValSpec evaluationSpec)
      throws HashedDbException {
    var spec = specDb.invokeSpec(evaluationSpec);
    var data = writeInvokeData(jarFile, classBinaryName);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Null newNullExpr() throws HashedDbException {
    var root = writeRoot(specDb.nullSpec());
    return specDb.nullSpec().newObj(root, this);
  }

  private Ref newRefExpr(ValSpec evaluationSpec, BigInteger index) throws HashedDbException {
    var data = writeRefData(index);
    var spec = specDb.refSpec(evaluationSpec);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
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

  private Lambda newLambdaVal(LambdaSpec spec, Expr body, RecExpr defaultArguments)
      throws HashedDbException {
    var data = writeLambdaData(body, defaultArguments);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Int newIntVal(BigInteger value) throws HashedDbException {
    var data = writeIntData(value);
    var root = writeRoot(specDb.intSpec(), data);
    return specDb.intSpec().newObj(root, this);
  }

  private Str newStrVal(String string) throws HashedDbException {
    var data = writeStrData(string);
    var root = writeRoot(specDb.strSpec(), data);
    return specDb.strSpec().newObj(root, this);
  }

  private Struc_ newStructVal(StructSpec spec, Rec rec) throws HashedDbException {
    var root = writeRoot(spec, rec.hash());
    return spec.newObj(root, this);
  }

  private Rec newRecVal(RecSpec spec, List<? extends Obj> objects) throws HashedDbException {
    var data = writeRecData(objects);
    var root = writeRoot(spec, data);
    return spec.newObj(root, this);
  }

  // method for writing Merkle-root to HashedDb

  private MerkleRoot writeRoot(Spec spec) throws HashedDbException {
    Hash rootHash = hashedDb.writeSequence(spec.hash());
    return new MerkleRoot(rootHash, spec, null);
  }

  private MerkleRoot writeRoot(Spec spec, Hash dataHash) throws HashedDbException {
    Hash rootHash = hashedDb.writeSequence(spec.hash(), dataHash);
    return new MerkleRoot(rootHash, spec, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(Expr function, RecExpr arguments) throws HashedDbException {
    return hashedDb.writeSequence(function.hash(), arguments.hash());
  }

  private Hash writeConstData(Val val) {
    return val.hash();
  }

  private Hash writeArrayExprData(Iterable<? extends Expr> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeInvokeData(Blob jarFile, Str classBinaryName) throws HashedDbException {
    return hashedDb.writeSequence(jarFile.hash(), classBinaryName.hash());
  }

  private Hash writeERecData(Iterable<? extends Expr> items) throws HashedDbException {
    return writeSequence(items);
  }

  private Hash writeSelectData(Expr rec, Int index) throws HashedDbException {
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

  private Hash writeLambdaData(Expr body, RecExpr defaultArguments) throws HashedDbException {
    return hashedDb.writeSequence(body.hash(), defaultArguments.hash());
  }

  private Hash writeIntData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
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
