package com.knoldus.akka
import akka.pattern.ask
import akka.actor.{Props, ActorSystem, Actor}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by knoldus on 16/3/16.
  */

object Parking  {

  val system = ActorSystem("Parking")
  val user = system.actorOf(Props[User],"user")
  val attendant = system.actorOf(Props[Attendant],"attendant")
  val monitor = system.actorOf(Props[Monitor],"monitor")

  def main(args: Array[String]) {

    user ! "locate"
    user ! "locate"
    user ! "locate"
    user ! "2"
    user ! "locate"
    user ! "locate"
    user ! "0"
    user ! "4"
    user ! "locate"
    user ! "locate"
    user ! "locate"


  }

}

class User extends Actor{
  override def receive: Receive = {

    case "locate" => Parking.attendant ! "area locate "
    case loc => Parking.attendant ! loc

  }
}

class Attendant extends Actor{
  override def receive: Actor.Receive = {
    case "area locate " => {
      val result = Await.result((Parking.monitor ? "allocate space") (Timeout(5 seconds)).mapTo[Int], 10.second)
      println("seat allocated:"+result)
    }

    case loc => {
      val result = Await.result((Parking.monitor ? loc ) (Timeout(5 seconds)).mapTo[Boolean], 10.second)
      println("seat deallocated:"+result)
    }
    //case loc => Parking.monitor ! loc
  }


}

class Monitor extends  Actor{

  val area:Array[Boolean] = Array(false,false,false,false,false,false,false,false,false,false)

  def setArea():Int ={

    val ticket =  area.indexOf(false)
    if(ticket >=0)
    {
      area(ticket) = true

    }
    ticket

  }

  def freeArea(id:String):Boolean={

    val iid = id.toInt
    area(iid) = false
    true


  }


  override def receive: Actor.Receive = {
    case "allocate space" => sender ! setArea()
    case loc => sender ! freeArea(loc.toString)

  }}