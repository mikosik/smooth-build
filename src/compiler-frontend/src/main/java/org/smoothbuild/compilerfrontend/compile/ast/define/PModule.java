package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.location.FileLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public final class PModule implements PContainer {
  private final FullPath fullPath;
  private final List<PStruct> structs;
  private final List<PPolyEvaluable> evaluables;
  private PScope scope;
  private Fqn fqn = null;

  public PModule(FullPath fullPath, List<PStruct> structs, List<PPolyEvaluable> evaluables) {
    this(fullPath, structs, evaluables, null);
  }

  public PModule(
      FullPath fullPath, List<PStruct> structs, List<PPolyEvaluable> evaluables, PScope scope) {
    this.fullPath = fullPath;
    this.structs = structs;
    this.evaluables = evaluables;
    this.scope = scope;
  }

  public FullPath fullPath() {
    return fullPath;
  }

  @Override
  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public Location location() {
    return new FileLocation(fullPath, 0);
  }

  public List<PPolyEvaluable> evaluables() {
    return evaluables;
  }

  public List<PStruct> structs() {
    return structs;
  }

  @Override
  public PScope scope() {
    return scope;
  }

  @Override
  public void setScope(PScope scope) {
    this.scope = scope;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PModule that
        && Objects.equals(this.structs, that.structs)
        && Objects.equals(this.evaluables, that.evaluables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(structs, evaluables);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PModule")
        .addField("fullPath", fullPath)
        .addListField("structs", structs)
        .addListField("evaluables", evaluables)
        .addField("scope", scope)
        .toString();
  }
}
