package org.smoothbuild.bytecode.type;

import org.smoothbuild.bytecode.type.CategoryKindB.ArrayKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.BlobKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.BoolKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.CallKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.CombineKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.DefFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.FuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.IfFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.IntKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.MapFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.NatFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.OrderKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.RefKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.SelectKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.StringKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.TupleKindB;

public class CategoryKinds {
  public static final CategoryKindB BLOB = new BlobKindB();
  public static final CategoryKindB BOOL = new BoolKindB();
  public static final CategoryKindB INT = new IntKindB();
  public static final CategoryKindB STRING = new StringKindB();
  public static final CategoryKindB ARRAY = new ArrayKindB();
  public static final CategoryKindB TUPLE = new TupleKindB();
  public static final DefFuncKindB DEF_FUNC = new DefFuncKindB();
  public static final IfFuncKindB IF_FUNC = new IfFuncKindB();
  public static final MapFuncKindB MAP_FUNC = new MapFuncKindB();
  public static final NatFuncKindB NAT_FUNC = new NatFuncKindB();
  public static final CategoryKindB ORDER = new OrderKindB();
  public static final CategoryKindB COMBINE = new CombineKindB();
  public static final CategoryKindB SELECT = new SelectKindB();
  public static final CategoryKindB CALL = new CallKindB();
  public static final CategoryKindB REF = new RefKindB();
  public static final CategoryKindB FUNC = new FuncKindB();
}
