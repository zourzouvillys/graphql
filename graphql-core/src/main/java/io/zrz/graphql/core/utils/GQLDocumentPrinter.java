package io.zrz.graphql.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import io.zrz.graphql.core.doc.GQLDefinition;
import io.zrz.graphql.core.doc.GQLDocument;

/**
 * Prints the document in "standard" (GQLSpec) form.
 * 
 * @author theo
 *
 */

public class GQLDocumentPrinter
{

  public String serialize(GQLDocument doc)
  {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try (PrintStream ps = new PrintStream(os))
    {
      // printstream being closed closes the underlying stream too.
      print(doc, ps);
      return os.toString();
    }
  }

  /**
   * 
   * @param doc
   * @param strm
   */

  public void print(GQLDocument doc, PrintStream strm)
  {

    for (GQLDefinition def : doc.definitions())
    {
      def.apply(new DefinitionPrinter(strm));
    }

  }


}
