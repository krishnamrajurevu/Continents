package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

import play.api.mvc.Results.Redirect
import play.api.mvc.Results.Status

class AuthenticatedUserAction  @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    val userName = request.headers.get("admin")
    userName match {
      case None => {
        Future.successful(Status(401)("admin header key not found"))
      }
      case Some(password) => { if(password.equalsIgnoreCase("QVANTEL"))  block(request) else Future.successful(Status(401)("Wrong password"))
      }
    }
  }

}