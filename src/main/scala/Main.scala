import java.io.File

import Client.Start
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

class Main {

}
object Main {
  val bookstoreSystemName = "Bookstore_system"
  val clientSystemName = "Client_system"
  val orderFileName = "orders"

  def main(args: Array[String]): Unit = {
    println("My first main since 2 months")
    val configServer = ConfigFactory.parseFile(new File("application.conf"))
    val configClient = ConfigFactory.parseFile(new File("local_app.conf"))

    val bookstoreSystem = ActorSystem(bookstoreSystemName, configServer)
    val server = bookstoreSystem.actorOf(Dispatcher.props, "dispatcher")

    val clientSystem = ActorSystem(clientSystemName, configClient)
    val client = clientSystem.actorOf(Client.props)
    client.tell(Start, null)
  }
}
