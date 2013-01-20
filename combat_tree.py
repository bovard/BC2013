
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
    nume = 0
    numa = 0
    numbe = 0
    numba = 0

    # find the number of enemies/allies in the front two rows
    for i in range(0,6):
        if map[i] == 'a':
            numa += 1
        elif map[i] == 'e':
            nume += 1

    # find the number of enimies/alllies in the back row
    for i in range(6,9):
        if map[i] == 'a':
            numba += 1
        elif map[i] == 'e':
            numbe += 1

    # if we have more allies than enemies, move in!
    if 0 < nume < numa + 1:
        score += 100
    # if it's equal... add a bit
    elif nume > 0 and nume == numa + 1:
        score += 10
    # if someone else has already gone in, go in!
    if numbe and numa:
        score += 50
    for i in range(len(map)):
        if map[i] == 'e':
            score -= 6
        elif map[i] == 'a':
            score += 6

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
                                map = i0+'o'+i2+i3+i4+i5+i6+i7+i8
                                if not results.get(map):
                                    results[map] = eval_map(map)



## ================== WRITE HASH FILES ==========================



def init_hash(f, name, type):
    f.write("package team122.combat;\n")
    f.write("import java.util.HashMap;\n")
    f.write("import java.util.Map;\n")
    f.write("\n")
    f.write("public class %s {\n" % name)
    f.write("    public int count = 0;\n")
    f.write("    public Map<String, %s> m = new HashMap<String, %s>();\n" % (type, type))

def write_constructor(f, num_o_inits, name):
    f.write("    public %s() {};\n" % name)
    f.write("    public void load() {;\n")
    f.write("        if (count == 0)\n")
    f.write("            init_0();\n")
    for i in range(1, num_o_inits+1):
        f.write("        else if (count == %s)\n" % i)
        f.write("            init_%s();\n" % i)
    f.write("        count++;\n")
    f.write("    }\n")
    f.write("    public final int TO_LOAD = %s;\n" % num_o_inits)

NUM_LOADS = 77
def write_hash(hash, name, type):

    f = open('combat/%s.java' % name, 'w')
    init_hash(f, name, type)
    i = NUM_LOADS
    num_o_inits = 0
    f.write('    private void init_0() {\n')
    for key in hash.keys():
        i -= 1
        if i <= 0:
            num_o_inits += 1
            i = NUM_LOADS
            f.write('    }')
            f.write('    private void init_%s() {\n' % num_o_inits)
        if type == 'String':
            f.write('        m.put("%s","%s");\n' % (key, hash[key]))
        elif type == 'Integer':
            f.write('        m.put("%s",%s);\n' % (key, hash[key]))

    f.write("    }\n")
    write_constructor(f, num_o_inits, name)
    f.write("}\n")
    f.close()


write_hash(results, "CombatHashMap", "Integer")
#write_hash(rotate_hash, "MapHashMap", "String")

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




