import akka.actor.ActorSystem

class Main {

}
object Main {
  val actorSystemName = "Bookstore_system"
  val orderFileName = "orders"

  def main(args: Array[String]): Unit = {
    println("My first main since 2 months")
    val actorSystem = ActorSystem(actorSystemName)
    val client = actorSystem.actorOf(Client.props)
    client.tell(Client.Start, null)
  }
}
