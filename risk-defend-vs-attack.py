lostDefends = 0
lostAttacks = 0

for a1 in xrange(6):
    for a2 in xrange(6):
        for a3 in xrange(6):
            for d1 in xrange(6):
                for d2 in xrange(6):
                    attacks = sorted([a1, a2, a3])[::-1]
                    defends = sorted([d1, d2])[::-1]
                    
                    for i in xrange(2):
                        if attacks[i] > defends[i]:
                            lostDefends += 1
                        else:
                            lostAttacks += 1
                            
print "Lost Defernders: %d" % lostDefends
print "Lost Attackers: %d" % lostAttacks