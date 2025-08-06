package akka

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object Protocol:
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info(" -> " + message.whom)
    message.replyTo ! Protocol.Greeted(message.whom, context.self)
    Behaviors.same
  }

object HellloWorldAkka extends App:
  // crea il sistema
  val system: ActorSystem[Protocol.Greet] = ActorSystem(Protocol(), name = "Hello-System")
  // invia un messaggio
  system ! Protocol.Greet("bella", system.ignoreRef)
  Thread.sleep(2000)
  system.terminate()