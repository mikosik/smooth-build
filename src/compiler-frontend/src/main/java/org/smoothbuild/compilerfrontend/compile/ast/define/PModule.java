package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public final class PModule implements PScoped {
  private final String fileName;
  private final List<PStruct> structs;
  private final List<PNamedEvaluable> evaluables;
  private PScope scope;

  public PModule(String fileName, List<PStruct> structs, List<PNamedEvaluable> evaluables) {
    this(fileName, structs, evaluables, null);
  }

  public PModule(
      String fileName, List<PStruct> structs, List<PNamedEvaluable> evaluables, PScope scope) {
    this.fileName = fileName;
    this.structs = structs;
    this.evaluables = evaluables;
    this.scope = scope;
  }

  public String fileName() {
    return fileName;
  }

  @Override
  public Id id() {
    return null;
  }

  public List<PNamedEvaluable> evaluables() {
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
        .addField("fileName", fileName)
        .addListField("structs", structs)
        .addListField("evaluables", evaluables)
        .addField("scope", scope)
        .toString();
  }
}
