(ns status-chat.react
  (:require [reagent.core :as r]
            [oops.core :refer [oget ocall]]))

(def ReactNative (js/require "react-native"))

(def app-registry (oget ReactNative "AppRegistry"))
(def text (r/adapt-react-class (oget ReactNative "Text")))
(def view (r/adapt-react-class (oget ReactNative "View")))
(def image (r/adapt-react-class (oget ReactNative "Image")))
(def touchable-highlight (r/adapt-react-class (oget ReactNative "TouchableHighlight")))
(def touchable-opacity (r/adapt-react-class (oget ReactNative "TouchableOpacity")))
(def section-list (r/adapt-react-class (oget ReactNative "SectionList")))


(def FastImage (js/require "react-native-fast-image"))
(def react-native-fast-image (oget FastImage "default"))

(def fast-image (r/adapt-react-class react-native-fast-image))

(def fast-image-preload (oget react-native-fast-image "preload"))
(def fast-image-priorities
  {:low    (oget react-native-fast-image "priority" "low")
   :normal (oget react-native-fast-image "priority" "normal")
   :high   (oget react-native-fast-image "priority" "high")})
(def fast-image-cache
  {:immutable  (oget react-native-fast-image "cacheControl" "immutable")
   :web        (oget react-native-fast-image "cacheControl" "web")
   :cache-only (oget react-native-fast-image "cacheControl" "cacheOnly")})
(def fast-image-resize-mode
  {:contain (oget react-native-fast-image "resizeMode" "contain")
   :cover   (oget react-native-fast-image "resizeMode" "cover")
   :stretch (oget react-native-fast-image "resizeMode" "stretch")
   :center  (oget react-native-fast-image "resizeMode" "center")})
