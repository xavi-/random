package Automaton

object AutomatonMain {
  def ruleMaker(num: Int, pattern: String): String = 
  pattern match {
    case "   " => if(((num >> 0) & 1) == 1) "X" else " "
    case "  X" => if(((num >> 1) & 1) == 1) "X" else " "
    case " X " => if(((num >> 2) & 1) == 1) "X" else " "
    case " XX" => if(((num >> 3) & 1) == 1) "X" else " "
    case "X  " => if(((num >> 4) & 1) == 1) "X" else " "
    case "X X" => if(((num >> 5) & 1) == 1) "X" else " "
    case "XX " => if(((num >> 6) & 1) == 1) "X" else " "
    case "XXX" => if(((num >> 7) & 1) == 1) "X" else " "
    case _ => pattern
  }

  def main(args: Array[String]) {
    for(n <- 0 until 50) {
      var rule = ruleMaker(n, _: String)
	  var autom = new Model(rule, "X")
      println("Starting: " + n)
	  println(autom.current)
	  for(i <- 0 until 100) {
	    println(autom.increment)
	  }
	  println("Done: "  + n)
    }
  }
}