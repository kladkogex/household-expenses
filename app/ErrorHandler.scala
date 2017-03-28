import com.google.inject.{Inject, Provider, Singleton}
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper}

import scala.concurrent.Future

/**
  * Standard error handler for the application
  * @param env            Hosting environment
  * @param configuration  Configuration for the app
  * @param sourceMapper   Source mapper
  * @param router         Router
  */
@Singleton
class ErrorHandler @Inject()(env: Environment,
                             configuration: Configuration,
                             sourceMapper: OptionalSourceMapper,
                             router: Provider[Router])
  extends DefaultHttpErrorHandler(env, configuration, sourceMapper, router) {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  /**
    * Displays a standard 404 page when a URL doesn't resolve to something useful.
    *
    * @param request Incoming request
    * @param message Message to display
    * @return Returns the outcome of the error handler
    */
  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future {
      NotFound(views.html.errors.pageNotFound(request.uri))
    }
  }
}
