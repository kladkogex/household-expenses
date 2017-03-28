package controllers

import java.io.File
import java.nio.file.Paths
import java.util.UUID

import actors.TransactionImporterActor
import actors.TransactionImporterActor.{ImportFailure, ImportResults}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import play.api.Configuration
import play.api.mvc.{Action, Controller, RequestHeader, Result}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Provides access to the import functionality
  *
  * @param actorSystem Actor system for running the import actor
  * @param config      Configuration needed for the upload folder
  */
class FileImport @Inject()(actorSystem: ActorSystem, config: Configuration) extends Controller {
  private val importer = actorSystem.actorOf(TransactionImporterActor.props, "transactions-importer")

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  /**
    * Displays the import form
    *
    * @return
    */
  def start = Action { implicit request =>
    Ok(views.html.fileimport.index(Seq(),Seq()))
  }

  /**
    * Completes the import operation
    *
    * @return
    */
  def complete = Action.async(parse.multipartFormData) { implicit request =>
    implicit val timeout: Timeout = 3 seconds

    val tempFolder = config.getString("upload.tempfolder").getOrElse("/tmp")

    val missingImportFile: Future[Result] = {
      Future.successful(BadRequest(views.html.fileimport.index(Seq(),Seq("Import file is niet gevonden"))))
    }

    request.body.file("importFile").fold(missingImportFile) { importFile =>
      if (!importFile.filename.endsWith(".csv")) {
        Future.successful(BadRequest("Invalid file extension detected"))
      } else {
        // Make sure to drop the file on disk so the antivirus can have a look at it.
        val savedFile = importFile.ref.moveTo(
          new File(Paths.get(tempFolder, UUID.randomUUID().toString() + ".csv").toString),
          replace = true)

        (importer ? savedFile).flatMap {
          case error: ImportFailure => Future.successful(
            BadRequest(views.html.fileimport.index(Seq(), Seq(error.message))))

          case results: ImportResults => Future.successful(
            Ok(views.html.fileimport.index(Seq(s"Het bestand is geimporteerd, " +
              s"${results.numberOfTransactions} verwerkt."),Seq())))
        }
      }
    }


  }
}
