package org.fluminous.jq.filter.json.obj

import cats.syntax.traverse._
import io.circe.Json
import org.fluminous.jq.filter.Filter
import org.fluminous.jq.{ Description, EvaluationException }

case class JsonObject(override val position: Int, values: Map[String, Filter]) extends Filter {
  override def transformSingle(input: Json): Either[EvaluationException, Json] = {
    values.toList.traverse { case (n, v) => v.transformSingle(input).map(j => (n, j)) }.map(m =>
      Json.fromFields(m.filterNot(_._2.isNull))
    )
  }
  override val description: String = JsonObject.typeDescription.description
}

object JsonObject {
  implicit def typeDescription: Description[JsonObject] = new Description[JsonObject] {
    override val description: String = "end of json object"
  }
}
