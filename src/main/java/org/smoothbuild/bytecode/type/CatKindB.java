package org.smoothbuild.bytecode.type;

import static org.smoothbuild.bytecode.type.CatKinds.ARRAY;
import static org.smoothbuild.bytecode.type.CatKinds.BLOB;
import static org.smoothbuild.bytecode.type.CatKinds.BOOL;
import static org.smoothbuild.bytecode.type.CatKinds.CALL;
import static org.smoothbuild.bytecode.type.CatKinds.COMBINE;
import static org.smoothbuild.bytecode.type.CatKinds.DEF_FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.IF_FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.INT;
import static org.smoothbuild.bytecode.type.CatKinds.MAP_FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.NAT_FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.ORDER;
import static org.smoothbuild.bytecode.type.CatKinds.PARAM_REF;
import static org.smoothbuild.bytecode.type.CatKinds.SELECT;
import static org.smoothbuild.bytecode.type.CatKinds.STRING;
import static org.smoothbuild.bytecode.type.CatKinds.TUPLE;

import java.util.function.BiFunction;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.DefFuncB;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.IfFuncB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.MapFuncB;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CatKindB.AbstFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.ArrayKindB;
import org.smoothbuild.bytecode.type.CatKindB.BaseKindB;
import org.smoothbuild.bytecode.type.CatKindB.CallKindB;
import org.smoothbuild.bytecode.type.CatKindB.CombineKindB;
import org.smoothbuild.bytecode.type.CatKindB.FuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.OrderKindB;
import org.smoothbuild.bytecode.type.CatKindB.ParamRefKindB;
import org.smoothbuild.bytecode.type.CatKindB.SelectKindB;
import org.smoothbuild.bytecode.type.CatKindB.TupleKindB;
import org.smoothbuild.bytecode.type.val.DefFuncCB;
import org.smoothbuild.bytecode.type.val.FuncCB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IfFuncCB;
import org.smoothbuild.bytecode.type.val.MapFuncCB;
import org.smoothbuild.bytecode.type.val.NatFuncCB;

public sealed abstract class CatKindB
    permits AbstFuncKindB, ArrayKindB, BaseKindB, CallKindB, CombineKindB, FuncKindB, OrderKindB,
    ParamRefKindB, SelectKindB, TupleKindB {
  public static sealed class BaseKindB extends CatKindB permits BlobKindB, BoolKindB, IntKindB,
      StringKindB {
    private BaseKindB(String name, byte marker, Class<? extends ExprB> typeJ) {
      super(name, marker, typeJ);
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

  public static final class ArrayKindB extends CatKindB {
    ArrayKindB() {
      super("ARRAY", (byte) 4, ArrayB.class);
    }
  }

  public static final class TupleKindB extends CatKindB {
    TupleKindB() {
      super("TUPLE", (byte) 5, TupleB.class);
    }
  }

  public static final class FuncKindB extends CatKindB {
    FuncKindB() {
      super("FUNC", (byte) 16, FuncB.class);
    }
  }

  public static sealed abstract class AbstFuncKindB<T extends FuncCB> extends CatKindB {
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

  public static final class OrderKindB extends CatKindB {
    OrderKindB() {
      super("ORDER", (byte) 8, OrderB.class);
    }
  }

  public static final class CombineKindB extends CatKindB {
    CombineKindB() {
      super("COMBINE", (byte) 9, CombineB.class);
    }
  }

  public static final class SelectKindB extends CatKindB {
    SelectKindB() {
      super("SELECT", (byte) 10, SelectB.class);
    }
  }

  public static final class CallKindB extends CatKindB {
    CallKindB() {
      super("CALL", (byte) 11, CallB.class);
    }
  }

  public static final class IfFuncKindB extends AbstFuncKindB<IfFuncCB>  {
    IfFuncKindB() {
      super("IF_FUNC", (byte) 13, IfFuncB.class, IfFuncCB::new);
    }
  }

  public static final class ParamRefKindB extends CatKindB {
    ParamRefKindB() {
      super("PARAM_REF", (byte) 14, ParamRefB.class);
    }
  }

  public static final class MapFuncKindB extends AbstFuncKindB<MapFuncCB> {
    MapFuncKindB() {
      super("MAP_FUNC", (byte) 15, MapFuncB.class, MapFuncCB::new);
    }
  }

  private final String name;
  private final byte marker;
  private final Class<? extends ExprB> typeJ;

  private CatKindB(String name, byte marker, Class<? extends ExprB> typeJ) {
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

  public static CatKindB fromMarker(byte marker) {
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
      // TODO 12 is unused
      case 13 -> IF_FUNC;
      case 14 -> PARAM_REF;
      case 15 -> MAP_FUNC;
      case 16 -> FUNC;
      default -> null;
    };
  }
}
