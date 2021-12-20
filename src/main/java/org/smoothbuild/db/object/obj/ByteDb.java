package org.smoothbuild.db.object.obj;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootExc.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.hashed.exc.NoSuchDataExc;
import org.smoothbuild.db.object.db.ByteDbExc;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.exc.DecodeObjCatExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjNoSuchObjExc;
import org.smoothbuild.db.object.obj.expr.CallB;
import org.smoothbuild.db.object.obj.expr.CombineB;
import org.smoothbuild.db.object.obj.expr.IfB;
import org.smoothbuild.db.object.obj.expr.InvokeB;
import org.smoothbuild.db.object.obj.expr.MapB;
import org.smoothbuild.db.object.obj.expr.OrderB;
import org.smoothbuild.db.object.obj.expr.ParamRefB;
import org.smoothbuild.db.object.obj.expr.SelectB;
import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.obj.val.ArrayBBuilder;
import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.obj.val.BlobBBuilder;
import org.smoothbuild.db.object.obj.val.BoolB;
import org.smoothbuild.db.object.obj.val.FuncB;
import org.smoothbuild.db.object.obj.val.IntB;
import org.smoothbuild.db.object.obj.val.MethodB;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.db.object.obj.val.TupleB;
import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.db.object.type.CatDb;
import org.smoothbuild.db.object.type.TypingB;
import org.smoothbuild.db.object.type.base.CatB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.db.object.type.val.ArrayTB;
import org.smoothbuild.db.object.type.val.CallableTB;
import org.smoothbuild.db.object.type.val.FuncTB;
import org.smoothbuild.db.object.type.val.MethodTB;
import org.smoothbuild.db.object.type.val.TupleTB;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ByteDb {
  private final HashedDb hashedDb;
  private final CatDb catDb;
  private final TypingB typing;

  public ByteDb(HashedDb hashedDb, CatDb catDb, TypingB typing) {
    this.hashedDb = hashedDb;
    this.catDb = catDb;
    this.typing = typing;
  }

  // methods for creating ValueH subclasses

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return new ArrayBBuilder(type, this);
  }

  public BlobBBuilder blobBuilder() {
    return wrapHashedDbExceptionAsObjectDbException(() -> new BlobBBuilder(this, hashedDb.sink()));
  }

  public BoolB bool(boolean value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newBool(value));
  }

  public MethodB method(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newMethod(type, jar, classBinaryName, isPure));
  }

  public FuncB func(FuncTB type, ObjB body) {
    checkBodyTypeAssignableToFuncResT(type, body);
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunc(type, body));
  }

  private void checkBodyTypeAssignableToFuncResT(FuncTB type, ObjB body) {
    if (!typing.isAssignable(type.res(), body.type())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.res().q()
          + " but body.type() is " + body.type().q() + ".");
    }
  }

  public IntB int_(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInt(value));
  }

  public StringB string(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newString(value));
  }

  public TupleB tuple(TupleTB tupleT, ImmutableList<ValB> items) {
    var types = tupleT.items();
    allMatchOtherwise(types, items, (s, i) -> Objects.equals(s, i.cat()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "tupleType specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("tupleType specifies item at index " + i
              + " with type " + types.get(i).name() + " but provided item has type "
              + items.get(i).cat().name() + " at that index.");
        }
    );

    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(tupleT, items));
  }

  // methods for creating ExprH subclasses

  public CallB call(ObjB callable, CombineB args) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(callable, args));
  }

  public CallB call(TypeB evalT, ObjB callable, CombineB args) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(evalT, callable, args));
  }

  public CombineB combine(TupleTB evalT, ImmutableList<ObjB> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCombine(evalT, items));
  }

  public IfB if_(ObjB condition, ObjB then, ObjB else_) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newIf(condition, then, else_));
  }

  public InvokeB invoke(ObjB method, CombineB args) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInvoke(method, args));
  }

  public InvokeB invoke(TypeB evalT, ObjB method, CombineB args) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInvoke(evalT, method, args));
  }

  public MapB map(ObjB array, ObjB func) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newMap(array, func));
  }

  public OrderB order(ArrayTB arrayTB, ImmutableList<ObjB> elems) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(arrayTB, elems));
  }

  public ParamRefB newParamRef(BigInteger value, TypeB evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newParamRef(evalT, value));
  }

  public SelectB select(ObjB selectable, IntB index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(selectable, index));
  }

  public SelectB select(TypeB evalT, ObjB selectable, IntB index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(evalT, selectable, index));
  }

  // generic getter

  public ObjB get(Hash rootHash) {
    List<Hash> hashes = decodeRootSeq(rootHash);
    if (hashes.size() != 2) {
      throw wrongSizeOfRootSeqException(rootHash, hashes.size());
    }
    CatB cat = getCatOrChainException(rootHash, hashes.get(0));
    Hash dataHash = hashes.get(1);
    return cat.newObj(new MerkleRoot(rootHash, cat, dataHash), this);
  }

  private CatB getCatOrChainException(Hash rootHash, Hash typeHash) {
    try {
      return catDb.get(typeHash);
    } catch (ByteDbExc e) {
      throw new DecodeObjCatExc(rootHash, e);
    }
  }

  private List<Hash> decodeRootSeq(Hash rootHash) {
    try {
      return hashedDb.readSeq(rootHash);
    } catch (NoSuchDataExc e) {
      throw new DecodeObjNoSuchObjExc(rootHash, e);
    } catch (HashedDbExc e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  // methods for creating Val Obj-s

  public ArrayB newArray(ArrayTB type, List<ValB> elems) throws HashedDbExc {
    var data = writeArrayData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  public BlobB newBlob(Hash dataHash) throws HashedDbExc {
    var root = newRoot(catDb.blob(), dataHash);
    return catDb.blob().newObj(root, this);
  }

  private BoolB newBool(boolean value) throws HashedDbExc {
    var data = writeBoolData(value);
    var root = newRoot(catDb.bool(), data);
    return catDb.bool().newObj(root, this);
  }

  private FuncB newFunc(FuncTB type, ObjB body) throws HashedDbExc {
    var data = writeFuncData(body);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private IntB newInt(BigInteger value) throws HashedDbExc {
    var data = writeIntData(value);
    var root = newRoot(catDb.int_(), data);
    return catDb.int_().newObj(root, this);
  }

  private MethodB newMethod(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws HashedDbExc {
    var data = writeMethodData(jar, classBinaryName, isPure);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private StringB newString(String string) throws HashedDbExc {
    var data = writeStringData(string);
    var root = newRoot(catDb.string(), data);
    return catDb.string().newObj(root, this);
  }

  private TupleB newTuple(TupleTB type, ImmutableList<ValB> vals) throws HashedDbExc {
    var data = writeTupleData(vals);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  // methods for creating Expr-s

  private CallB newCall(ObjB callable, CombineB args) throws HashedDbExc {
    var resT = inferCallResT(castTypeToFuncTH(callable), args);
    return newCallImpl(resT, callable, args);
  }

  private CallB newCall(TypeB evalT, ObjB callable, CombineB args) throws HashedDbExc {
    var resT = inferCallResT(castTypeToFuncTH(callable), args);
    if (!typing.isAssignable(evalT, resT)) {
      throw new IllegalArgumentException(
          "Call's result type " + resT.q() + " cannot be assigned to evalT " + evalT.q() + ".");
    }
    return newCallImpl(resT, callable, args);
  }

  private CallB newCallImpl(TypeB evalT, ObjB callable, CombineB args) throws HashedDbExc {
    var type = catDb.call(evalT);
    var data = writeCallData(callable, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private FuncTB castTypeToFuncTH(ObjB callable) {
    if (callable.type() instanceof FuncTB funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to FuncH.");
    }
  }

  private TypeB inferCallResT(CallableTB callableTB, ObjB args) {
    var argsT = castTypeToTupleTH(args);
    return inferCallResT(callableTB, argsT, () -> illegalArgs(callableTB, argsT));
  }

  public TypeB inferCallResT(CallableTB callableT, TupleTB argsT, Runnable illegalArgsExcThrower) {
    var argTs = argsT.items();
    var paramTs = callableT.params();
    allMatchOtherwise(
        paramTs,
        argTs,
        typing::isParamAssignable,
        (expectedSize, actualSize) -> illegalArgsExcThrower.run(),
        i -> illegalArgsExcThrower.run()
    );
    var varBounds = typing.inferVarBoundsLower(paramTs, argTs);
    return typing.mapVarsLower(callableT.res(), varBounds);
  }

  private TupleTB castTypeToTupleTH(ObjB args) {
    if (args.type() instanceof TupleTB tupleT) {
      return tupleT;
    } else {
      throw new IllegalArgumentException("`args` component doesn't evaluate to TupleH.");
    }
  }

  private void illegalArgs(CallableTB callableTB, TupleTB argsT) {
    throw new IllegalArgumentException(
        "Arguments evaluation type %s should be equal to callable type parameters %s."
            .formatted(argsT.name(), callableTB.paramsTuple().name()));
  }

  private OrderB newOrder(ArrayTB arrayTB, ImmutableList<ObjB> elems) throws HashedDbExc {
    var elemT = arrayTB.elem();
    validateOrderElems(elemT, elems);
    var type = catDb.order(elemT);
    var data = writeOrderData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private void validateOrderElems(TypeB elemT, ImmutableList<ObjB> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElemT = elems.get(i).type();
      if (!typing.isAssignable(elemT, iElemT)) {
        throw new IllegalArgumentException("Illegal elem type. Expected " + elemT.q()
            + " but element at index " + i + " has type " + iElemT.q() + ".");
      }
    }
  }

  private CombineB newCombine(TupleTB evalT, ImmutableList<ObjB> items) throws HashedDbExc {
    validateCombineItems(evalT, items);
    var type = catDb.combine(evalT);
    var data = writeCombineData(items);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private void validateCombineItems(TupleTB evalT, ImmutableList<ObjB> items) {
    var expectedItemTs = evalT.items();
    for (int i = 0; i < items.size(); i++) {
      var expectedItemT = expectedItemTs.get(i);
      var actualItemT = items.get(i).type();
      if (!typing.isAssignable(expectedItemT, actualItemT)) {
        throw new IllegalArgumentException("Illegal item type. Expected " + expectedItemT.q()
            + " at index " + i + " has type " + actualItemT.q() + ".");
      }
    }
  }

  private IfB newIf(ObjB condition, ObjB then, ObjB else_) throws HashedDbExc {
    checkArgument(condition.type().equals(catDb.bool()),
        "`condition` component must evaluate to BoolH but is " + condition.type().q() + ".");
    var evalT = typing.mergeUp(then.type(), else_.type());
    var type = catDb.if_(evalT);
    var data = writeIfData(condition, then, else_);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private InvokeB newInvoke(ObjB method, CombineB args) throws HashedDbExc {
    var resT = inferCallResT(castTypeToMethodTH(method), args);
    return newInvokeImpl(resT, method, args);
  }

  private InvokeB newInvoke(TypeB evalT, ObjB method, CombineB args) throws HashedDbExc {
    var resT = inferCallResT(castTypeToMethodTH(method), args);
    if (!typing.isAssignable(evalT, resT)) {
      throw new IllegalArgumentException(
          "Method's result type " + resT.q() + " cannot be assigned to evalT " + evalT.q() + ".");
    }
    return newInvokeImpl(resT, method, args);
  }

  private InvokeB newInvokeImpl(TypeB evalT, ObjB method, CombineB args) throws HashedDbExc {
    var type = catDb.invoke(evalT);
    var data = writeInvokeData(method, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MethodTB castTypeToMethodTH(ObjB callable) {
    if (callable.type() instanceof MethodTB methodTB) {
      return methodTB;
    } else {
      throw new IllegalArgumentException("`method` component doesn't evaluate to MethodH.");
    }
  }

  private MapB newMap(ObjB array, ObjB func) throws HashedDbExc {
    if (array.type() instanceof ArrayTB arrayT
        && func.type() instanceof FuncTB funcT
        && funcT.params().size() == 1
        && typing.isAssignable(funcT.params().get(0), arrayT.elem())) {
      // TODO func can be generic so we need to infer proper result type
      var vars = typing.inferVarBoundsLower(funcT.params(), list(arrayT.elem()));
      var elemEvalT = typing.mapVarsLower(funcT.res(), vars);
      var evalT = catDb.array(elemEvalT);
      var type = catDb.map(evalT);

      var data = writeMapData(array, func);
      var root = newRoot(type, data);
      return type.newObj(root, this);
    } else {
      // TODO add more info
      throw new IllegalArgumentException();
    }
  }

  private SelectB newSelect(ObjB selectable, IntB index) throws HashedDbExc {
    var evalT = selectEvalT(selectable, index);
    return newSelectImpl(evalT, selectable, index);
  }

  private SelectB newSelect(TypeB evalT, ObjB selectable, IntB index) throws HashedDbExc {
    var inferredEvalT = selectEvalT(selectable, index);
    if (!typing.isAssignable(evalT, inferredEvalT)) {
      throw new IllegalArgumentException("Selected item type " + inferredEvalT.q()
          + " cannot be assigned to evalT " + evalT.q() + ".");
    }
    return newSelectImpl(inferredEvalT, selectable, index);
  }

  private SelectB newSelectImpl(TypeB evalT, ObjB selectable, IntB index) throws HashedDbExc {
    var data = writeSelectData(selectable, index);
    var cat = catDb.select(evalT);
    var root = newRoot(cat, data);
    return cat.newObj(root, this);
  }

  private TypeB selectEvalT(ObjB selectable, IntB index) {
    var evalT = selectable.type();
    if (evalT instanceof TupleTB tuple) {
      int intIndex = index.toJ().intValue();
      var items = tuple.items();
      checkElementIndex(intIndex, items.size());
      return items.get(intIndex);
    } else {
      throw new IllegalArgumentException(
          "Selectable.type() should be instance of TupleTB but is " + evalT.q());
    }
  }

  private ParamRefB newParamRef(TypeB evalT, BigInteger index) throws HashedDbExc {
    var data = writeParamRefData(index);
    var type = catDb.ref(evalT);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MerkleRoot newRoot(CatB cat, Hash dataHash) throws HashedDbExc {
    Hash rootHash = hashedDb.writeSeq(cat.hash(), dataHash);
    return new MerkleRoot(rootHash, cat, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ObjB callable, CombineB args) throws HashedDbExc {
    return hashedDb.writeSeq(callable.hash(), args.hash());
  }

  private Hash writeCombineData(ImmutableList<ObjB> items) throws HashedDbExc {
    return writeSeq(items);
  }

  private Hash writeIfData(ObjB condition, ObjB then, ObjB else_) throws HashedDbExc {
    return hashedDb.writeSeq(condition.hash(), then.hash(), else_.hash());
  }

  private Hash writeInvokeData(ObjB method, CombineB args) throws HashedDbExc {
    return hashedDb.writeSeq(method.hash(), args.hash());
  }

  private Hash writeMapData(ObjB array, ObjB func) throws HashedDbExc {
    return hashedDb.writeSeq(array.hash(), func.hash());
  }

  private Hash writeOrderData(ImmutableList<ObjB> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeParamRefData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeSelectData(ObjB selectable, IntB index) throws HashedDbExc {
    return hashedDb.writeSeq(selectable.hash(), index.hash());
  }

  // methods for writing data of Val-s

  private Hash writeArrayData(List<ValB> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeBoolData(boolean value) throws HashedDbExc {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeFuncData(ObjB body) {
    return body.hash();
  }

  private Hash writeIntData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeMethodData(BlobB jar, StringB classBinaryName, BoolB isPure)
      throws HashedDbExc {
    return hashedDb.writeSeq(jar.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeStringData(String string) throws HashedDbExc {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(ImmutableList<ValB> items) throws HashedDbExc {
    return writeSeq(items);
  }

  // helpers

  private Hash writeSeq(List<? extends ObjB> objs) throws HashedDbExc {
    var hashes = Lists.map(objs, ObjB::hash);
    return hashedDb.writeSeq(hashes);
  }

  public ImmutableList<Hash> readSeq(Hash hash) throws HashedDbExc {
    return hashedDb.readSeq(hash);
  }

  // TODO visible for classes from db.object package tree until creating Obj is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }

  public CatDb catDb() {
    return catDb;
  }

  public Typing<TypeB> typing() {
    return typing;
  }
}
