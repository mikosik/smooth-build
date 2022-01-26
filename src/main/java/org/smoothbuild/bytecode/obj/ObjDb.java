package org.smoothbuild.bytecode.obj;

import java.math.BigInteger;

import org.smoothbuild.bytecode.obj.base.ObjB;
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
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.db.Hash;

import com.google.common.collect.ImmutableList;

public interface ObjDb {
  public ArrayBBuilder arrayBuilder(ArrayTB type);

  public BlobBBuilder blobBuilder();

  public BoolB bool(boolean value);

  public MethodB method(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure);

  public FuncB func(FuncTB type, ObjB body);

  public IntB int_(BigInteger value);

  public StringB string(String value);

  public TupleB tuple(TupleTB tupleT, ImmutableList<ValB> items);

  public CallB call(TypeB evalT, ObjB func, CombineB args);

  public CombineB combine(TupleTB evalT, ImmutableList<ObjB> items);

  public IfB if_(ObjB condition, ObjB then, ObjB else_);

  public InvokeB invoke(TypeB evalT, ObjB method, CombineB args);

  public MapB map(ObjB array, ObjB func);

  public OrderB order(ArrayTB evalT, ImmutableList<ObjB> elems);

  public ParamRefB paramRef(TypeB evalT, BigInteger value);

  public SelectB select(TypeB evalT, ObjB selectable, IntB index);

  public ObjB get(Hash hash);
}
