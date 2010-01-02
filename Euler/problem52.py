oneToSix = [1, 2, 3, 4, 5, 6]

i = 10**5
while True:
    if len(set(map(lambda x: "".join(sorted(str(i * x))), oneToSix))) == 1:
        print("The answer: %d" % i)
        break
    
    if i % 4001 == 0: print(i)
    i += 1