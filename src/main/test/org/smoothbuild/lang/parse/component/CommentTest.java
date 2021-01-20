package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Test;

public class CommentTest {
  @Test
  public void full_line_comment() {
    module("""
           # ((( full line comment "
           result = "";
           """)
        .loadsSuccessfully();
  }

  @Test
  public void trailing_comment() {
    module("""
           result = "" ;  # comment at the end of line
           """)
        .loadsSuccessfully();
  }
}
