domains = map(lambda x: x.split()[0][1:].lower(),
              open("/top-level-domains.txt").readlines())
words = map(lambda x: x.strip().lower().replace(" ", "-"),
            open("/Word Lists/COMMON.TXT").readlines())
hacks = set()
domainFeq = {}.fromkeys(domains, 0)

print "Finding URL hacks..."
for word in words:
    if len(word) < 5: continue
    
    if word[-2:] in domains:
        hackedUrl = (word[:-2] + "." + word[-2:]).replace("-.", ".")
        hacks.add(hackedUrl)

        domainFeq[word[-2:]] += 1
            
    if len(word) < 6: continue
    
    if word[-3:] in domains:
        hackedUrl = (word[:-3] + "." + word[-3:]).replace("-.", ".")
        hacks.add(hackedUrl)
        
        domainFeq[word[-3:]] += 1

print "Writing file..."
file = open("hacked-urls.txt", 'w')
for hack in sorted(hacks): file.write(hack + "\n")
file.close()
print "File written."


print sorted(filter(lambda x: x[1] != 0, domainFeq.items()),
             key=lambda x: x[1], reverse = True)
