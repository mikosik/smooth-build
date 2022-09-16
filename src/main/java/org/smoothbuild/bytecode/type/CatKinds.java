package org.smoothbuild.bytecode.type;

import org.smoothbuild.bytecode.type.CatKindB.ArrayKindB;
import org.smoothbuild.bytecode.type.CatKindB.BlobKindB;
import org.smoothbuild.bytecode.type.CatKindB.BoolKindB;
import org.smoothbuild.bytecode.type.CatKindB.CallKindB;
import org.smoothbuild.bytecode.type.CatKindB.CombineKindB;
import org.smoothbuild.bytecode.type.CatKindB.DefFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.FuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.IfFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.IntKindB;
import org.smoothbuild.bytecode.type.CatKindB.MapFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.NatFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.OrderKindB;
import org.smoothbuild.bytecode.type.CatKindB.ParamRefKindB;
import org.smoothbuild.bytecode.type.CatKindB.SelectKindB;
import org.smoothbuild.bytecode.type.CatKindB.StringKindB;
import org.smoothbuild.bytecode.type.CatKindB.TupleKindB;

public class CatKinds {
  public static final CatKindB BLOB = new BlobKindB();
  public static final CatKindB BOOL = new BoolKindB();
  public static final CatKindB INT = new IntKindB();
  public static final CatKindB STRING = new StringKindB();
  public static final CatKindB ARRAY = new ArrayKindB();
  public static final CatKindB TUPLE = new TupleKindB();
  public static final DefFuncKindB DEF_FUNC = new DefFuncKindB();
  public static final IfFuncKindB IF_FUNC = new IfFuncKindB();
  public static final MapFuncKindB MAP_FUNC = new MapFuncKindB();
  public static final NatFuncKindB NAT_FUNC = new NatFuncKindB();
  public static final CatKindB ORDER = new OrderKindB();
  public static final CatKindB COMBINE = new CombineKindB();
  public static final CatKindB SELECT = new SelectKindB();
  public static final CatKindB CALL = new CallKindB();
  public static final CatKindB PARAM_REF = new ParamRefKindB();
  public static final CatKindB FUNC = new FuncKindB();
}
