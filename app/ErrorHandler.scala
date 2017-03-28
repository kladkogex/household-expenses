import com.google.inject.{Inject, Provider, Singleton}
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper}
import play.api.mvc.Results._

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject() (env: Environment,
                              configuration: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router])
  extends DefaultHttpErrorHandler(env, configuration, sourceMapper, router) {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  
  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future {
      NotFound(views.html.errors.pageNotFound(request.uri))
    }
  }
}
