package org.smoothbuild.bytecode;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.exc.BytecodeDbExc;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ArrayBBuilder;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BlobBBuilder;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.DefFuncB;
import org.smoothbuild.bytecode.expr.val.IfFuncB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.MapFuncB;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.BlobTB;
import org.smoothbuild.bytecode.type.val.BoolTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IntTB;
import org.smoothbuild.bytecode.type.val.StringTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.util.collect.Lists;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class BytecodeF {
  private final BytecodeDb bytecodeDb;
  private final CatDb catDb;
  private final TupleTB messageT;
  private final TupleTB fileT;

  @Inject
  public BytecodeF(BytecodeDb bytecodeDb, CatDb catDb) {
    this.bytecodeDb = bytecodeDb;
    this.catDb = catDb;
    this.messageT = createMessageT(catDb);
    this.fileT = createFileT(catDb);
  }

  // Objects

  public ArrayBBuilder arrayBuilderWithElems(TypeB elemT) {
    return bytecodeDb.arrayBuilder(catDb.array(elemT));
  }

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return bytecodeDb.arrayBuilder(type);
  }

  public BlobB blob(DataWriter dataWriter) {
    try (BlobBBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new BytecodeDbExc(e);
    }
  }

  public BlobBBuilder blobBuilder() {
    return bytecodeDb.blobBuilder();
  }

  public BoolB bool(boolean value) {
    return bytecodeDb.bool(value);
  }

  public CallB call(TypeB evalT, ExprB func, CombineB args) {
    return bytecodeDb.call(evalT, func, args);
  }

  public CombineB combine(TupleTB evalT, ImmutableList<ExprB> items) {
    return bytecodeDb.combine(evalT, items);
  }

  public TupleB file(StringB path, BlobB content) {
    return bytecodeDb.tuple(fileT(), list(path, content));
  }

  public DefFuncB defFunc(TypeB resT, ImmutableList<TypeB> paramTs, ExprB body) {
    var type = catDb.funcT(resT, paramTs);
    return defFunc(type, body);
  }

  public DefFuncB defFunc(TypeB resT, TupleTB paramTs, ExprB body) {
    var type = catDb.funcT(resT, paramTs);
    return defFunc(type, body);
  }

  public DefFuncB defFunc(FuncTB type, ExprB body) {
    return bytecodeDb.defFunc(type, body);
  }

  public NatFuncB natFunc(FuncTB funcTB, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return bytecodeDb.natFunc(funcTB, jar, classBinaryName, isPure);
  }

  public IfFuncB ifFunc(TypeB t) {
    return bytecodeDb.ifFunc(t);
  }

  public IntB int_(BigInteger value) {
    return bytecodeDb.int_(value);
  }

  public MapFuncB mapFunc(TypeB r, TypeB s) {
    return bytecodeDb.mapFunc(r, s);
  }

  public ParamRefB paramRef(TypeB evalT, BigInteger value) {
    return bytecodeDb.paramRef(evalT, value);
  }

  public SelectB select(TypeB evalT, ExprB tuple, IntB index) {
    return bytecodeDb.select(evalT, tuple, index);
  }

  public StringB string(String string) {
    return bytecodeDb.string(string);
  }

  public TupleB tuple(ImmutableList<ValB> items) {
    var tupleTB = catDb.tuple(Lists.map(items, ValB::type));
    return bytecodeDb.tuple(tupleTB, items);
  }

  public TupleB tuple(TupleTB type, ImmutableList<ValB> items) {
    return bytecodeDb.tuple(type, items);
  }

  public OrderB order(ArrayTB arrayTB, ImmutableList<ExprB> elems) {
    return bytecodeDb.order(arrayTB, elems);
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
    return catDb.funcT(resT, paramTs);
  }

  public FuncTB funcT(TypeB resT, TupleTB paramTs) {
    return catDb.funcT(resT, paramTs);
  }

  public IntTB intT() {
    return catDb.int_();
  }

  public TupleTB messageT() {
    return messageT;
  }

  public StringTB stringT() {
    return catDb.string();
  }

  public TupleTB tupleT(TypeB... itemTs) {
    return catDb.tuple(itemTs);
  }

  public TupleTB tupleT(ImmutableList<TypeB> itemTs) {
    return catDb.tuple(itemTs);
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
    ValB textObject = bytecodeDb.string(text);
    ValB severityObject = bytecodeDb.string(severity);
    return bytecodeDb.tuple(messageT(), list(textObject, severityObject));
  }

  private static TupleTB createMessageT(CatDb catDb) {
    var stringT = catDb.string();
    return catDb.tuple(stringT, stringT);
  }

  private static TupleTB createFileT(CatDb catDb) {
    return catDb.tuple(catDb.string(), catDb.blob());
  }
}
