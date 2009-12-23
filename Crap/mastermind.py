import random

colors = ['a', 'b', 'c', 'd', 'e']
answer = "bdcd" #''.join([random.choice(colors) for i in range(4)])

tries = 0

while(1):
    tries += 1
    guess = raw_input("Guess: ")
    
    if answer == guess: break;
    
    guess = guess.ljust(len(answer))
    
    if len(guess) > len(answer): print "Your guess is too long.  I'm only looking '%s'" % guess[:4]
    
    guess = guess[:4]
    tempAns = answer
    
    correctPosition = 0
    for i in range(4):
        if tempAns[i] == guess[i]:
            correctPosition += 1
            tempAns = tempAns.replace(tempAns[i], " ", 1)
            guess = guess.replace(answer[i], " ", 1)
            
    correctColors = 0    
    for i in range(4):
        if tempAns[i] == " ": continue
        
        if tempAns[i] != guess[i] and tempAns[i] in guess:
            correctColors += 1
            guess = guess.replace(tempAns[i], " ", 1)
            tempAns = tempAns.replace(tempAns[i], " ", 1)
        
    print "Correct Colors: %s; Correct Positions: %s" % (correctColors, correctPosition)

print "Congrats!!! you guessed the right answer!!"
print "Answer: %s" % answer
print "Tries: %d" % tries