# Bond: Platinum Edition

Bond is a modern approach to messaging written in a modern language.

## Prerequisites (currently osx only)

1. Ensure [leiningen](http://leiningen.org/), nodejs, and npm are installed
2. Download node-webkit [from here](https://github.com/rogerwang/node-webkit#downloads) and place it in your Applications directory.

## Getting Started With Bond

Install patched version of lein-css (In another directory)

    git clone https://github.com/gilbertw1/lein-lesscss
    cd lein-lesscss
    lein install

Compile Less Files

    lein lesscss

Compile Clojurescript

    lein cljsbuild once

Install Node Modules

    npm install

Patch Native Modules

    ./script/nw-gyp-repair.sh

Run Bond

    ./script/bond_cljs


## Development

Auto Compile Clojurescript on change

    lein cljsbuild auto