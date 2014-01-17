This is a calculator application written in Scala, using actor system. This
uses akka as the actor system framework.

## Usage

To calculate `(1 + 2) * (3 - 4) * (5 + 6) * (7 - 8 - 9)`

    $ sbt "run (* (+ 1 2) (- 3 4) (+ 5 6) (- 7 8 9))"

akka-calculator will (1) parse the expression sequentially, (2) spawns calculate worker actors, (3) let them calculate small pieces with dumping their progress, (4) shows the final result, and (5) quit the application.

## Author

Tatsuhiro Ujihisa

## License

GPLv3 or any later version
