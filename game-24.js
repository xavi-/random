/* Results
// basic: + - / *
  150/715
// basic, pow 
  128/715
// basic, mod
  110/715
// basic, min/max
  111/715
// basic, pow, min/max
  103/715
// basic, mod, min/max
  105/715
// with basic, pow, mod
  95/715
// basic, pow, mod, min/max
  90/715
// boolean: & ^ |
  715/715
// boolean, pow:
  640/715
// boolean, mod:
  715/715
// boolean, min/max:
  715/715
// boolean, pow, mod:
  602/715
// boolean, pow, min/max
  638/715
// boolean, mod, min/max
  715/715
// boolean, pow, mod, min/max
  602/715
// with basic, boolean
  56/715
// with basic, pow, boolean
  49/715
// with basic, mod, boolean
  50/715
// basic, pow, min/max, boolean
  46/715
// basic, mod, min/max, boolean
  49/715
// with basic, pow, mod, boolean
  45/715
// with basic, pow, mod, boolean, min/max
  44/715
  

// with pow
  715/715
// with mod
  715/715
// with min/max
  715/715

// with pow, mod
  696/715
// with pow, mod, min/max
  696/715
*/

function Stack(head, tail) {
    if(arguments.length === 0) { return Stack.empty; }
    
    if(arguments.length === 1 && head.length) { // first parameter is an array
        var st = Stack.empty;
        
        head.forEach(function(item) { st = st.push(item); });
        
        return st;
    }
    
    this.peek = head;
    
    this.pop = tail || Stack.empty;
}

Stack.prototype.push = function(val) {
    return new Stack(val, this);
}

Stack.prototype.toString = function() {
    var temp = this, arr = [];
    
    while(!temp.isEmpty) { arr.push(temp.peek); temp = temp.pop; }
    
    return arr.join();
}

Stack.empty = (function() {
    var empty = new Stack(null, {});
    empty.pop = empty;
    empty.isEmpty = true;
    
    return empty;
})();

var makeCombinations = (function() {
    var ops;
    
    function recurseFind(stack, numbers, path) {
        if(stack.pop.isEmpty && numbers.isEmpty) { return [ path.push(stack.peek) ]; }
        
        if(stack.pop.isEmpty) {
            stack = stack.push(numbers.peek);
            path = path.push(numbers.peek);
            numbers = numbers.pop;
            
            return recurseFind(stack, numbers, path);
        }
        
        var paths = [];

        ops.forEach(function(op) {
            Array.prototype.push.apply(paths, recurseFind(op(stack), numbers, path.push(op.name)));
        });
        if(numbers.isEmpty) { return paths; }
        
        stack = stack.push(numbers.peek);
        path = path.push(numbers.peek);
        numbers = numbers.pop;
        Array.prototype.push.apply(paths, recurseFind(stack, numbers, path));
        
        return paths;
    }
    
    return function findCombinations(numbers, operations) {
        ops = operations || repl.context.all;
        return recurseFind(new Stack(), new Stack(numbers), new Stack());
    }
})();

function createLookup(ops) {
    var list = [], num = "";
    for(var i = 0; i < 1e4; i++) {
        if(i < 1) { num = "AAAA"; }
        else if(i < 10) { num = "AAA" + i; }
        else if(i < 100) { num = "AA" + i; }
        else if(i < 1000) { num = "A" + i; }
        else { num = "" + i; }
    
        num = num.replace(/0/g, "A").split("").map(function(n) { return parseInt(n, 11); });
        list.push(makeCombinations(num, ops)
                        .filter(function(c) { return c.peek === 24; })
                        .map(function(c) { return c.toString(); }));
    
        if(i % 234 === 0) { console.log(i); }
    }

    console.log("creating look up table...");
    var lookup = {}, num = "";
    for(var i = 0; i < 1e4; i++) {
        if(i < 1) { num = "0000"; }
        else if(i < 10) { num = "000" + i; }
        else if(i < 100) { num = "00" + i; }
        else if(i < 1000) { num = "0" + i; }
        else { num = "" + i; }
    
        num = num.split("").sort().join("");
    
        if(!lookup[num]) { lookup[num] = []; }
        Array.prototype.push.apply(lookup[num], list[i]);
    
        if(i % 1234 === 0) { console.log(i); }
    }
    
    return { list: list, lookup: lookup };
}


function multi(stack) { return stack.pop.pop.push(stack.pop.peek * stack.peek); }
function add(stack) { return stack.pop.pop.push(stack.pop.peek + stack.peek); }
function sub(stack) { return stack.pop.pop.push(stack.pop.peek - stack.peek); }
function div(stack) { return stack.pop.pop.push(stack.pop.peek / stack.peek); }
function and(stack) { return stack.pop.pop.push(stack.pop.peek & stack.peek); }
function or(stack) { return stack.pop.pop.push(stack.pop.peek | stack.peek); }
function xor(stack) { return stack.pop.pop.push(stack.pop.peek ^ stack.peek); }
function mod(stack) { return stack.pop.pop.push(stack.pop.peek % stack.peek); }
function pow(stack) { return stack.pop.pop.push(Math.pow(stack.pop.peek, stack.peek)); }
function min(stack) { return stack.pop.pop.push(Math.min(stack.pop.peek, stack.peek)); }
function max(stack) { return stack.pop.pop.push(Math.max(stack.pop.peek, stack.peek)); }

var basic = [ add, sub, multi, div ];
var bool = [ and, or, xor ];
var minMax = [ min, max ];
var all = basic.concat(mod, pow, bool, minMax);

var results = createLookup(basic);

var repl = require("repl").start("> ");

all.forEach(function(op) { repl.context[op.name] = op; });

repl.context.basic = basic;
repl.context.bool = bool;
repl.context.minMax = minMax;
repl.context.all = all;

repl.context.results = results;
repl.context.recalculate = function() {
    var ops = [];
    
    ops = Array.prototype.concat.apply(ops, arguments);
    
    result = createLookup(ops);
    
    repl.context.results.list = result.list;
    repl.context.results.lookup = result.lookup;
};