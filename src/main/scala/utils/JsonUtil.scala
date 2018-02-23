package utils

import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Call, RequestHeader}

/**
  * Created by Andreas Boss on 30.08.16.
  */
trait JsonUtil {
  def addSelfLink(json: JsValue, c: Call)(implicit request: RequestHeader) =
    Json.obj("_links" -> Json.obj("rel" -> "self", "href" -> c.absoluteURL())) ++ json
      .as[JsObject]
}
