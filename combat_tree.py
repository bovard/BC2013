
"""
0*2
345
678

open = o
enemy = e
ally = a
blocked = b
"""

results = {}

rotate_hash = {}


def rotate_map(map):
    """
    Rotates a map
    0*2
    345
    678

    0->6
    1->3
    2->0
    5->1
    8->2
    7->5
    6->8
    3->7
    4->4
    """
    new = ''
    for i in [2,5,8,1,4,7,0,3,6]:
        new += map[i]
    return new

def in_results(map):
    r1 = rotate_map(map)
    r2 = rotate_map(r1)
    r3 = rotate_map(r2)

    if results.get(map):
        return True
    if results.get(r1):
        return True
    if results.get(r2):
        return True
    if results.get(r3):
        return True

    # if we haven't hit this yet, add the rotates the to rotate_hash
    rotate_hash[r1] = map
    rotate_hash[r2] = map
    rotate_hash[r3] = map

    return False



def eval_map(map):
    score = 0
    if 'e' in map:
        if 'e' in map[0:5]:
            score += 1
        for i in range(len(map)):
            if map[i] == 'e':
                score -= 1
            elif map[i] == 'a':
                score += 1
    return score



possibles = ['e', 'a', 'o']

for i0 in possibles:
    for i2 in possibles:
        for i3 in possibles:
            for i4 in possibles:
                for i5 in possibles:
                    for i6 in possibles:
                        for i7 in possibles:
                            for i8 in possibles:
                                map = i0+'*'+i2+i3+i4+i5+i6+i7+i8
                                if not in_results(map):
                                    results[map] = eval_map(map)



## ================== WRITE HASH FILES ==========================

def init_hash(f, name):
    f.write("import java.util.HashMap;\n")
    f.write("import java.util.Map;\n")
    f.write("\n")
    f.write("public class %s {\n" % name)
    f.write("    private static final Map<String, Integer> myMap = new HashMap<Integer, String>();\n")
    f.write("    static {\n")



f = open('behavior/CombatHashMap.java', 'w')
print("making hash")
init_hash(f, 'CombatHashMap')
for key in results.keys():
    f.write("        myMap.put(%s,%s);\n" % (key, results[key]))

f.write("    }\n")
f.write("}\n")
f.close()


f = open('behavior/MapHashMap.java', 'w')
print("making hash")
init_hash(f, 'MapHashMap')
for key in rotate_hash.keys():
    f.write("        myMap.put(%s,%s);\n" % (key, rotate_hash[key]))

f.write("    }\n")
f.write("}\n")
f.close()

def something():
    """
    import java.util.HashMap;
    import java.util.Map;

    public class Test {
        private static final Map<Integer, String> myMap = new HashMap<Integer, String>();
        static {
            myMap.put(1, "one");
            myMap.put(2, "two");
        }

        private static final Map<Integer, String> myMap2 = new HashMap<Integer, String>(){
            {
                put(1, "one");
                put(2, "two");
            }
        };
    }
    """
    pass



# MAP GENERATIONS =========================

# for a given map,

# rotate it and see if any of the rotations are already done

# if not,


# MAP ITERATIONS ======================

def get_moves(pos, map):
    """
    figures out if a robot at a given position can move

    012
    345
    678

    open = o
    enemy = e
    ally = a
    blocked = b

    @return a list of the position it can move to
    """
    can_move = []

    if pos == 0:
        for i in [1,3]:
            if map[i] == 'o':
                can_move.append(i)
    elif pos == 1:
        for i in [0,3,2,5]:
            if map[i] == 'o':
                can_move.append(i)
    elif pos == 2:
        for i in [1,5]:
            if map[i] == 'o':
                can_move.append(i)
    elif pos == 3:
        for i in [0,1,6,7]:
            if map[i] == 'o':
                can_move.append(i)
    elif pos == 5:
        for i in [1,2,7,8]:
            if map[i] == 'o':
                can_move.append(i)
    elif pos == 6:
        for i in [3,7]:
            if map[i] == 'o':
                can_move.append(i)
    elif pos == 7:
        for i in [3,6,5,8]:
            if map[i] == 'o':
                can_move.append(i)
    elif pos == 8:
        for i in [5,7]:
            if map[i] == 'o':
                can_move.append(i)


# generate all possible move orders (6! = 720)
def generate_move_orders(map):
    can_move = []
    for i in range(0, 8):
        if map[i] == 'e' or map[i] == 'a':
            can_move.append(i)








# kick off each state one at a time

# collect the hp results, take the average



# STATE ITERATIONS:

# for a given map, move_order, hp_count

# pull the first off the move_order
# generate a new state for each move, adding/subtracting to the hp count




