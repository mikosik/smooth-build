package org.smoothbuild.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.util.collect.NList.nListWithNonUniqueNames;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class StructN extends MonoNamedN {
  private final NList<ItemN> fields;
  private final FuncN ctor;

  public StructN(String name, List<ItemN> fields, Loc loc) {
    this(name, nListWithNonUniqueNames(ImmutableList.copyOf(fields)), loc);
  }

  private StructN(String name, NList<ItemN> fields, Loc loc) {
    super(name, loc);
    this.fields = fields;
    this.ctor = new FuncN(
        Optional.of(new TypeN(name, loc)),
        UPPER_CAMEL.to(LOWER_CAMEL, name), fields, Optional.empty(), Optional.empty(),
        loc);
  }

  public FuncN ctor() {
    return ctor;
  }

  public NList<ItemN> fields() {
    return fields;
  }
}
