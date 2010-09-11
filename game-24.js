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
    var ops = [ function multi(stack) { return stack.pop.pop.push(stack.pop.peek * stack.peek); }
              , function add(stack) { return stack.pop.pop.push(stack.pop.peek + stack.peek); }
              , function sub(stack) { return stack.pop.pop.push(stack.pop.peek - stack.peek); }
              , function div(stack) { return stack.pop.pop.push(stack.pop.peek / stack.peek); } ];
    
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
    
    return function findCombinations(numbers) {
        return recurseFind(new Stack(), new Stack(numbers), new Stack());
    }
})();


var lookup = [], num = "";
for(var i = 0; i < 1e4; i++) {
    if(i < 1) { num = "AAAA"; }
    else if(i < 10) { num = "AAA" + i; }
    else if(i < 100) { num = "AA" + i; }
    else if(i < 1000) { num = "A" + i; }
    else { num = "" + i; }
    
    num = num.replace("0", "A").split("").map(function(n) { return parseInt(n, 11); });
    lookup.push(makeCombinations(num)
                    .filter(function(c) { return c.peek === 24; })
                    .map(function(c) { return c.toString(); }));
    
    if(i % 234 === 0) { console.log(i); }
}

exports.lookup = lookup;