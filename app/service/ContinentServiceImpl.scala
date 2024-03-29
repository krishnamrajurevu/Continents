package service
import dao.ContinentDAO
import javax.inject.Inject
import model.Continent

import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.concurrent.duration._

class ContinentServiceImpl @Inject()(continentDAO : ContinentDAO,countryService: CountryService) extends ContinentService {


  override def getAllContinents: Future[Seq[Continent]] = continentDAO.getAllContinents

  override def addContinent(continentName: String) : Option[String]= {

    val continents = Await.result(continentDAO.getAllContinents, 5.seconds)
    println("continents ::"+continents)
    continents.find(_.continentName == continentName) match {
      case Some(_) => Some("Continent already existed")
      case None => val continent = Continent(continents.maxByOption(_.continentId).map(_.continentId + 1).getOrElse(1), continentName)
        val res: Future[String] =continentDAO.addContinent(continent)
        println("res ::"+res)
        Some("Continent created successfully")
    }
  }

  override def deleteContinent(continentId: Int): Option[String] = {
    val continents = ListBuffer.empty ++= Await.result(continentDAO.getAllContinents, 5.seconds)
    continents.find(_.continentId == continentId).fold[Option[String]](None) { value =>
      val result: Option[String] = countryService.removeCountriesByContinent(continentId)
      val message = result match {
        case Some(value) => ", " + value + " of continents"
        case None => ""
      }

      continents.filterInPlace(_.continentId != continentId)
      continentDAO.deleteContinent(continentId)
      Some(s"Continent $message successfully deleted")
    }

  }
}
