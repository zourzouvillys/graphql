package io.joss.graphql.client.channel.unirest;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.joss.graphql.client.runtime.GQLExecution;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.utils.GQLDocumentPrinter;
import io.joss.graphql.core.value.GQLListValue;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValues;

public class UnirestExecution implements GQLExecution
{

  private UnirestHttpChannel unirest;
  private GQLDocument doc;
  private String name;
  private GQLObjectValue input;

  /**
   * @param unirest
   * @param doc
   * @param name
   * @param input
   */

  public UnirestExecution(UnirestHttpChannel unirest, GQLDocument doc, String name, GQLObjectValue input)
  {
    this.unirest = unirest;
    this.doc = doc;
    this.name = name;
    this.input = input;
  }


  
  /**
   * Return as a {@link GQLValue}.
   */

  @Override
  public GQLObjectValue get()
  {

    JSONObject node = new JSONObject();

    node.put("query", new GQLDocumentPrinter().serialize(doc));
    node.put("variables", new JSONObject());

    try
    {

      HttpResponse<String> req = Unirest.post(unirest.getUrl())
          .headers(unirest.getHeaders())
          .header("Accept", "application/json")
          .header("Content-Type", "application/json")
          .body(new JsonNode(node.toString()))
          .asString();

      if (req.getStatus() != 200)
      {
        throw new UnirestHttpException(req.getStatus(), req.getStatusText(), req.getHeaders(), req.getBody());
      }

      JSONObject body = new JSONObject(req.getBody());

      JSONObject data = body.getJSONObject("data");

      return convert(data);

    }
    catch (UnirestException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }

  /**
   * 
   * @param data
   * @return
   */

  private GQLObjectValue convert(JSONObject data)
  {
    GQLObjectValue.Builder ob = GQLObjectValue.builder();
    for (String key : (Set<String>) data.keySet())
    {
      ob.value(key, convert(data.get(key)));
    }
    return ob.build();
  }

  private GQLListValue convert(JSONArray data)
  {
    GQLListValue.Builder ob = GQLListValue.builder();
    for (int i = 0; i < data.length(); ++i)
    {
      ob.value(convert(data.get(i)));
    }
    return ob.build();
  }

  private GQLValue convert(Object object)
  {
    if (object == null || JSONObject.NULL.equals(object))
    {
      return null;
    }
    else if (object instanceof JSONObject)
    {
      return convert((JSONObject) object);
    }
    else if (object instanceof JSONString)
    {
      return GQLValues.stringValue(object.toString());
    }
    else if (object instanceof JSONArray)
    {
      return convert((JSONArray) object);
    }
    else if (object instanceof String)
    {
      return GQLValues.stringValue((String) object);
    }
    else if (object instanceof Integer || object instanceof Integer)
    {
      return GQLValues.intValue((int)object);
    }
    else if (object instanceof Long)
    {
      return GQLValues.intValue((long)object);
    }
    else if (object instanceof Double)
    {
      return GQLValues.floatValue((double)object);
    }
    else if (object instanceof Float)
    {
      return GQLValues.floatValue((float)object);
    }
    else
    {
      throw new RuntimeException("Unable to convert JSON " + object.getClass());
    }
  }

}
