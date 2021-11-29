package org.smoothbuild.lang.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.util.collect.NList.nListWithDuplicates;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class StructN extends NamedN {
  private final NList<ItemN> fields;
  private final ConstructorN constructor;

  public StructN(String name, List<ItemN> fields, Location location) {
    this(name, nListWithDuplicates(ImmutableList.copyOf(fields)), location);
  }

  private StructN(String name, NList<ItemN> fields, Location location) {
    super(name, location);
    this.fields = fields;
    this.constructor = new ConstructorN(name, fields, location);
  }

  public ConstructorN constructor() {
    return constructor;
  }

  public NList<ItemN> fields() {
    return fields;
  }

  public final class ConstructorN extends FuncN {
    public ConstructorN(String structName, List<ItemN> params, Location location) {
      super(
          Optional.of(new TypeN(structName, location)),
          UPPER_CAMEL.to(LOWER_CAMEL, structName),
          Optional.empty(),
          params,
          Optional.empty(),
          location);
    }

    @Override
    public NList<ItemN> params() {
      return fields;
    }
  }
}
