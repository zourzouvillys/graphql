package io.zrz.graphql.zulu.engine;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.zulu.doc.GQLPreparedDocument;
import io.zrz.graphql.zulu.doc.GQLPreparedOperation;

public class ZuluCompileResult {

  private final GQLPreparedOperation op;
  private final List<ZuluWarning> warnings;
  private final ZuluExecutable executable;
  private final GQLPreparedDocument doc;

  public ZuluCompileResult(GQLPreparedOperation op, ExecutableBuilder builder, ZuluExecutable executable) {
    this.op = op;
    this.warnings = builder.warnings();
    this.executable = executable;
    this.doc = op.document();
  }

  public ZuluCompileResult(GQLPreparedDocument doc, ImmutableList<ZuluWarning> warnings) {
    this.doc = doc;
    this.op = null;
    this.warnings = warnings;
    this.executable = null;
  }

  public ZuluExecutable executable() {
    return executable;
  }

  public List<ZuluWarning> warnings() {
    if (this.warnings == null)
      return Collections.emptyList();
    return this.warnings;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append(op).append("\n");
    sb.append(warnings).append("\n");
    sb.append(executable).append("\n");

    return sb.toString();

  }

  public static ZuluCompileResult withErrors(ZuluWarning... warnings) {
    return new ZuluCompileResult(null, ImmutableList.copyOf(warnings));
  }

  public static ZuluCompileResult withErrors(GQLPreparedDocument doc, ZuluWarning... warnings) {
    return new ZuluCompileResult(doc, ImmutableList.copyOf(warnings));
  }

}
