package service

import com.google.inject.ImplementedBy
import model.Continent

import scala.concurrent.Future

@ImplementedBy(classOf[ContinentServiceImpl])
trait ContinentService {

  def getAllContinents: Future[Seq[Continent]]

  def addContinent(continentName: String) : Option[Continent]

  def deleteContinent(continentId: Int) : Option[String]
}
