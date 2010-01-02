def isPrime(n):
    if n <  2: return False
    if n == 2: return True
    if n % 2 == 0: return False
    
    i = 3
    while(n / i >= i):
        if(n % i == 0): return False
        i += 2
    
    return True

def testExpression(a, b):
    for n in range(1, 900):
        if not isPrime(n*n + a*n + b):
            return (n, a, b)
    
    return (89, a, b)
    
maxSeq = (0, 0, 0)
primes = [i for i in range(2, 1000) if isPrime(i)]

for b in primes:
    for a in range(-999, 1000, 2):
        seq = testExpression(a, b);
        
        if seq[0] > maxSeq[0]: maxSeq = seq
        
    
    print("on b: %s" % b)

print("maxSeq: %s", maxSeq)