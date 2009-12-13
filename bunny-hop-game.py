def orginalIsValidScore(score):
    def helper(tempScore, bellsHit, birdsHit):
        if tempScore == score:
            return (True, bellsHit, birdsHit)

        if tempScore > score:
            return (False, -1, -1)
        
        hitBellScore = tempScore + bellsHit + 1
        result = helper(hitBellScore, bellsHit + 1, birdsHit)

        if result[0] or tempScore == 0: return result
        
        hitBirdScore = tempScore * 2
        result = helper(hitBirdScore, bellsHit, birdsHit + [bellsHit])

        return result

    score /= 10
    
    return helper(0, 0, [])

def isValidScore(score):
    score /= 10
    queue = [(1, 1, [])]
    
    while True:
        temp = queue.pop()
        tmpScore = temp[0]
        bellsHit = temp[1]
        birdsHit = temp[2]

        if tmpScore == score:
            return (True, bellsHit, birdsHit)

        if tmpScore > score:
            if queue == []:
                return (False, -1, -1)
            
            continue

        if birdsHit == [] or bellsHit - birdsHit[-1] > 1:
            hitBirdResults = (tmpScore * 2, bellsHit, birdsHit + [bellsHit])
            queue += [hitBirdResults]
            
        hitBellResults = (tmpScore + bellsHit + 1, bellsHit + 1, birdsHit)
        queue += [hitBellResults]
        

print "score: 420580; result: %s" % str(isValidScore(420580))
print "score: 1251480; result: %s" % str(isValidScore(1251480))
print "score: 35547670; result: %s" % str(isValidScore(35547670))
print "score: 154862840; result: %s" % str(isValidScore(154862840))
print "score: 61958703810; result: %s" % str(isValidScore(61958703810))
print "score: 74759582590; result: %s" % str(isValidScore(74759582590))
