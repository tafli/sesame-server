package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Created by Andreas Boss on 23.08.16.
  */
class Application extends Controller {

  /**
    * Redirects to the api index
    * @return
    */
  def index = Action {
    Redirect(routes.Application.apiIndex())
  }

  /**
    * Entrypoint for the API
    * @return
    */
  def apiIndex = Action { implicit request =>
    val json = Json.obj(
      "version" -> "0.1",
      "_links" -> Seq(
        Json.obj("rel" -> "self", "href" -> routes.Application.index().absoluteURL())
        , Json.obj("rel" -> "bricklets", "href" -> routes.Bricklets.getBricklets().absoluteURL())
        , Json.obj("rel" -> "door", "href" -> routes.Doors.getDoors().absoluteURL())
      )
    )

    Ok(json)
  }
}
