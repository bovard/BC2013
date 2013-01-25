#!/bin/bash
# you must be in team122 for this to work!

# 1.0
git stash 
git checkout 1.0
rm -rdf ../team100
mkdir ../team100
cp -r . ../team100
find ../team100 -name '*.java' -type f -exec  sed -i.bak 's/team122/team100/g' {} +
find ../team100 -name '*.java' -type f -exec  sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 3;/g' {} + 
rm -f ../team100/*.bak


# 2.0
git checkout 2.0
rm -rdf ../team200
mkdir ../team200
cp -r . ../team200/
find ../team200 -name '*.java' -type f -exec sed -i.bak 's/team122/team200/g' {} +
find ../team200 -name '*.java' -type f -exec sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 5;/g' {} +
rm -f ../team200/*.bak
 
# 3.0
git checkout 3.0
rm -rdf ../team300
mkdir ../team300
cp -r . ../team300/
find ../team300 -name '*.java' -type f -exec sed -i.bak 's/team122/team300/g' {} +
find ../team300 -name '*.java' -type f -exec sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 7;/g' {} +
rm -f ../team300/*.bak


# 4.1
git checkout 4.1
rm -rdf ../team410
mkdir ../team410
cp -r . ../team410/
find ../team410 -name '*.java' -type f -exec sed -i.bak 's/team122/team410/g' {} +
find ../team410 -name '*.java' -type f -exec sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 11;/g' {} +
rm -f ../team410/*.bak

# 5.2.2
git checkout 5.2.2
rm -rdf ../team522
mkdir ../team522
cp -r . ../team522/
find ../team522 -name '*.java' -type f -exec sed -i.bak 's/team122/team522/g' {} +
find ../team522 -name '*.java' -type f -exec sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 13;/g' {} +
rm -f ../team522/*.bak

# 6.1
git checkout 6.1
rm -rdf ../team610
mkdir ../team610
cp -r . ../team610/
find ../team610 -name '*.java' -type f -exec sed -i.bak 's/team122/team610/g' {} +
find ../team610 -name '*.java' -type f -exec sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 19;/g' {} +
rm -f ../team610/*.bak

# Back Door Solider!
git checkout BDS
rm -rdf ../teamBDS
mkdir ../teamBDS
cp -r . ../teamBDS/
find ../teamBDS -name '*.java' -type f -exec sed -i.bak 's/team122/teamBDS/g' {} +
find ../teamBDS -name '*.java' -type f -exec sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 19;/g' {} +
rm -f ../teamBDS/*.bak

git checkout master

