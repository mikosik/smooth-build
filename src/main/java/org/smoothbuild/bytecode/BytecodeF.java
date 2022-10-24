package org.smoothbuild.bytecode;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
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
import org.smoothbuild.bytecode.expr.inst.ArrayBBuilder;
import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.BlobBBuilder;
import org.smoothbuild.bytecode.expr.inst.BoolB;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.bytecode.expr.inst.IfFuncB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.MapFuncB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.type.CategoryDb;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.bytecode.type.inst.BlobTB;
import org.smoothbuild.bytecode.type.inst.BoolTB;
import org.smoothbuild.bytecode.type.inst.FuncTB;
import org.smoothbuild.bytecode.type.inst.IntTB;
import org.smoothbuild.bytecode.type.inst.StringTB;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class BytecodeF {
  private final BytecodeDb bytecodeDb;
  private final CategoryDb categoryDb;
  private final TupleTB messageT;
  private final TupleTB fileT;

  @Inject
  public BytecodeF(BytecodeDb bytecodeDb, CategoryDb categoryDb) {
    this.bytecodeDb = bytecodeDb;
    this.categoryDb = categoryDb;
    this.messageT = createMessageT(categoryDb);
    this.fileT = createFileT(categoryDb);
  }

  // Objects

  public ArrayBBuilder arrayBuilderWithElems(TypeB elemT) {
    return bytecodeDb.arrayBuilder(categoryDb.array(elemT));
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

  public CallB call(ExprB func, CombineB args) {
    return bytecodeDb.call(func, args);
  }

  public CombineB combine(ImmutableList<ExprB> items) {
    return bytecodeDb.combine(items);
  }

  public TupleB file(BlobB content, StringB path) {
    return bytecodeDb.tuple(list(content, path));
  }

  public DefFuncB defFunc(TypeB resT, ImmutableList<TypeB> paramTs, ExprB body) {
    var type = categoryDb.funcT(resT, paramTs);
    return defFunc(type, body);
  }

  public DefFuncB defFunc(TypeB resT, TupleTB paramTs, ExprB body) {
    var type = categoryDb.funcT(resT, paramTs);
    return defFunc(type, body);
  }

  public DefFuncB defFunc(FuncTB type, ExprB body) {
    return bytecodeDb.defFunc(type, body);
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

  public NatFuncB natFunc(FuncTB funcTB, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return bytecodeDb.natFunc(funcTB, jar, classBinaryName, isPure);
  }

  public PickB pick(ExprB array, ExprB index) {
    return bytecodeDb.pick(array, index);
  }

  public RefB ref(TypeB evalT, BigInteger value) {
    return bytecodeDb.ref(evalT, value);
  }

  public SelectB select(ExprB tuple, IntB index) {
    return bytecodeDb.select(tuple, index);
  }

  public StringB string(String string) {
    return bytecodeDb.string(string);
  }

  public TupleB tuple(ImmutableList<InstB> items) {
    return bytecodeDb.tuple(items);
  }

  public OrderB order(ArrayTB arrayTB, ImmutableList<ExprB> elems) {
    return bytecodeDb.order(arrayTB, elems);
  }

  // Types

  public ArrayTB arrayT(TypeB elemT) {
    return categoryDb.array(elemT);
  }

  public BlobTB blobT() {
    return categoryDb.blob();
  }

  public BoolTB boolT() {
    return categoryDb.bool();
  }

  public FuncTB funcT(TypeB resT, ImmutableList<TypeB> paramTs) {
    return categoryDb.funcT(resT, paramTs);
  }

  public FuncTB funcT(TypeB resT, TupleTB paramTs) {
    return categoryDb.funcT(resT, paramTs);
  }

  public IntTB intT() {
    return categoryDb.int_();
  }

  public TupleTB messageT() {
    return messageT;
  }

  public StringTB stringT() {
    return categoryDb.string();
  }

  public TupleTB tupleT(TypeB... itemTs) {
    return categoryDb.tuple(itemTs);
  }

  public TupleTB tupleT(ImmutableList<TypeB> itemTs) {
    return categoryDb.tuple(itemTs);
  }

  // other values and its types

  public TupleTB fileT() {
    return fileT;
  }

  public TupleB fatalMessage(String text) {
    return message(FATAL, text);
  }

  public TupleB errorMessage(String text) {
    return message(ERROR, text);
  }

  public TupleB warningMessage(String text) {
    return message(WARNING, text);
  }

  public TupleB infoMessage(String text) {
    return message(INFO, text);
  }

  private TupleB message(Level level, String text) {
    InstB textObject = bytecodeDb.string(text);
    InstB severityObject = bytecodeDb.string(level.name());
    return bytecodeDb.tuple(list(textObject, severityObject));
  }

  private static TupleTB createMessageT(CategoryDb categoryDb) {
    var stringT = categoryDb.string();
    return categoryDb.tuple(stringT, stringT);
  }

  private static TupleTB createFileT(CategoryDb categoryDb) {
    return categoryDb.tuple(categoryDb.blob(), categoryDb.string());
  }
}
