package org.smoothbuild.db.object.obj;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.map;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.hashed.exc.NoSuchDataException;
import org.smoothbuild.db.object.db.ObjectDbException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.DecodeObjSpecException;
import org.smoothbuild.db.object.obj.exc.NoSuchObjException;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.obj.expr.Invoke;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.expr.StructExpr;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.NativeMethod;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.spec.SpecDb;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.SelectSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;
import org.smoothbuild.util.collect.Named;

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

  // methods for creating value or value builders

  public ArrayBuilder arrayBuilder(ValSpec elementSpec) {
    return new ArrayBuilder(specDb.array(elementSpec), this);
  }

  public BlobBuilder blobBuilder() {
    return wrapHashedDbExceptionAsObjectDbException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool bool(boolean value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newBool(value));
  }

  public Lambda lambda(LambdaSpec spec, Expr body) {
    if (!Objects.equals(spec.result(), body.evaluationSpec())) {
      throw new IllegalArgumentException("`spec` specifies result as " + spec.result().name()
          + " but body.evaluationSpec() is " + body.evaluationSpec().name() + ".");
    }
    return wrapHashedDbExceptionAsObjectDbException(() -> newLambda(spec, body));
  }

  public Int int_(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInt(value));
  }

  public NativeMethod nativeMethod(Blob jarFile, Str classBinaryName) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newNativeMethod(jarFile, classBinaryName));
  }

  public Str string(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newString(value));
  }

  public Struc_ struct(StructSpec structSpec, ImmutableList<Val> items) {
    var fieldTypes = map(structSpec.fields().list(), Named::object);
    allMatchOtherwise(fieldTypes, items, (f, i) -> Objects.equals(f, i.spec()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "structSpec specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("structSpec specifies field at index " + i
              + " with spec " + fieldTypes.get(i).name() + " but provided item has spec "
              + items.get(i).spec().name() + " at that index.");
        }
    );
    return wrapHashedDbExceptionAsObjectDbException(() -> newStruct(structSpec, items));
  }

  public Tuple tuple(TupleSpec tupleSpec, Iterable<? extends Obj> items) {
    List<Obj> itemList = ImmutableList.copyOf(items);
    var specs = tupleSpec.items();

    allMatchOtherwise(specs, itemList, (s, i) -> Objects.equals(s, i.spec()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "TupleSpec specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("TupleSpec specifies item at index " + i
              + " with spec " + specs.get(i).name() + " but provided item has spec "
              + itemList.get(i).spec().name() + " at that index.");
        }
    );

    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(tupleSpec, itemList));
  }

  // methods for creating expr-s

  public Call call(Expr function, Construct arguments) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(function, arguments));
  }

  public Const const_(Val val) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConst(val));
  }

  public Construct construct(ImmutableList<? extends Expr> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstruct(items));
  }

  public Invoke invoke(
      ValSpec evaluationSpec, NativeMethod nativeMethod, Bool isPure, Int argumentCount) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newInvoke(evaluationSpec, nativeMethod, isPure, argumentCount));
  }

  public Order order(List<? extends Expr> elements) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elements));
  }

  public Ref ref(BigInteger value, ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evaluationSpec, value));
  }

  public Select select(Expr struct, Int index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(struct, index));
  }

  public StructExpr structExpr(StructSpec evaluationSpec, ImmutableList<? extends Expr> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newStructExpr(evaluationSpec, items));
  }

  // generic getter

  public Obj get(Hash rootHash) {
    List<Hash> hashes = decodeRootSequence(rootHash);
    if (hashes.size() != 2) {
      throw wrongSizeOfRootSequenceException(rootHash, hashes.size());
    }
    Spec spec = getSpecOrChainException(rootHash, hashes.get(0));
    Hash dataHash = hashes.get(1);
    return spec.newObj(new MerkleRoot(rootHash, spec, dataHash), this);
  }

  private Spec getSpecOrChainException(Hash rootHash, Hash specHash) {
    try {
      return specDb.get(specHash);
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

  // methods for creating Val Obj-s

  public Array newArray(ArraySpec spec, List<? extends Obj> elements) throws HashedDbException {
    var data = writeArrayData(elements);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  public Blob newBlob(Hash dataHash) throws HashedDbException {
    var root = newRoot(specDb.blob(), dataHash);
    return specDb.blob().newObj(root, this);
  }

  private Bool newBool(boolean value) throws HashedDbException {
    var data = writeBoolData(value);
    var root = newRoot(specDb.bool(), data);
    return specDb.bool().newObj(root, this);
  }

  private Lambda newLambda(LambdaSpec spec, Expr body) throws HashedDbException {
    var data = writeLambdaData(body);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Int newInt(BigInteger value) throws HashedDbException {
    var data = writeIntData(value);
    var root = newRoot(specDb.int_(), data);
    return specDb.int_().newObj(root, this);
  }

  private NativeMethod newNativeMethod(Blob jarFile, Str classBinaryName) throws HashedDbException {
    var spec = specDb.nativeMethod();
    var data = writeNativeMethodData(jarFile, classBinaryName);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Str newString(String string) throws HashedDbException {
    var data = writeStringData(string);
    var root = newRoot(specDb.string(), data);
    return specDb.string().newObj(root, this);
  }

  private Struc_ newStruct(StructSpec spec, ImmutableList<Val> items) throws HashedDbException {
    var data = writeStructData(items);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Tuple newTuple(TupleSpec spec, List<? extends Obj> objects) throws HashedDbException {
    var data = writeTupleData(objects);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  // methods for creating Expr-s

  private Call newCall(Expr function, Construct arguments) throws HashedDbException {
    var lambdaSpec = functionEvaluationSpec(function);
    verifyArguments(lambdaSpec, arguments);
    var spec = specDb.call(lambdaSpec.result());
    var data = writeCallData(function, arguments);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private static void verifyArguments(LambdaSpec lambdaSpec, Construct arguments) {
    if (!Objects.equals(lambdaSpec.parametersTuple(), arguments.evaluationSpec())) {
      throw new IllegalArgumentException(("Arguments evaluation spec %s should be equal to "
          + "function evaluation spec parameters %s.")
          .formatted(arguments.evaluationSpec().name(), lambdaSpec.parametersTuple().name()));
    }
  }

  private LambdaSpec functionEvaluationSpec(Expr function) {
    if (function.evaluationSpec() instanceof LambdaSpec lambdaSpec) {
      return lambdaSpec;
    } else {
      throw new IllegalArgumentException("`function` component doesn't evaluate to Function.");
    }
  }

  private Const newConst(Val val) throws HashedDbException {
    var spec = specDb.const_(val.spec());
    var data = writeConstData(val);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Invoke newInvoke(ValSpec evaluationSpec, NativeMethod nativeMethod, Bool isPure,
      Int argumentCount) throws HashedDbException {
    var data = writeInvokeData(nativeMethod, isPure, argumentCount);
    var spec = specDb.invoke(evaluationSpec);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Order newOrder(List<? extends Expr> elements) throws HashedDbException {
    ValSpec elementSpec = elementSpec(elements);
    var spec = specDb.order(elementSpec);
    var data = writeOrderData(elements);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private ValSpec elementSpec(List<? extends Expr> elements) {
    Optional<ValSpec> elementSpec = elements.stream()
        .map(expr -> expr.spec().evaluationSpec())
        .reduce((spec1, spec2) -> {
          if (spec1.equals(spec2)) {
            return spec1;
          } else {
            throw new IllegalArgumentException("Element evaluation specs are not equal "
                + spec1.name() + " != " + spec2.name() + ".");
          }
        });
    Spec spec = elementSpec.orElse(specDb.nothing());
    if (spec instanceof ValSpec valSpec) {
      return valSpec;
    } else {
      throw new IllegalArgumentException(
          "Element specs should be ValSpec but was " + spec.getClass().getCanonicalName());
    }
  }

  private Construct newConstruct(List<? extends Expr> items) throws HashedDbException {
    var itemSpecs = map(items, Expr::evaluationSpec);
    var evaluationSpec = specDb.tuple(itemSpecs);
    var spec = specDb.construct(evaluationSpec);
    var data = writeConstructData(items);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Select newSelect(Expr struct, Int index) throws HashedDbException {
    var spec = selectSpec(struct, index);
    var data = writeSelectData(struct, index);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private SelectSpec selectSpec(Expr expr, Int index) {
    if (expr.spec().evaluationSpec() instanceof StructSpec struct) {
      var fields = struct.fields();
      int intIndex = index.jValue().intValue();
      checkElementIndex(intIndex, fields.size());
      var field = fields.getObject(intIndex);
      return specDb.select(field);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private StructExpr newStructExpr(StructSpec evaluationSpec, List<? extends Expr> items)
      throws HashedDbException {
    ImmutableList<Named<ValSpec>> specs = evaluationSpec.fields().list();
    allMatchOtherwise(specs, items,
        (f, v) -> f.object().equals(v.evaluationSpec()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "StructSpec specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("StructSpec specifies item at index " + i
              + " with spec " + specs.get(i).object().name() + " but provided item has spec "
              + items.get(i).spec().name() + " at that index.");
        });

    var spec = specDb.structExpr(evaluationSpec);
    var data = writeStructExprData(items);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private Ref newRef(ValSpec evaluationSpec, BigInteger index) throws HashedDbException {
    var data = writeRefData(index);
    var spec = specDb.ref(evaluationSpec);
    var root = newRoot(spec, data);
    return spec.newObj(root, this);
  }

  private MerkleRoot newRoot(Spec spec, Hash dataHash) throws HashedDbException {
    Hash rootHash = hashedDb.writeSequence(spec.hash(), dataHash);
    return new MerkleRoot(rootHash, spec, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(Expr function, Construct arguments) throws HashedDbException {
    return hashedDb.writeSequence(function.hash(), arguments.hash());
  }

  private Hash writeConstData(Val val) {
    return val.hash();
  }

  private Hash writeConstructData(List<? extends Expr> items) throws HashedDbException {
    return writeSequence(items);
  }

  private Hash writeInvokeData(NativeMethod nativeMethod,
      Bool isPure, Int argumentCount) throws HashedDbException {
    return hashedDb.writeSequence(nativeMethod.hash(), isPure.hash(), argumentCount.hash());
  }

  private Hash writeNativeMethodData(Blob jarFile, Str classBinaryName) throws HashedDbException {
    return hashedDb.writeSequence(jarFile.hash(), classBinaryName.hash());
  }

  private Hash writeOrderData(List<? extends Expr> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeRefData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeSelectData(Expr struct, Int index) throws HashedDbException {
    return hashedDb.writeSequence(struct.hash(), index.hash());
  }

  private Hash writeStructExprData(List<? extends Expr> items) throws HashedDbException {
    return writeSequence(items);
  }

  // methods for writing data of Val-s

  private Hash writeArrayData(List<? extends Obj> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeLambdaData(Expr body) {
    return body.hash();
  }

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeStructData(List<? extends Val> items) throws HashedDbException {
    return writeSequence(items);
  }

  private Hash writeTupleData(List<? extends Obj> items) throws HashedDbException {
    return writeSequence(items);
  }

  // helpers

  private Hash writeSequence(List<? extends Obj> objs) throws HashedDbException {
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
