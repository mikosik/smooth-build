package org.smoothbuild.vm.bytecode.type;

import org.smoothbuild.vm.bytecode.type.CategoryKindB.ArrayKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.BlobKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.BoolKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.CallKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.CombineKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.FuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.IfFuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.IntKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.LambdaKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.MapFuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.NativeFuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.OperKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.OrderKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.PickKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.SelectKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.StringKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.TupleKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.VarKindB;
import org.smoothbuild.vm.bytecode.type.oper.CallCB;
import org.smoothbuild.vm.bytecode.type.oper.CombineCB;
import org.smoothbuild.vm.bytecode.type.oper.OrderCB;
import org.smoothbuild.vm.bytecode.type.oper.PickCB;
import org.smoothbuild.vm.bytecode.type.oper.SelectCB;
import org.smoothbuild.vm.bytecode.type.oper.VarCB;

public class CategoryKinds {
  public static final CategoryKindB BLOB = new BlobKindB();
  public static final CategoryKindB BOOL = new BoolKindB();
  public static final CategoryKindB FUNC = new FuncKindB();
  public static final CategoryKindB INT = new IntKindB();
  public static final CategoryKindB STRING = new StringKindB();
  public static final CategoryKindB ARRAY = new ArrayKindB();
  public static final CategoryKindB TUPLE = new TupleKindB();
  public static final LambdaKindB LAMBDA = new LambdaKindB();
  public static final IfFuncKindB IF_FUNC = new IfFuncKindB();
  public static final MapFuncKindB MAP_FUNC = new MapFuncKindB();
  public static final NativeFuncKindB NATIVE_FUNC = new NativeFuncKindB();

  public static final OperKindB<CallCB> CALL = new CallKindB();
  public static final OperKindB<CombineCB> COMBINE = new CombineKindB();
  public static final OperKindB<OrderCB> ORDER = new OrderKindB();
  public static final OperKindB<PickCB> PICK = new PickKindB();
  public static final OperKindB<VarCB> VAR = new VarKindB();
  public static final OperKindB<SelectCB> SELECT = new SelectKindB();
}
