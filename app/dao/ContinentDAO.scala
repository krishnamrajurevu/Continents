package dao

import com.google.inject.ImplementedBy
import model.Continent

import scala.concurrent.Future

@ImplementedBy(classOf[ContinentDAOImpl])
trait ContinentDAO{


  def getAllContinents: Future[Seq[Continent]]

  def addContinent(continent: Continent): Future[String]

  def getContinent(continentId: Int): Future[Option[Continent]]

  def deleteContinent(continentId: Int): Future[Int]
}
