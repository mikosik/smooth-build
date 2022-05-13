package org.smoothbuild.lang.type;

public abstract sealed class MergingTS extends TypeS permits JoinTS, MeetTS {
  protected MergingTS(String name, VarSetS vars) {
    super(name, vars);
  }
}
