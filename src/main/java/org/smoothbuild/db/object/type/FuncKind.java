package org.smoothbuild.db.object.type;

import static org.smoothbuild.db.object.type.base.CatKindH.ABST_FUNC;
import static org.smoothbuild.db.object.type.base.CatKindH.DEF_FUNC;
import static org.smoothbuild.db.object.type.base.CatKindH.IF_FUNC;
import static org.smoothbuild.db.object.type.base.CatKindH.MAP_FUNC;
import static org.smoothbuild.db.object.type.base.CatKindH.NAT_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.AbstFuncTH;
import org.smoothbuild.db.object.type.val.DefFuncTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.IfFuncTH;
import org.smoothbuild.db.object.type.val.MapFuncTH;
import org.smoothbuild.db.object.type.val.NatFuncTH;
import org.smoothbuild.db.object.type.val.TupleTH;

public abstract class FuncKind<T extends FuncTH> {
  public static final FuncKind<AbstFuncTH> ABSTRACT_KIND =
      new FuncKind<>(ABST_FUNC) {
        @Override
        public AbstFuncTH newInstance(Hash hash, TypeH result, TupleTH params) {
          return new AbstFuncTH(hash, result, params);
        }
      };
  public static final FuncKind<NatFuncTH> NATIVE_KIND =
      new FuncKind<>(NAT_FUNC) {
        @Override
        public NatFuncTH newInstance(Hash hash, TypeH result, TupleTH params) {
          return new NatFuncTH(hash, result, params);
        }
      };
  public static final FuncKind<DefFuncTH> DEFINED_KIND =
      new FuncKind<>(DEF_FUNC) {
        @Override
        public DefFuncTH newInstance(Hash hash, TypeH result, TupleTH params) {
          return new DefFuncTH(hash, result, params);
        }
      };
  public static final FuncKind<IfFuncTH> IF_KIND =
      new FuncKind<>(IF_FUNC) {
        @Override
        public IfFuncTH newInstance(Hash hash, TypeH result, TupleTH params) {
          return new IfFuncTH(hash, result, params);
        }
      };
  public static final FuncKind<MapFuncTH> MAP_KIND =
      new FuncKind<>(MAP_FUNC) {
        @Override
        public MapFuncTH newInstance(Hash hash, TypeH result, TupleTH params) {
          return new MapFuncTH(hash, result, params);
        }
      };

  private final CatKindH kind;

  private FuncKind(CatKindH kind) {
    this.kind = kind;
  }

  public static FuncKind<?> from(CatKindH kind) {
    return switch (kind) {
      case ABST_FUNC -> ABSTRACT_KIND;
      case DEF_FUNC -> DEFINED_KIND;
      case NAT_FUNC -> NATIVE_KIND;
      case IF_FUNC -> IF_KIND;
      case MAP_FUNC -> MAP_KIND;
      default -> throw new IllegalArgumentException();
    };
  }

  public CatKindH kind() {
    return kind;
  }

  public abstract T newInstance(Hash hash, TypeH result, TupleTH params);
}
