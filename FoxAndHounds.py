FOX_TURN = 1
HOUNDS_TURN = 0

FOX_WIN = 1
HOUNDS_WIN = 0

BOARD = 0
CHILDREN = 1
MOVE = 2
WINNER = 3

def switchTurn(whoMove):
    if(whoMove == FOX_TURN):
        return HOUNDS_TURN
    else:
        return FOX_TURN
            
def hasHoundsWon(hounds, fox):
    return (avaliableFoxMoves(hounds, fox) == [])

def hasFoxWon(hounds, fox):
    for pos in hounds:
        if pos[0] < fox[0]:
            return False
    
    return True
    
def avaliableFoxMoves(hounds, fox):
    moves = [[fox[0] - 1, fox[1] - 1], [fox[0] - 1, fox[1] + 1],
             [fox[0] + 1, fox[1] - 1], [fox[0] + 1, fox[1] + 1]]
    moves = filter(isValidPosition, moves)
    for pos in hounds:
        if(pos in moves): moves.remove(pos)

    return moves

def avaliableHoundMoves(hound, fox):
    moves = [[hound[0] + 1, hound[1] - 1], [hound[0] + 1, hound[1] + 1]]
    moves = filter(isValidPosition, moves)
    if(fox in moves): moves.remove(fox)

    return moves

def isValidPosition(pos):
    return (pos[0] < 8 and pos[1] < 8 and pos[0] >= 0 and pos[1] >= 0)

def boardHasher(hounds, fox):
    total = 0
    
    hounds.sort()
    
    for pos in hounds:
        total = total << 6 | (pos[0] << 3 | pos[1])

    total = total << 6 | (fox[0] << 3 | fox[1])

    return total

def boardDeHasher(boardHash):
    fox = [(boardHash & 63) >> 3, boardHash & 7]
    boardHash >>= 6

    hounds = []
    for i in range(4):
        hounds.insert(0, [(boardHash & 63) >> 3, boardHash & 7])
        boardHash >>= 6
    
    return hounds, fox

def prettyPrintBoard(boardHash):
    hounds, fox = boardDeHasher(boardHash)
    
    print "The Fox: %s" % fox
    print "The Hounds: %s" % hounds
    
    text = ""
    for a in range(8):
        text += "\n" + "+-"*8
        text += "+\n|"
        for b in range(8):
            if([a, b] in hounds):
                text += "H|"
            elif([a, b] == fox):
                text += "F|"
            elif(a % 2 == b % 2):
                text += ":|"
            else:
                text += " |"
    text += "\n" + "+-"*8 + "+"

    print text

def findWinnerForBoard(parent, board, whoMove, treeMap):
    hounds, fox = boardDeHasher(board)

    if(board in treeMap):
        return  treeMap[board][WINNER]
    elif(hasFoxWon(hounds, fox)):
        treeMap[board] = (parent, [], HOUNDS_TURN, FOX_WIN)
        return  FOX_WIN
    elif(hasHoundsWon(hounds, fox)):
        treeMap[board] = (parent, [], FOX_TURN, HOUNDS_WIN)
        return HOUNDS_WIN
    else:
        children = []

        if(whoMove == FOX_TURN):
            foxMoves = avaliableFoxMoves(hounds, fox)

            for move in foxMoves:
                children.append(boardHasher(hounds, move))
        else:
            for i in range(len(hounds)):
                houndMoves = avaliableHoundMoves(hounds[i], fox)
                houndMoves = filter(lambda x: not x in hounds, houndMoves)
                
                for move in houndMoves:
                    children.append(boardHasher(hounds[:i]+[move]+hounds[i+1:], fox))

        boardWinner = whoMove = switchTurn(whoMove)
        
        tempWinner = None
        for child in children:
            tempWinner = findWinnerForBoard(board, child, whoMove, treeMap)
            
            if(boardWinner != tempWinner):
                boardWinner = tempWinner
                break

        treeMap[board] = (parent, children, whoMove, boardWinner)
        return boardWinner

def findOptimalGameSequence(board, winner, treeMap):
    moveSequence = [board]

    while treeMap[board][CHILDREN] != []:
        for child in treeMap[board][CHILDREN]:
            findWinnerForBoard(board, child, switchTurn(treeMap[board][MOVE]), treeMap)

            for childchild in treeMap[child][CHILDREN]:
                findWinnerForBoard(child, childchild, treeMap[board][MOVE], treeMap)

        win = HOUNDS_WIN
        if treeMap[board][MOVE] == HOUNDS_TURN:
            win = FOX_WIN
        childVals = map(lambda x:
                        [x, map(lambda y: treeMap[y][WINNER], treeMap[x][CHILDREN]).count(win)],
                        treeMap[board][CHILDREN])

        if treeMap[board][MOVE] == HOUNDS_TURN:
            bestChild = max(childVals, key=lambda x: x[1])[0]
        else:
            bestChild = min(map(lambda x: [x[0], depth(x[0],treeMap)], childVals), key=lambda x:x[1])[0]
        
        moveSequence.append(bestChild)
        board = bestChild
    
    return moveSequence
    
hounds = [[5, 0], [5, 2], [5, 4], [0, 7]]
fox =  [7, 0]

boardHash = boardHasher(hounds,fox)
print "Start position:\n"
print prettyPrintBoard(boardHash)
print "~~~"*20

treeMap = {}
winner = findWinnerForBoard(0, boardHash, FOX_TURN, treeMap)

if(winner == FOX_WIN):
    print "The fox will always win\n"
else:
    print "The Hounds will always win\n"

print "Boards Solved: %d" % len(treeMap)
print "Boards Found: %d" % sum(map(lambda x: len(x[CHILDREN]), treeMap.values()))

optSeq = findOptimalGameSequence(boardHash, winner, treeMap)
print "Optimal Sequence: %s" % optSeq

ind = 0
for board in optSeq:
    print "\nBoard: %d" % ind
    prettyPrintBoard(board)

    ind += 1
