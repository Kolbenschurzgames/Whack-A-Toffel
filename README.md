# Whack-A-Toffel
[![Build Status](https://travis-ci.org/Kolbenschurzgames/Whack-A-Toffel.svg?branch=master)](https://travis-ci.org/Kolbenschurzgames/Whack-A-Toffel)

Next-Gen Retro-Tech-Festival style mobile game where you hit, squash, squeeze the tiny but resilient Toffel.

## Backend Deployment

From the root directory:

    git subtree push --prefix server heroku master
    
In case you need to force push for some reason:

    git push heroku `git subtree split --prefix server master`:master --force
