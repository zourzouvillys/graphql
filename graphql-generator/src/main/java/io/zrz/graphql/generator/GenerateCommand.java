package io.zrz.graphql.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

import io.zrz.graphql.core.parser.DefaultGQLParser;
import io.zrz.graphql.generator.java.SimpleJavaGenerator;
import io.zrz.graphql.generator.protobuf.ProtobufGenerator;
import io.zrz.zulu.schema.ResolvedSchema;
import io.zrz.zulu.schema.SchemaCompiler;
import io.zrz.zulu.schema.binding.BoundDocument;
import io.zrz.zulu.schema.binding.BoundElementPrintingVisitor;
import io.zrz.zulu.schema.binding.BoundOperation;

/**
 *
 * @author theo
 *
 */

@Command(name = "generate")
public class GenerateCommand implements CliRunnable {

  @Option(name = "--javaPackage")
  public String javaPackage = "";

  @Option(name = "--clientName")
  public String clientName = "ZuluStub";

  @Option(name = "--queryRoot")
  public String queryRoot = null;

  @Option(name = "--mutationRoot")
  public String mutationRoot = null;

  @Option(name = "--subscriptionRoot")
  public String subscriptionRoot = null;

  @Option(name = "--dumpTree")
  public boolean dumpTree = false;

  @Arguments
  public List<String> files = new LinkedList<>();

  @Override
  public void run() throws IOException {

    // generate schema from file
    final SchemaCompiler compiler = new SchemaCompiler();

    this.files
        .stream()
        .map(Paths::get)
        .filter(path -> path.toString().endsWith(".schema"))
        .forEach(path -> compiler.addUnit(path));

    final ResolvedSchema schema = compiler.compile(this.queryRoot, this.mutationRoot);

    // parse the queries
    final List<BoundOperation> operations = this.files
        .stream()
        .map(Paths::get)
        .filter(path -> path.toString().endsWith(".graphql") || path.toString().endsWith(".gql") || path.toString().endsWith(".query"))
        .peek(System.err::println)
        .map(DefaultGQLParser::parseDocument)
        .map(doc -> new BoundDocument(schema, doc))
        .flatMap(doc -> doc.operations().stream())
        .collect(Collectors.toList());

    if (this.dumpTree) {
      operations.forEach(op -> op.accept(new BoundElementPrintingVisitor(System.out)));
    }

    this.write(operations);

  }

  private void write(final List<BoundOperation> operations) throws IOException {
    final Path target = Paths.get("./autogen");
    Files.createDirectories(target);
    new SimpleJavaGenerator(operations, target, this.javaPackage, this.clientName).write();
    new ProtobufGenerator(operations, target, this.javaPackage, this.clientName).write();
  }

}
