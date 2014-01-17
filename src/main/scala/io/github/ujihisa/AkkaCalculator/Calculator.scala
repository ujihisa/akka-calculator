package io.github.ujihisa.AkkaCalculator

import akka.actor.{Actor, ActorRef, Props}
import scala.concurrent.Promise
import io.github.ujihisa.AkkaCalculator.AkkaCalculator.{Eval, Expr, ImmediateExpr, PlusExpr, MinusExpr, PrintInst, WaitPairInst}

object Calculator {
  def props() = Props(new Calculator())
}

class Calculator extends Actor {
  val routerRef = context.parent
  // TODO check if the routerRef is actually a router to Calculator

  private var uniqueLabelIdx = 0
  def generateUniqueLabel() = {
    uniqueLabelIdx += 1
    s"${self.path.name}/${uniqueLabelIdx}"
  }

  var lookingForPair = Map[String, Long]()
  def receive = {
    // case SetCalculatorRouter(newCalculatorRouter) =>
    //   calculatorRouter.success(newCalculatorRouter)
    //   calculatorRouter map println
    case Eval(e, inst) => (e, inst) match {
      case (ImmediateExpr(a), PrintInst) =>
        println(self.path.name, 'print, a)
      case (ImmediateExpr(a), WaitPairInst(label, inst)) =>
        lookingForPair.get(label) match {
          case Some(another) =>
            println(self.path.name, 'immediate, 'found, a, label)
            routerRef ! Eval(ImmediateExpr(a + another), inst)
          case None =>
            println(self.path.name, 'immediate, 'wait, a, label)
            lookingForPair += label -> a
        }
      case (PlusExpr(a, b), inst) =>
        val uniqueLabel = generateUniqueLabel()
        println(self.path.name, 'plus, a, b, uniqueLabel)
        routerRef ! Eval(a, WaitPairInst(uniqueLabel, inst))
        routerRef ! Eval(b, WaitPairInst(uniqueLabel, inst))
      case (MinusExpr(a, b), inst) =>
        val uniqueLabel = generateUniqueLabel()
        println(self.path.name, 'minus, a, b, uniqueLabel)
        routerRef ! Eval(a, WaitPairInst(uniqueLabel, inst))
        routerRef ! Eval(b, WaitPairInst(uniqueLabel, inst))
    }
    case e => println('omg, e)
  }
}
