package org.smoothbuild.db.bytecode.obj;

import java.math.BigInteger;

import org.smoothbuild.db.bytecode.obj.base.ObjB;
import org.smoothbuild.db.bytecode.obj.expr.CallB;
import org.smoothbuild.db.bytecode.obj.expr.CombineB;
import org.smoothbuild.db.bytecode.obj.expr.IfB;
import org.smoothbuild.db.bytecode.obj.expr.InvokeB;
import org.smoothbuild.db.bytecode.obj.expr.MapB;
import org.smoothbuild.db.bytecode.obj.expr.OrderB;
import org.smoothbuild.db.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.db.bytecode.obj.expr.SelectB;
import org.smoothbuild.db.bytecode.obj.val.ArrayBBuilder;
import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.db.bytecode.obj.val.BlobBBuilder;
import org.smoothbuild.db.bytecode.obj.val.BoolB;
import org.smoothbuild.db.bytecode.obj.val.FuncB;
import org.smoothbuild.db.bytecode.obj.val.IntB;
import org.smoothbuild.db.bytecode.obj.val.MethodB;
import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.bytecode.type.val.FuncTB;
import org.smoothbuild.db.bytecode.type.val.MethodTB;
import org.smoothbuild.db.bytecode.type.val.TupleTB;
import org.smoothbuild.db.hashed.Hash;

import com.google.common.collect.ImmutableList;

public interface ByteDb {
  ArrayBBuilder arrayBuilder(ArrayTB type);

  BlobBBuilder blobBuilder();

  BoolB bool(boolean value);

  MethodB method(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure);

  FuncB func(FuncTB type, ObjB body);

  IntB int_(BigInteger value);

  StringB string(String value);

  TupleB tuple(TupleTB tupleT, ImmutableList<ValB> items);

  CallB call(TypeB evalT, ObjB callable, CombineB args);

  CombineB combine(TupleTB evalT, ImmutableList<ObjB> items);

  IfB if_(ObjB condition, ObjB then, ObjB else_);

  InvokeB invoke(TypeB evalT, ObjB method, CombineB args);

  MapB map(ObjB array, ObjB func);

  OrderB order(ArrayTB arrayTB, ImmutableList<ObjB> elems);

  ParamRefB paramRef(TypeB evalT, BigInteger value);

  SelectB select(TypeB evalT, ObjB selectable, IntB index);

  ObjB get(Hash rootHash);
}
