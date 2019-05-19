import java.io.{BufferedWriter, File, FileWriter, PrintWriter}
import java.nio.file.Files

import OrderSaver.Saved
import akka.actor.{Actor, Props}


class OrderSaver extends Actor {

  def receive = {
    case SaveOrder(title) =>
      val file = new FileWriter(Main.orderFileName, true)
      val pw = new BufferedWriter(file)
      pw.append(title + "\n")
      pw.close()
      sender().tell(Saved, null)
  }
}

object OrderSaver {
  def props: Props = Props[OrderSaver]
  case object Saved
}
