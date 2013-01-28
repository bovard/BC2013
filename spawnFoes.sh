#!/bin/bash
# you must be in team122 for this to work!

# 6.1
git checkout 6.1
rm -rdf ../team610
mkdir ../team610
cp -r . ../team610/
find ../team610 -name '*.java' -type f -exec sed -i.bak 's/team122/team610/g' {} +
find ../team610 -name '*.java' -type f -exec sed -i.bak 's/SEED_MULTIPLIER = 17;/SEED_MULTIPLIER = 19;/g' {} +
rm -f ../team610/*.bak

git checkout master

