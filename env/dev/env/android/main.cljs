(ns ^:figwheel-no-load env.android.main
  (:require [reagent.core :as r]
            [status-chat.core :as core]
            [figwheel.client :as figwheel :include-macros true]
            [re-frisk-remote.core :as rr]
            [re-frame.core :refer [clear-subscription-cache!]]
            [env.config :as conf]
            [env.common.utils :as env.utils]))

(enable-console-print!)

(assert (exists? core/init) "Fatal Error - Your core.cljs file doesn't define an 'init' function!!! - Perhaps there was a compilation failure?")
(assert (exists? core/app-root) "Fatal Error - Your core.cljs file doesn't define an 'app-root' function!!! - Perhaps there was a compilation failure?")

(def cnt (r/atom 0))
(defn reloader [] @cnt [core/app-root])

;; Do not delete, root-el is used by the figwheel-bridge.js
(def root-el (r/as-element [reloader]))

(figwheel/start {:websocket-url    (:android conf/figwheel-urls)
                 :heads-up-display false
                 :jsload-callback  #(swap! cnt inc)})

(rr/enable-re-frisk-remote! {:host    (env.utils/re-frisk-url (:android conf/figwheel-urls))
                             :on-init core/init})