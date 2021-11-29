package org.smoothbuild.lang.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.util.collect.NList.nListWithDuplicates;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class StructN extends NamedN {
  private final NList<ItemN> fields;
  private final CtorN ctor;

  public StructN(String name, List<ItemN> fields, Loc loc) {
    this(name, nListWithDuplicates(ImmutableList.copyOf(fields)), loc);
  }

  private StructN(String name, NList<ItemN> fields, Loc loc) {
    super(name, loc);
    this.fields = fields;
    this.ctor = new CtorN(name, fields, loc);
  }

  public CtorN ctor() {
    return ctor;
  }

  public NList<ItemN> fields() {
    return fields;
  }

  public final class CtorN extends FuncN {
    public CtorN(String structName, List<ItemN> params, Loc loc) {
      super(
          Optional.of(new TypeN(structName, loc)),
          UPPER_CAMEL.to(LOWER_CAMEL, structName),
          Optional.empty(),
          params,
          Optional.empty(),
          loc);
    }

    @Override
    public NList<ItemN> params() {
      return fields;
    }
  }
}
