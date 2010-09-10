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

Stack.empty = (function() {
    var empty = new Stack(null, {});
    empty.pop = empty;
    empty.isEmpty = true;
    
    return empty;
})();

var makeCombinations = (function() {
    var ops = (function() {
        var rawOps = [ function multi(a, b) { return a * b; }
                     , function add(a, b) { return a + b; }
                     , function sub(a, b) { return a - b; }
                     , function div(a, b) { return a / b; } ];
                     
        return rawOps.map(function(op) {
            var newOp = function(stack) {
                var a = stack.peek, b = stack.pop.peek;
                
                return stack.pop.pop.push(op(a, b));
            };
            
            newOp.opName = op.name;
            
            return newOp;
        });
    })();
    
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
            Array.prototype.push.apply(paths, recurseFind(op(stack), numbers, path.push(op.opName)));
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

exports.Stack = Stack;
exports.makeCombinations = makeCombinations;