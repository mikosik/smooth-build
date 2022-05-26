package org.smoothbuild.bytecode;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.ObjDb;
import org.smoothbuild.bytecode.obj.base.ObjB;
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
import org.smoothbuild.bytecode.obj.exc.ObjDbExc;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.obj.expr.InvokeB;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.BlobTB;
import org.smoothbuild.bytecode.type.cnst.BoolTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.IntTB;
import org.smoothbuild.bytecode.type.cnst.MethodTB;
import org.smoothbuild.bytecode.type.cnst.NothingTB;
import org.smoothbuild.bytecode.type.cnst.StringTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.bytecode.type.cnst.VarB;
import org.smoothbuild.util.collect.Lists;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class BytecodeF {
  private final ObjDb objDb;
  private final CatDb catDb;
  private final TupleTB messageT;
  private final TupleTB fileT;

  @Inject
  public BytecodeF(ObjDb objDb, CatDb catDb) {
    this.objDb = objDb;
    this.catDb = catDb;
    this.messageT = createMessageT(catDb);
    this.fileT = createFileT(catDb);
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
      throw new ObjDbExc(e);
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
    return objDb.tuple(fileT(), list(path, content));
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

  public TupleB tuple(ImmutableList<CnstB> items) {
    var tupleTB = catDb.tuple(Lists.map(items, CnstB::type));
    return objDb.tuple(tupleTB, items);
  }

  public TupleB tuple(TupleTB type, ImmutableList<CnstB> items) {
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

  public VarB varT(String name) {
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
    CnstB textObject = objDb.string(text);
    CnstB severityObject = objDb.string(severity);
    return objDb.tuple(messageT(), list(textObject, severityObject));
  }

  private static TupleTB createMessageT(CatDb catDb) {
    var stringT = catDb.string();
    return catDb.tuple(list(stringT, stringT));
  }

  private static TupleTB createFileT(CatDb catDb) {
    return catDb.tuple(list(catDb.string(), catDb.blob()));
  }
}
