words = open("C:\Documents and Settings\Administrator\My Documents\Crap\Word Lists\CROSSWD.TXT", "r").readlines()
words = [ word.strip() for word in words if len(word) > 3 ]
letterFreq = {}

for word in words:
    for letter in word:
        if letter not in letterFreq: letterFreq[letter] = 0
        
        letterFreq[letter] += 1

print(sorted([(letter, freq) for letter, freq in letterFreq.items()], key=lambda x: -x[1]))

