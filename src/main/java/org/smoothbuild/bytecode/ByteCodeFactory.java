package org.smoothbuild.bytecode;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.ObjDb;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.exc.ByteDbExc;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.obj.expr.InvokeB;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.obj.val.ArrayBBuilder;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.BlobBBuilder;
import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.BlobTB;
import org.smoothbuild.bytecode.type.val.BoolTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IntTB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.StringTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.VarTB;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ByteCodeFactory {
  private final ObjDb objDb;
  private final CatDb catDb;
  private final TupleTB messageT;
  private final TupleTB fileT;
  private final TypingB typing;

  @Inject
  public ByteCodeFactory(ObjDb objDb, CatDb catDb, TypingB typing) {
    this.objDb = objDb;
    this.catDb = catDb;
    this.messageT = createMessageT(catDb);
    this.fileT = createFileT(catDb);
    this.typing = typing;
  }

  public TypingB typing() {
    return typing;
  }

  // Objects

  public ArrayBBuilder arrayBuilderWithElems(TypeB elemT) {
    return objDb.arrayBuilder(catDb.array(elemT));
  }

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return objDb.arrayBuilder(type);
  }

  public BlobB blob(DataWriter dataWriter) {
    try (BlobBBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new ByteDbExc(e);
    }
  }

  public BlobBBuilder blobBuilder() {
    return objDb.blobBuilder();
  }

  public BoolB bool(boolean value) {
    return objDb.bool(value);
  }

  public CallB call(TypeB evalT, ObjB func, CombineB args) {
    return objDb.call(evalT, func, args);
  }

  public CombineB combine(TupleTB evalT, ImmutableList<ObjB> items) {
    return objDb.combine(evalT, items);
  }

  public TupleB file(StringB path, BlobB content) {
    return objDb.tuple(fileT(), list(content, path));
  }

  public IfB if_(ObjB condition, ObjB then, ObjB else_) {
    return objDb.if_(condition, then, else_);
  }

  public FuncB func(FuncTB type, ObjB body) {
    return objDb.func(type, body);
  }

  public MethodB method(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return objDb.method(type, jar, classBinaryName, isPure);
  }

  public IntB int_(BigInteger value) {
    return objDb.int_(value);
  }

  public MapB map(ObjB array, ObjB func) {
    return objDb.map(array, func);
  }

  public InvokeB invoke(TypeB evalT, ObjB method, CombineB args) {
    return objDb.invoke(evalT, method, args);
  }

  public ParamRefB paramRef(TypeB evalT, BigInteger value) {
    return objDb.paramRef(evalT, value);
  }

  public SelectB select(TypeB evalT, ObjB tuple, IntB index) {
    return objDb.select(evalT, tuple, index);
  }

  public StringB string(String string) {
    return objDb.string(string);
  }

  public TupleB tuple(TupleTB type, ImmutableList<ValB> items) {
    return objDb.tuple(type, items);
  }

  public OrderB order(ArrayTB arrayTB, ImmutableList<ObjB> elems) {
    return objDb.order(arrayTB, elems);
  }

  // Types

  public ArrayTB arrayT(TypeB elemT) {
    return catDb.array(elemT);
  }

  public BlobTB blobT() {
    return catDb.blob();
  }

  public BoolTB boolT() {
    return catDb.bool();
  }

  public FuncTB funcT(TypeB resT, ImmutableList<TypeB> paramTs) {
    return catDb.func(resT, paramTs);
  }

  public IntTB intT() {
    return catDb.int_();
  }

  public TupleTB messageT() {
    return messageT;
  }

  public MethodTB methodT(TypeB resT, ImmutableList<TypeB> paramTs) {
    return catDb.method(resT, paramTs);
  }

  public NothingTB nothingT() {
    return catDb.nothing();
  }

  public StringTB stringT() {
    return catDb.string();
  }

  public TupleTB tupleT(ImmutableList<TypeB> itemTs) {
    return catDb.tuple(itemTs);
  }

  public VarTB varT(String name) {
    return catDb.var(name);
  }

  // other values and its types

  public TupleTB fileT() {
    return fileT;
  }

  public TupleB errorMessage(String text) {
    return message(ERROR.name(), text);
  }

  public TupleB warningMessage(String text) {
    return message(WARNING.name(), text);
  }

  public TupleB infoMessage(String text) {
    return message(INFO.name(), text);
  }

  private TupleB message(String severity, String text) {
    ValB textObject = objDb.string(text);
    ValB severityObject = objDb.string(severity);
    return objDb.tuple(messageT(), list(textObject, severityObject));
  }

  private static TupleTB createMessageT(CatDb catDb) {
    var stringT = catDb.string();
    return catDb.tuple(list(stringT, stringT));
  }

  private static TupleTB createFileT(CatDb catDb) {
    return catDb.tuple(list(catDb.blob(), catDb.string()));
  }
}
