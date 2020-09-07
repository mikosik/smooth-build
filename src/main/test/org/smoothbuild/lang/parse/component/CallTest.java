package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Test;

public class CallTest {
  @Test
  public void call_without_parentheses_inside_pipe_is_allowed() {
    module("""
           myIdentity(A value) = value;
           result = "abc" | myIdentity;
           """)
        .loadsSuccessfully();
  }
}
