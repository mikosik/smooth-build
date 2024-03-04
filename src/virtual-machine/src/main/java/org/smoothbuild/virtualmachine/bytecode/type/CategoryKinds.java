package org.smoothbuild.virtualmachine.bytecode.type;

import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.ArrayKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.BlobKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.BoolKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.CallKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.CombineKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.FuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.IfFuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.IntKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.LambdaKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.MapFuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.NativeFuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.OperKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.OrderKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.PickKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.ReferenceKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.SelectKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.StringKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.TupleKindB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CallCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CombineCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OrderCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.PickCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.ReferenceCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.SelectCB;

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
  public static final OperKindB<ReferenceCB> REFERENCE = new ReferenceKindB();
  public static final OperKindB<SelectCB> SELECT = new SelectKindB();
}
