def goodCanidate(n):
    strN = str(n)
    
    if "0" in strN: return False
    
    if len(strN) != len(set(strN)): return False
    
    if "0" in str(n * 2): return False
    
    return True

canidates = [ n for n in range(9123, 9999) if goodCanidate(n) ]

for c in reversed(canidates):
    print("checking: %s" % c)
    if len(set(str(c) + str(c * 2))) == 9:
        print("Answer: %s; c: %s" % (str(c) + str(c * 2), c))
        break;

print("Done...")