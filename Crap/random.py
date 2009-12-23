import random


def rand3():
    if random.random() < .5:
        if random.random() < .5:
            return 1
        else:
            return 2
    else:
        if random.random() < .5:
            return 3
        else:
            return rand3();


results = { 3: 0, 2: 0, 1: 0 }
for i in xrange(10000000):
    results[rand3()] += 1

print results
