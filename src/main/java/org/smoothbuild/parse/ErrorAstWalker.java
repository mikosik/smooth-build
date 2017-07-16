package org.smoothbuild.parse;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.parse.ast.AstWalker;

public class ErrorAstWalker extends AstWalker<List<ParseError>> {
  @Override
  public List<ParseError> reduce(List<ParseError> a, List<ParseError> b) {
    ArrayList<ParseError> result = new ArrayList<>();
    result.addAll(a);
    result.addAll(b);
    return result;
  }

  @Override
  public List<ParseError> reduceIdentity() {
    return new ArrayList<>();
  }
}