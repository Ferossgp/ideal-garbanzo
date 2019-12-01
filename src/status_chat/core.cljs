(ns status-chat.core
  (:require [reagent.core :as r :refer [atom]]
            [oops.core :refer [oget ocall]]
            [status-chat.react :as react]
            [status-chat.views.chat.view :as chat]))

(defn app-root []
  (fn []
    [chat/chat-screen]))

(defn init []
  (ocall react/app-registry "registerComponent" "StatusChat"
         #(r/reactify-component app-root)))
