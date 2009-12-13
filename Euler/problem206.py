mask = ''.join([str(n) for n in range(1, 10)]) + "0"

n = 1010101030
while n < 1389026620:
    if str(n**2)[::2] == mask:
        print "The answer: %d" % n
        break;
    
    if n % 100 == 30:
        n += 40
        continue
    n += 60
    
    if n % 59930 == 0: print n