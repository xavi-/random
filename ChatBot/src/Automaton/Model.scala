package Automaton

class Model(rule: (String => String), seed: String) {
  var current = seed
  
  def increment(): String = {
    var paddedCurrent = "  " + current + "   "
    var newCurrent = ""
    
    for(i <- 0 to paddedCurrent.length - 4) {
      newCurrent += rule(paddedCurrent.substring(i, i + 3))
    }
    
    current = newCurrent
    
    return newCurrent
  }  
}