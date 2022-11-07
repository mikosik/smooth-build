package org.smoothbuild.bytecode.type;

import static org.smoothbuild.bytecode.type.CategoryKinds.ARRAY;
import static org.smoothbuild.bytecode.type.CategoryKinds.BLOB;
import static org.smoothbuild.bytecode.type.CategoryKinds.BOOL;
import static org.smoothbuild.bytecode.type.CategoryKinds.CALL;
import static org.smoothbuild.bytecode.type.CategoryKinds.CLOSURIZE;
import static org.smoothbuild.bytecode.type.CategoryKinds.COMBINE;
import static org.smoothbuild.bytecode.type.CategoryKinds.DEF_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.IF_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.INT;
import static org.smoothbuild.bytecode.type.CategoryKinds.MAP_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.NAT_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.ORDER;
import static org.smoothbuild.bytecode.type.CategoryKinds.PICK;
import static org.smoothbuild.bytecode.type.CategoryKinds.REF;
import static org.smoothbuild.bytecode.type.CategoryKinds.SELECT;
import static org.smoothbuild.bytecode.type.CategoryKinds.STRING;
import static org.smoothbuild.bytecode.type.CategoryKinds.TUPLE;

import java.util.function.BiFunction;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.BoolB;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.bytecode.expr.inst.FuncB;
import org.smoothbuild.bytecode.expr.inst.IfFuncB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.MapFuncB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.ClosurizeB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CategoryKindB.AbstFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.ArrayKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.BaseKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.ClosurizeKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.FuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.OperKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.TupleKindB;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.bytecode.type.inst.DefFuncCB;
import org.smoothbuild.bytecode.type.inst.FuncCB;
import org.smoothbuild.bytecode.type.inst.FuncTB;
import org.smoothbuild.bytecode.type.inst.IfFuncCB;
import org.smoothbuild.bytecode.type.inst.MapFuncCB;
import org.smoothbuild.bytecode.type.inst.NatFuncCB;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.bytecode.type.oper.CallCB;
import org.smoothbuild.bytecode.type.oper.CombineCB;
import org.smoothbuild.bytecode.type.oper.OperCB;
import org.smoothbuild.bytecode.type.oper.OrderCB;
import org.smoothbuild.bytecode.type.oper.PickCB;
import org.smoothbuild.bytecode.type.oper.RefCB;
import org.smoothbuild.bytecode.type.oper.SelectCB;

public sealed abstract class CategoryKindB
    permits AbstFuncKindB, ArrayKindB, BaseKindB, ClosurizeKindB, FuncKindB, OperKindB, TupleKindB {
  public static sealed class BaseKindB extends CategoryKindB
      permits BlobKindB, BoolKindB, IntKindB, StringKindB {
    private BaseKindB(String name, byte marker, Class<? extends ExprB> typeJ) {
      super(name, marker, typeJ);
    }
  }

  public static sealed class OperKindB<T extends OperCB> extends CategoryKindB
      permits CallKindB, CombineKindB, OrderKindB, PickKindB, RefKindB, SelectKindB {
    private final BiFunction<Hash, TypeB, T> constructor;
    private final Class<? extends TypeB> dataClass;

    private OperKindB(String name, byte marker, BiFunction<Hash, TypeB, T> constructor,
        Class<? extends TypeB> dataClass, Class<? extends ExprB> typeJ) {
      super(name, marker, typeJ);
      this.constructor = constructor;
      this.dataClass = dataClass;
    }

    public BiFunction<Hash, TypeB, T> constructor() {
      return constructor;
    }

    public Class<? extends TypeB> dataClass() {
      return dataClass;
    }
  }

  public static final class BlobKindB extends BaseKindB {
    BlobKindB() {
      super("BLOB", (byte) 0, BlobB.class);
    }
  }

  public static final class BoolKindB extends BaseKindB {
    BoolKindB() {
      super("BOOL", (byte) 1, BoolB.class);
    }
  }

  public static final class IntKindB extends BaseKindB {
    IntKindB() {
      super("INT", (byte) 2, IntB.class);
    }
  }

  public static final class StringKindB extends BaseKindB {
    StringKindB() {
      super("STRING", (byte) 3, StringB.class);
    }
  }

  public static final class ArrayKindB extends CategoryKindB {
    ArrayKindB() {
      super("ARRAY", (byte) 4, ArrayB.class);
    }
  }

  public static final class TupleKindB extends CategoryKindB {
    TupleKindB() {
      super("TUPLE", (byte) 5, TupleB.class);
    }
  }

  public static final class FuncKindB extends CategoryKindB {
    FuncKindB() {
      super("FUNC", (byte) 16, FuncB.class);
    }
  }

  public static sealed abstract class AbstFuncKindB<T extends FuncCB> extends CategoryKindB {
    private final BiFunction<Hash, FuncTB, T> instantiator;

    private AbstFuncKindB(String name, byte marker, Class<? extends ExprB> typeJ,
        BiFunction<Hash, FuncTB, T> instantiator) {
      super(name, marker, typeJ);
      this.instantiator = instantiator;
    }

    public BiFunction<Hash, FuncTB, T> instantiator() {
      return instantiator;
    }
  }

  public static final class DefFuncKindB extends AbstFuncKindB<DefFuncCB> {
    DefFuncKindB() {
      super("DEF_FUNC", (byte) 6, DefFuncB.class, DefFuncCB::new);
    }
  }

  public static final class NatFuncKindB extends AbstFuncKindB<NatFuncCB> {
    NatFuncKindB() {
      super("NAT_FUNC", (byte) 7, NatFuncB.class, NatFuncCB::new);
    }
  }

  public static final class OrderKindB extends OperKindB<OrderCB> {
    OrderKindB() {
      super("ORDER", (byte) 8, OrderCB::new, ArrayTB.class, OrderB.class);
    }
  }

  public static final class CombineKindB extends OperKindB<CombineCB> {
    CombineKindB() {
      super("COMBINE", (byte) 9, CombineCB::new, TupleTB.class, CombineB.class);
    }
  }

  public static final class SelectKindB extends OperKindB<SelectCB> {
    SelectKindB() {
      super("SELECT", (byte) 10, SelectCB::new, TypeB.class, SelectB.class);
    }
  }

  public static final class CallKindB extends OperKindB<CallCB> {
    CallKindB() {
      super("CALL", (byte) 11, CallCB::new, TypeB.class, CallB.class);
    }
  }

  public static final class PickKindB extends OperKindB<PickCB> {
    PickKindB() {
      super("PICK", (byte) 12, PickCB::new, TypeB.class, PickB.class);
    }
  }

  public static final class IfFuncKindB extends AbstFuncKindB<IfFuncCB>  {
    IfFuncKindB() {
      super("IF_FUNC", (byte) 13, IfFuncB.class, IfFuncCB::new);
    }
  }

  public static final class RefKindB extends OperKindB<RefCB> {
    RefKindB() {
      super("REF", (byte) 14, RefCB::new, TypeB.class, RefB.class);
    }
  }

  public static final class MapFuncKindB extends AbstFuncKindB<MapFuncCB> {
    MapFuncKindB() {
      super("MAP_FUNC", (byte) 15, MapFuncB.class, MapFuncCB::new);
    }
  }

  public static final class ClosurizeKindB extends CategoryKindB {
    ClosurizeKindB() {
      super("CLOSURIZE", (byte) 17, ClosurizeB.class);
    }
  }

  private final String name;
  private final byte marker;
  private final Class<? extends ExprB> typeJ;

  private CategoryKindB(String name, byte marker, Class<? extends ExprB> typeJ) {
    this.name = name;
    this.marker = marker;
    this.typeJ = typeJ;
  }

  public String name() {
    return name;
  }

  public byte marker() {
    return marker;
  }

  public Class<? extends ExprB> typeJ() {
    return typeJ;
  }

  public static CategoryKindB fromMarker(byte marker) {
    return switch (marker) {
      case 0 -> BLOB;
      case 1 -> BOOL;
      case 2 -> INT;
      case 3 -> STRING;
      case 4 -> ARRAY;
      case 5 -> TUPLE;
      case 6 -> DEF_FUNC;
      case 7 -> NAT_FUNC;
      case 8 -> ORDER;
      case 9 -> COMBINE;
      case 10 -> SELECT;
      case 11 -> CALL;
      case 12 -> PICK;
      case 13 -> IF_FUNC;
      case 14 -> REF;
      case 15 -> MAP_FUNC;
      case 16 -> FUNC;
      case 17 -> CLOSURIZE;
      default -> null;
    };
  }
}
