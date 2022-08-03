package org.smoothbuild.bytecode.obj;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.bytecode.obj.Helpers.wrapHashedDbExcAsObjDbExc;
import static org.smoothbuild.bytecode.obj.exc.DecodeObjRootExc.cannotReadRootException;
import static org.smoothbuild.bytecode.obj.exc.DecodeObjRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.bytecode.type.ValidateArgs.validateArgs;
import static org.smoothbuild.bytecode.type.cnst.TNamesB.BOOL;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.ArrayBBuilder;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.bytecode.obj.cnst.BlobBBuilder;
import org.smoothbuild.bytecode.obj.cnst.BoolB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.FuncB;
import org.smoothbuild.bytecode.obj.cnst.IntB;
import org.smoothbuild.bytecode.obj.cnst.MethodB;
import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.obj.exc.DecodeObjCatExc;
import org.smoothbuild.bytecode.obj.exc.DecodeObjNoSuchObjExc;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.obj.expr.InvokeB;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.CallableTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.MethodTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.bytecode.type.exc.CatDbExc;
import org.smoothbuild.db.Hash;
import org.smoothbuild.db.HashedDb;
import org.smoothbuild.db.exc.HashedDbExc;
import org.smoothbuild.db.exc.NoSuchDataExc;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjDbImpl implements ObjDb {
  private final HashedDb hashedDb;
  private final CatDb catDb;

  public ObjDbImpl(HashedDb hashedDb, CatDb catDb) {
    this.hashedDb = hashedDb;
    this.catDb = catDb;
  }

  // methods for creating CnstB subclasses

  @Override
  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return new ArrayBBuilder(type, this);
  }

  @Override
  public BlobBBuilder blobBuilder() {
    return wrapHashedDbExcAsObjDbExc(() -> new BlobBBuilder(this, hashedDb.sink()));
  }

  @Override
  public BoolB bool(boolean value) {
    return wrapHashedDbExcAsObjDbExc(() -> newBool(value));
  }

  @Override
  public MethodB method(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return wrapHashedDbExcAsObjDbExc(
        () -> newMethod(type, jar, classBinaryName, isPure));
  }

  @Override
  public FuncB func(FuncTB type, ObjB body) {
    checkBodyTypeAssignableToFuncResT(type, body);
    return wrapHashedDbExcAsObjDbExc(() -> newFunc(type, body));
  }

  private void checkBodyTypeAssignableToFuncResT(FuncTB type, ObjB body) {
    if (!type.res().equals(body.type())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.res().q()
          + " but body.type() is " + body.type().q() + ".");
    }
  }

  @Override
  public IntB int_(BigInteger value) {
    return wrapHashedDbExcAsObjDbExc(() -> newInt(value));
  }

  @Override
  public StringB string(String value) {
    return wrapHashedDbExcAsObjDbExc(() -> newString(value));
  }

  @Override
  public TupleB tuple(TupleTB tupleT, ImmutableList<CnstB> items) {
    var itemTs = tupleT.items();
    allMatchOtherwise(itemTs, items, (s, i) -> Objects.equals(s, i.cat()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "tupleType specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("tupleType specifies item at index " + i
              + " with type " + itemTs.get(i).name() + " but provided item has type "
              + items.get(i).cat().name() + " at that index.");
        }
    );

    return wrapHashedDbExcAsObjDbExc(() -> newTuple(tupleT, items));
  }

  // methods for creating ExprB subclasses

  @Override
  public CallB call(TypeB evalT, ObjB func, CombineB args) {
    return wrapHashedDbExcAsObjDbExc(() -> newCall(evalT, func, args));
  }

  @Override
  public CombineB combine(TupleTB evalT, ImmutableList<ObjB> items) {
    return wrapHashedDbExcAsObjDbExc(() -> newCombine(evalT, items));
  }

  @Override
  public IfB if_(TypeB evalT, ObjB condition, ObjB then, ObjB else_) {
    return wrapHashedDbExcAsObjDbExc(() -> newIf(evalT, condition, then, else_));
  }

  @Override
  public InvokeB invoke(TypeB evalT, ObjB method, CombineB args) {
    return wrapHashedDbExcAsObjDbExc(() -> newInvoke(evalT, method, args));
  }

  @Override
  public MapB map(ObjB array, ObjB func) {
    return wrapHashedDbExcAsObjDbExc(() -> newMap(array, func));
  }

  @Override
  public OrderB order(ArrayTB evalT, ImmutableList<ObjB> elems) {
    return wrapHashedDbExcAsObjDbExc(() -> newOrder(evalT, elems));
  }

  @Override
  public ParamRefB paramRef(TypeB evalT, BigInteger value) {
    return wrapHashedDbExcAsObjDbExc(() -> newParamRef(evalT, value));
  }

  @Override
  public SelectB select(TypeB evalT, ObjB selectable, IntB index) {
    return wrapHashedDbExcAsObjDbExc(() -> newSelect(evalT, selectable, index));
  }

  // generic getter

  @Override
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
    } catch (CatDbExc e) {
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

  // methods for creating Cnst-s

  public ArrayB newArray(ArrayTB type, List<CnstB> elems) throws HashedDbExc {
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

  private TupleB newTuple(TupleTB type, ImmutableList<CnstB> cnsts) throws HashedDbExc {
    var data = writeTupleData(cnsts);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  // methods for creating Expr-s

  private CallB newCall(TypeB evalT, ObjB func, CombineB args) throws HashedDbExc {
    CallableTB callableTB = castTypeToFuncTB(func);
    validateArgsInCall(callableTB, args);
    var resT = callableTB.res();
    if (!evalT.equals(resT)) {
      throw new IllegalArgumentException(
          "Call's result type " + resT.q() + " cannot be assigned to evalT " + evalT.q() + ".");
    }

    var type = catDb.call(evalT);
    var data = writeCallData(func, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private FuncTB castTypeToFuncTB(ObjB callable) {
    if (callable.type() instanceof FuncTB funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to FuncB.");
    }
  }

  private void validateArgsInCall(CallableTB callableTB, CombineB args) {
    validateArgs(callableTB, args.type().items(),
        () -> {throw illegalArgs(callableTB, args.type());}
    );
  }

  private IllegalArgumentException illegalArgs(CallableTB callableTB, TupleTB argsT) {
    return new IllegalArgumentException(
        "Argument evaluation types %s should be equal to callable parameter types %s."
            .formatted(itemTsToString(argsT), itemTsToString(callableTB.paramsTuple())));
  }

  private static String itemTsToString(TupleTB argsT) {
    return "(" + toCommaSeparatedString(argsT.items()) + ")";
  }

  private OrderB newOrder(ArrayTB evalT, ImmutableList<ObjB> elems) throws HashedDbExc {
    validateOrderElems(evalT.elem(), elems);
    var type = catDb.order(evalT);
    var data = writeOrderData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private void validateOrderElems(TypeB elemT, ImmutableList<ObjB> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElemT = elems.get(i).type();
      if (!elemT.equals(iElemT)) {
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
    if (expectedItemTs.size() != items.size()) {
      throw new IllegalArgumentException(
          "Expected " + expectedItemTs.size() + " items, got " + items.size() + ".");
    }
    for (int i = 0; i < items.size(); i++) {
      var expectedItemT = expectedItemTs.get(i);
      var actualItemT = items.get(i).type();
      if (!expectedItemT.equals(actualItemT)) {
        throw new IllegalArgumentException("Illegal item type. Expected " + expectedItemT.q()
            + " at index " + i + " has type " + actualItemT.q() + ".");
      }
    }
  }

  private IfB newIf(TypeB evalT, ObjB condition, ObjB then, ObjB else_) throws HashedDbExc {
    checkArgument(condition.type().equals(catDb.bool()),
        "`condition` component must evaluate to " + BOOL + " but is " + condition.type().q() + ".");
    checkArgument(evalT.equals(then.type()),
        "`then` component must evaluate to " + evalT.q() + " but is " + then.type().q() + ".");
    checkArgument(evalT.equals(else_.type()),
        "`else` component must evaluate to " + evalT.q() + " but is " + else_.type().q() + ".");
    var type = catDb.if_(evalT);
    var data = writeIfData(condition, then, else_);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private InvokeB newInvoke(TypeB evalT, ObjB method, CombineB args) throws HashedDbExc {
    CallableTB callableTB = castTypeToMethodTB(method);
    validateArgsInCall(callableTB, args);
    var resT = callableTB.res();
    if (!evalT.equals(resT)) {
      throw new IllegalArgumentException(
          "Method's result type " + resT.q() + " cannot be assigned to evalT " + evalT.q() + ".");
    }
    var type = catDb.invoke(evalT);
    var data = writeInvokeData(method, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MethodTB castTypeToMethodTB(ObjB method) {
    if (method.type() instanceof MethodTB methodTB) {
      return methodTB;
    } else {
      throw new IllegalArgumentException("`method` component doesn't evaluate to MethodB.");
    }
  }

  private MapB newMap(ObjB array, ObjB func) throws HashedDbExc {
    if (!(array.type() instanceof ArrayTB arrayT)) {
      throw new IllegalArgumentException("array.type() must be instance of "
          + ArrayTB.class.getSimpleName() + " but is "
          + array.type().getClass().getSimpleName() + ".");
    }
    if (!(func.type() instanceof FuncTB funcT)) {
      throw new IllegalArgumentException("func.type() must be instance of "
          + FuncTB.class.getSimpleName() + " but is "
          + func.type().getClass().getSimpleName() + ".");
    }
    if (funcT.params().size() != 1) {
      throw new IllegalArgumentException(
          "func parameter count must be 1 but is " + funcT.params().size() + ".");
    }
    var elemT = arrayT.elem();
    var funcParamT = funcT.params().get(0);
    if (!funcParamT.equals(elemT)) {
      throw new IllegalArgumentException("array element type " + elemT
          + " is not assignable to func parameter type " + funcParamT + ".");
    }
    var evalT = catDb.array(funcT.res());
    var type = catDb.map(evalT);

    var data = writeMapData(array, func);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private SelectB newSelect(TypeB evalT, ObjB selectable, IntB index) throws HashedDbExc {
    var inferredEvalT = selectEvalT(selectable, index);
    if (!evalT.equals(inferredEvalT)) {
      throw new IllegalArgumentException("Selected item type " + inferredEvalT.q()
          + " cannot be assigned to evalT " + evalT.q() + ".");
    }
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
    var type = catDb.paramRef(evalT);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MerkleRoot newRoot(CatB cat, Hash dataHash) throws HashedDbExc {
    Hash rootHash = hashedDb.writeSeq(cat.hash(), dataHash);
    return new MerkleRoot(rootHash, cat, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ObjB func, CombineB args) throws HashedDbExc {
    return hashedDb.writeSeq(func.hash(), args.hash());
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

  // methods for writing data of Cnst-s

  private Hash writeArrayData(List<CnstB> elems) throws HashedDbExc {
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

  private Hash writeTupleData(ImmutableList<CnstB> items) throws HashedDbExc {
    return writeSeq(items);
  }

  // helpers

  private Hash writeSeq(List<? extends ObjB> objs) throws HashedDbExc {
    var hashes = Lists.map(objs, ObjB::hash);
    return hashedDb.writeSeq(hashes);
  }

  // TODO visible for classes from db.object package tree until creating Obj is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }

  public CatDb catDb() {
    return catDb;
  }
}
