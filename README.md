## Development React Native
To get started with mobile development you will need to make yourself familiar with the tool. A good brief introduction can be found in [React Native Getting Started Docs](https://facebook.github.io/react-native/docs/getting-started) under *React Native CLI Quickstart*

For a better understandig of build process you will need to get familiar with the library:
[CLJ-RN](https://github.com/status-im/clj-rn)


### React Native Metro
The React Native Metro Bundler has to be started manually before launching the app in dev mode
```
$ react-native start
```

### In case of cache problems with React Native
Run this script to start react-native metro bundler on clean cache
```
$ yarn run clear-cache
```

### Example of running android app
```
$ clj -R:repl build.clj figwheel -p android -a [avd|real|genymotion]
```

If all went well you should see the REPL prompt and changes in source files should be hot-loaded by Figwheel.

Now you need to run react-native and app will open on the device

```
$ yarn run android
```

Due to a known bug in react-native you should open the debugger link manually in browser
[http://localhost:8081/debugger-ui/](http://localhost:8081/debugger-ui/)

### Example of running ios app
```
$ clj -R:repl build.clj figwheel -p ios -i [simulator|real]
```
Now you need to run react-native and app will open on the device

```
$ yarn run ios
```

## Advanced CLJS compilation
```
$ clj -C:mobile build.clj compile advanced [ios | android]
```
To avoid using externs in advanced build, use [cljs-oops](https://github.com/binaryage/cljs-oops).

Follow the [React Native documentation](https://facebook.github.io/react-native/docs/signed-apk-android.html) to proceed with the release.


## License

Licensed under the [Mozilla Public License v2.0](LICENSE.md)
