package controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.BuildInfo

class Application @Inject() (cc:ControllerComponents) extends AbstractController(cc) {
  def index: Action[AnyContent] = Action {
    Redirect(routes.Application.apiIndex())
  }

  /**
    * Entrypoint for the API
    * @return
    */
  def apiIndex = Action { implicit request =>
    val json = Json.obj(
      "version" -> s"${BuildInfo.version}, Build ${BuildInfo.buildInfoBuildNumber}",
      "_links" -> Seq(
        Json.obj("rel" -> "self", "href" -> routes.Application.index().absoluteURL()),
        Json.obj(
          "rel" -> "bricklets",
          "href" -> routes.Bricklets.getBricklets().absoluteURL()
        ),
        Json.obj("rel" -> "door", "href" -> routes.Doors.getDoors().absoluteURL())
      )
    )

    Ok(json)
  }
}
