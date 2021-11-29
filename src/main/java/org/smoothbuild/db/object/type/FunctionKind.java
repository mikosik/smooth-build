package org.smoothbuild.db.object.type;

import static org.smoothbuild.db.object.type.base.SpecKindH.ABST_FUNC;
import static org.smoothbuild.db.object.type.base.SpecKindH.DEF_FUNC;
import static org.smoothbuild.db.object.type.base.SpecKindH.IF_FUNC;
import static org.smoothbuild.db.object.type.base.SpecKindH.MAP_FUNC;
import static org.smoothbuild.db.object.type.base.SpecKindH.NAT_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.SpecKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.AbstFuncTypeH;
import org.smoothbuild.db.object.type.val.DefFuncTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
import org.smoothbuild.db.object.type.val.IfFuncTypeH;
import org.smoothbuild.db.object.type.val.MapFuncTypeH;
import org.smoothbuild.db.object.type.val.NatFuncTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

abstract class FuncKind<T extends FuncTypeH> {
  public static final FuncKind<AbstFuncTypeH> ABSTRACT_KIND =
      new FuncKind<>(ABST_FUNC) {
        @Override
        public AbstFuncTypeH newInstance(Hash hash, TypeH result, TupleTypeH params) {
          return new AbstFuncTypeH(hash, result, params);
        }
      };
  public static final FuncKind<NatFuncTypeH> NATIVE_KIND =
      new FuncKind<>(NAT_FUNC) {
        @Override
        public NatFuncTypeH newInstance(Hash hash, TypeH result, TupleTypeH params) {
          return new NatFuncTypeH(hash, result, params);
        }
      };
  public static final FuncKind<DefFuncTypeH> DEFINED_KIND =
      new FuncKind<>(DEF_FUNC) {
        @Override
        public DefFuncTypeH newInstance(Hash hash, TypeH result, TupleTypeH params) {
          return new DefFuncTypeH(hash, result, params);
        }
      };
  public static final FuncKind<IfFuncTypeH> IF_KIND =
      new FuncKind<>(IF_FUNC) {
        @Override
        public IfFuncTypeH newInstance(Hash hash, TypeH result, TupleTypeH params) {
          return new IfFuncTypeH(hash, result, params);
        }
      };
  public static final FuncKind<MapFuncTypeH> MAP_KIND =
      new FuncKind<>(MAP_FUNC) {
        @Override
        public MapFuncTypeH newInstance(Hash hash, TypeH result, TupleTypeH params) {
          return new MapFuncTypeH(hash, result, params);
        }
      };

  private final SpecKindH kind;

  private FuncKind(SpecKindH kind) {
    this.kind = kind;
  }

  public static FuncKind<?> from(SpecKindH kind) {
    return switch (kind) {
      case ABST_FUNC -> ABSTRACT_KIND;
      case DEF_FUNC -> DEFINED_KIND;
      case NAT_FUNC -> NATIVE_KIND;
      case IF_FUNC -> IF_KIND;
      case MAP_FUNC -> MAP_KIND;
      default -> throw new IllegalArgumentException();
    };
  }

  public SpecKindH kind() {
    return kind;
  }

  public abstract T newInstance(Hash hash, TypeH result, TupleTypeH params);
}
