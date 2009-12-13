def gcd(a, b):
    if b == 0: return a;
    
    return gcd(b, a % b);

nToMinK = {}

for n in xrange(11, 999, 2):
    if n % 3 == 0: continue
    if n % 5 == 0: continue

    for k in xrange(1, 10000):
        if (10**k - 1) % n == 0:
            nToMinK[n] = k
            break;
        
    print n

print "Ans: %d" % max(map(lambda x: (x[1], x[0]), nToMinK.items()))[1]