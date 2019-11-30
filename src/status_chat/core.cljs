(ns status-chat.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [status-chat.react :as react]
            [status-chat.events]
            [status-chat.subs]))

(def logo-img (js/require "./resources/images/cljs.png"))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [react/view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [react/text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [react/image {:source logo-img
                     :style  {:width 80 :height 80 :margin-bottom 30}}]
       [react/touchable-highlight {:style    {:background-color "#999" :padding 10 :border-radius 5}
                                   :on-press identity}
        [react/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent react/app-registry "StatusChat" #(r/reactify-component app-root)))
