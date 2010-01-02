def isPrime(n):
    if n == 2: return True
    if n % 2 == 0: return False
    
    i = 3
    while(n / i >= i):
        if(n % i == 0): return False
        i += 2
    
    return True

primes = [ n for n in range(2, 1000000) if isPrime(n) ]
def factor(n):
    fact = set()
    rem = n
    
    for p in primes:
        while(rem % p == 0):
            rem /= p
            fact.add(p)
        
        if rem == 1: break;
    
    return fact


n = 210
conseq = 0
while True:
    if len(factor(n)) == 4:
        conseq += 1
    else:
        conseq = 0
    
    if conseq == 4: break;
    n += 1
    
    if n % 1001 == 1: print("n: %s" % n)

print("Answer: %s, %s, %s, %s" % (n - 3, n - 2, n - 1, n))