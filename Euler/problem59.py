badChars = "<>+`$*`~@"

def goodDecode(decoded):
    for b in badChars:
        if b in decoded: return False
    
    return True


file = open("C:\\Documents and Settings\\Administrator\\Desktop\\random\\Euler\\cipher1.txt", "r")
encrypt = list(map(lambda x: int(x), file.read()[:-1].split(",")))
file.close()

wfile = open("C:\\Documents and Settings\\Administrator\\Desktop\\random\\Euler\\cipher-out.txt", "w")

testSet = encrypt[:15]
for c1 in range(97, 97 + 26):
    for c2 in range(97, 97 + 26):
        for c3 in range(97, 97 + 26):
            ciphers = (c1, c2, c3)
            decoded = "".join([ chr(testSet[i] ^ ciphers[i % 3]) for i in range(len(testSet)) ])
            
            if not goodDecode(decoded): continue
            
            wfile.write("c: %s, result: %s\n" % (list(map(chr, ciphers)), decoded))
            print(list(map(chr, ciphers)))

wfile.close()


# Answer is god
cipher = (ord("g"), ord("o"), ord("d"))
print("Answer: %s", sum([ (encrypt[i] ^ cipher[i % 3]) for i in range(len(encrypt)) ]))
print("Answer: %s", "".join([ chr(encrypt[i] ^ cipher[i % 3]) for i in range(len(encrypt)) ]))