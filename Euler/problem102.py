def makeVector(p1, p2):
    return ((p2[0] - p1[0], p2[1] - p1[1]))

# Is the point contained in the vector contained in the parallelgram formed
# by the vecA and vecB.  The parallelgram is position as posVec
def containsPoint(point, posVec, vecA, vecB):
    weight = [0, 0]
    det = vecA[0] * vecB[1] - vecA[1] * vecB[0]
    
    weight[0] = (point[0] - posVec[0]) * vecB[1] + (point[1] - posVec[1]) * -vecB[0]
    weight[1] = (point[0] - posVec[0]) * -vecA[1] + (point[1] - posVec[1]) * vecA[0]
    
    weight[0] /= det
    weight[1] /= det
    
    return 0 <= weight[0] <= 1 and 0 <= weight[1] <= 1

def strToTriangle(string):
    nums = map(int, string.split(","));
    return zip(nums[::2], nums[1::2])
    
    
triangles = map(lambda x: strToTriangle(x.strip("\n")), 
                open("triangles.txt", 'r').readlines())

goodTriangles = 0
for tri in triangles:
    vecA = makeVector(tri[0], tri[1]);
    vecB = makeVector(tri[0], tri[2]);
    
    if not containsPoint((0, 0), tri[0], vecA, vecB): continue
    
    vecA = makeVector(tri[1], tri[0]);
    vecB = makeVector(tri[1], tri[2]);
    
    if not containsPoint((0, 0), tri[1], vecA, vecB): continue
    
    goodTriangles += 1
    
print goodTriangles