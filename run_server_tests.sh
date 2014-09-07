./gradlew :server:npmInstall

npm install -g mocha

./gradlew :server:npm_test

NODE_ENV=test DEBUG=toffel* mocha --harmony src/test/integration