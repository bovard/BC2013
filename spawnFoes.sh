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


# 2.0
#git checkout 2.0
#rm -rdf ../team200
#mkdir ../team200
#cp -r . ../team200/
#find ../team200 -type f -print0 | xargs -0 sed -i.bak 's/team122/team200/g'
#find ../team200 -type f -print0 | xargs -0 sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 5;/g'

# 3.0
#git checkout 3.0
#rm -rdf ../team300
#mkdir ../team300
#cp -r . ../team300/
#find ../team300 -type f -print0 | xargs -0 sed -i.bak 's/team122/team300/g'
#find ../team300 -type f -print0 | xargs -0 sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 7;/g'


# 4.1
#git checkout 4.1
#rm -rdf ../team410
#mkdir ../team410
#cp -r . ../team410/
#find ../team410 -type f -print0 | xargs -0 sed -i.bak 's/team122/team410/g'
#find ../team410 -type f -print0 | xargs -0 sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 11;/g'

# 5.2.2
#git checkout 5.2.2
#rm -rdf ../team522
#mkdir ../team522
#cp -r . ../team522/
#find ../team522 -type f -print0 | xargs -0 sed -i.bak 's/team122/team522/g'
#find ../team522 -type f -print0 | xargs -0 sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 13;/g'

# 6.1
#git checkout 6.1
#rm -rdf ../team610
#mkdir ../team610
#cp -r . ../team610/
#find ../team610 -type f -print0 | xargs -0 sed -i.bak 's/team122/team610/g'
#find ../team610 -type f -print0 | xargs -0 sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 19;/g'

