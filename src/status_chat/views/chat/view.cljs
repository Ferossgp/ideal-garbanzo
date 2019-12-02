(ns status-chat.views.chat.view
  (:require [clojure.string :as cstr]
            [reagent.core :as r]
            [oops.core :refer [oget]]
            [status-chat.react :as react]
            [status-chat.views.chat.messages :refer [messages]]
            ["react-native-linear-gradient" :as LinearGradient]))

(def linear-gradient (r/adapt-react-class (oget LinearGradient "default")))

(def curren-user {:id 1})

;; could be clojure.core.match
(defn- match [[ss ts] [s t]]
  (and (contains? ss s)
       (contains? ts t)))


;; NOTE: Should be separated into components, each with own ns

;; NOTE: Sizes, Fonts, and Colors should be constants in a common style system
(def white "white")
(def black "black")
(def primary "#4360DF")
(def light-white "rgba(255, 255, 255, 0.7)")
(def secondary "#ECEFFC")
(def dark-grey "#939BA1")
(def light-grey "#EEF2F5")

(def base-font {:font-family "Inter-Regular"})
(def bold-font {:font-family "Inter-Bold"})

(def base-border-width 1)

(def chat-screen-style
  {:flex               1
   :background-color   white
   :padding-horizontal 8})

;; Avatar component

(def avatar-wrapper-style {:margin-bottom 4})

;; Red box has some border-radius, I don't think it is a part of touchable
(def avatar-container-style {:padding-horizontal 8})

(def avatar-image-style {:width 36
                         :height 36
                         :border-radius 36
                         :border-width base-border-width
                         :border-style "solid"
                         :border-color "rgba(0,0,0,0.1)" ; Seems like it should be light grey
                         })

;; Assume that every user has an avatar image
(defn avatar [{:keys [image]}]
  [react/view {:style avatar-wrapper-style}
   [react/touchable-opacity {:on-press identity}
    [react/view {:style avatar-container-style}
     [react/fast-image {:source {:uri image}
                        :style  avatar-image-style}]]]])

;; Username component
(defn username-style [anonymous]
  (if anonymous
    (merge
     base-font
     {:font-size   12
      :line-height 18
      :color       dark-grey})
    (merge
     bold-font
     {:font-size   13
      :line-height 18
      :color       primary})))

(def username-wrapper-style {:padding-vertical 2
                             :margin-left      12})

(defn username [{:keys [username anonymous]}]
  [react/touchable-opacity {:on-press identity}
   [react/view {:style username-wrapper-style}
    [react/text {:style (username-style anonymous)}
     username]]])

;; Reply component
(def reply-container-style
  {:padding-horizontal 12
   :padding-top        6})

(defn reply-person-name-style [sender]
  (merge
   bold-font
   {:font-size   13
    :line-height 18}
   (case sender
     :incoming {:color dark-grey}
     :outgoing {:color light-white})))

(defn reply-text-style [sender]
  (merge
   base-font
   {:line-height      20
    :font-size        14
    :padding-vertical 6}
   (case sender
     :incoming {:color dark-grey}
     :outgoing {:color light-white})))

(defn separator-style [_]
  {:height           2
   :border-radius    1
   :background-color "rgba(0, 0, 0, 0.1)"})

(defn reply-text [sender {:keys [name text]}]
  [react/view {:style reply-container-style}
   [react/view
    [react/text {:style (reply-person-name-style sender)}
     (str "↪ " name)]]
   [react/view
    ;; here can be reuse of message components
    [react/text {:style (reply-text-style sender)}
     text]]
   [react/view {:style (separator-style sender)}]])

;; Status text component

(def status-text-container-style
  {:align-items      "flex-end"
   :padding-vertical 4
   :flex             1})

(def status-text-style
  (merge
   base-font
   {:font-size   10
    :line-height 14
    :flex        1
    :color       dark-grey}))

;; Assume if no status, margin-bottom for the message is 0
(defn status-text []
  [react/view {:style status-text-container-style}
   [react/text {:style status-text-style}
    (cstr/upper-case "✓ Sent")]])

;; Message timestamp component
(defn message-timestamp-style [sender type]
  (merge {:font-size      10
          :line-height    14
          :letter-spacing 0.5
          :padding-bottom 3
          :padding-left   12
          :margin-left    "auto"
          :align-self     "flex-end"}

         (condp match [sender type]
           [#{:incoming} #{"text"}] {:color dark-grey}
           [#{:outgoing} #{"text"}] {:color light-white}

           [#{:outgoing :incoming} #{"transaction" "sticker" "emoji"}]
           {:color dark-grey})))

;; The specification for the timestamp is not finished,
;; In different bubbles it is aligned differently
;; It looks like an absolute position on the bottom-right corner

(defn message-time [sender {:keys [timestamp type]}]
  [react/text {:style (message-timestamp-style sender type)}
   (cstr/upper-case timestamp)])


;; Text message component
(def message-content-row-style
  {:flex-direction     "row"
   :flex-wrap          "wrap"
   :justify-content    "space-between"
   :padding-vertical   6
   :padding-horizontal 12})

(defn message-text-style [type]
  (merge
   base-font
   {:font-size   15
    :line-height 22
    :flex-wrap   "wrap"}
   (case type
     :incoming {:color black}
     :outgoing {:color white})))

(def read-more-height 72)

(defn read-more-style [sender]
  (merge
   {:padding-bottom  8
    :align-items     "center"
    :justify-content "flex-end"
    :margin-top      (* 0.1 read-more-height)
    :height          (* 0.9 read-more-height) ; Imitate 90%
    :bottom          0
    :width           "100%"
    :position        "absolute"}
   (case sender
     :incoming {:border-bottom-right-radius 16
                :border-bottom-left-radius  4}
     :outgoing {:border-bottom-right-radius 4
                :border-bottom-left-radius  16})))

(def read-less-style
  {:padding-bottom  8
   :align-items     "center"
   :justify-content "flex-end"
   :height          44})

(def read-more-icon-style
  {:width            24
   :height           24
   :border-radius    12
   :align-items      "center"
   :justify-content  "center"
   :background-color "#939BA1"})

(defn gradient-colors [sender]
  (clj->js (case sender
             :incoming ["rgba(236, 239, 252, 0)" "rgba(236, 239, 252, 1)"]
             :outgoing ["rgba(67, 96, 223, 0)" "rgba(67, 96, 223, 1)"])))

(defn read-more [{:keys [sender open]}]
  [react/view {}
   [react/touchable-opacity {:on-press open
                             ;; FIXME: Touchable with position absolute does not receive touches
                             ;; so I made a hack with manipulation of sizes
                             :style    {:width   "100%"
                                        :height  read-more-height
                                        :top     (- read-more-height)
                                        :z-index 10}}
    [linear-gradient {:colors (gradient-colors sender)
                      :style  (read-more-style sender)}
     [react/view {:style read-more-icon-style}
      ;; Should be icon instead of text
      [react/text "⌄"]]]]])

(defn read-less [{:keys [close]}]
  [react/touchable-opacity {:on-press close}
   [react/view {:style read-less-style}
    [react/view {:style read-more-icon-style}
     [react/text "⌃"]]]])

(def text-limit 180)

(defn text-message []
  (let [open   (r/atom false)
        height (r/atom nil)
        layout (r/atom nil)]
    (fn [sender {:keys [text] :as message}]
      (let [long-text (< text-limit (count text))]
        ;; FIXME: Touchable with position absolute does not receive touches
        ;; so I made a hack with manipulation of sizes
        ;; thats a fast solution but not best for production
        (when (and long-text (not @open)
                   (not @height) @layout)
          (reset! height (- (oget @layout "height") read-more-height)))
        [react/view {:on-layout #(reset! layout (oget % "nativeEvent" "layout"))
                     :style     (if (and (not @open) @height)
                                  {:height @height}
                                  {})}
         [react/view {:style message-content-row-style}
          [react/text {:style (message-text-style sender)}
           (if (or @open (not long-text))
             text
             ;; Should be a better excerpt?
             (subs text 0 text-limit))]
          (when (or @open (not long-text))
            [message-time sender message])]
         (when long-text
           (if @open
             [read-less {:close #(reset! open false)}]
             [read-more {:sender sender
                         :open   #(reset! open true)}]))]))))

;; Emoji message component
(defn emoji-message [sender {:keys [emoji]
                             :as   message}]
  [react/view {:style message-content-row-style}
   [react/text
    emoji]
   [message-time sender message]])

;; Sticker message component
(defn sticker-message [sender {:keys [uri] :as message}]
  [react/view {:style message-content-row-style}
   [react/fast-image {:source {:uri uri}
                      ;; Sizes can be taken from server, depends on impl
                      :style  {:width  140
                               :height 140}}]
   [message-time sender message]])

;; Confirmed pill component

(def pill-container-style
  {:padding-vertical   6
   :padding-horizontal 12
   :border-radius      14
   :border-color       light-grey
   :border-width       base-border-width
   :border-style       "solid"
   :flex-direction     "row"})

(defn pill [& children]
  (into [react/view {:style pill-container-style}]
        children))

;; Transaction message component
(def transaction-message-container-style
  {:flex-direction "column"})

(def transaction-content-style
  {:flex-direction     "column"
   :padding-horizontal 12
   :padding-vertical   10})

(def transaction-message-header-style
  {:flex-direction "row"
   :margin-bottom  8})

(def transaction-message-header-text-style
  (merge bold-font
         {:color       dark-grey
          :font-size   13
          :line-height 18}))

(def confirmed-icon-style
  {:margin-right 6})

(def confirmed-text-style
  (merge
   bold-font
   {:font-size   13
    :line-height 16
    :color       black}))

(def transaction-button-style
  {:padding-vertical 12
   :align-items      "center"
   :border-top-width base-border-width
   :border-top-color light-grey
   :border-top-style "solid"})

(def transaction-button-text-style
  {:font-size   15
   :line-height 20
   :color       primary})

(def transaction-amount-style
  {:flex-direction "row"})

(def transaction-amount-logo-style
  {:margin-right 6})

(def transaction-state-pill-style
  {:margin-top 12})

(def amount-style
  (merge
   base-font
   {:font-size   20
    :line-height 24
    :color       black}))

(def fiat-style
  (merge
   base-font
   {:font-size   12
    :line-height 16
    :color       dark-grey}))

(def transaction-amount-state-style
  {:flex-direction "row"
   :align-items    "flex-end"})

(defn confirmed-pill []
  [react/view {:style transaction-state-pill-style}
   [pill
    ;; Icon could be loaded as svg with react-native-svg
    [react/view {:style confirmed-icon-style}
     [react/view {:style {:width            16
                          :height           16
                          :background-color "rgba(245, 88, 88, 0.2)"}}]]
    [react/text {:style confirmed-text-style}
     "Confirmed"]]])

(defn transaction-amount [{:keys [currency alt-currency]}]
  [react/view {:style transaction-amount-style}
   [react/view {:style transaction-amount-logo-style}
    [react/fast-image {:source {:uri (:logo currency)}
                       :style  {:width  24
                                :height 24}}]]
   [react/view
    [react/text {:style amount-style}
     (str (:value currency) " " (:code currency))]
    [react/text {:style fiat-style}
     (str (:value alt-currency) " " (:code alt-currency))]]])


(defn transaction-buttons []
  [react/view {:style {:flex-direction "column"}}
   [react/touchable-opacity {}
    [react/view {:style transaction-button-style}
     [react/text {:style (merge bold-font
                                transaction-button-text-style)}
       "Accept and send"]]]
   [react/touchable-opacity {}
    [react/view {:style transaction-button-style}
     [react/text {:style (merge base-font
                                transaction-button-text-style)}
       "Decline"]]]])

(defn transaction-message [sender {:keys [state timestamp] :as tr}]
  [react/view {:style transaction-message-container-style}
   [react/view {:style transaction-content-style}
    [react/view {:style transaction-message-header-style}
     [react/text {:style transaction-message-header-text-style}
      (case state
        "request"   "↑ Transaction request"
        "confirmed" "↑ Outgoing transaction")]]
    [transaction-amount tr]
    [react/view {:style transaction-amount-state-style}
     (when (= state "confirmed")
       [confirmed-pill])
     [message-time sender {:timestamp timestamp
                           :type      "transaction"}]]]
   (when (= state "request")
     [transaction-buttons])])

;; Message group component

(defn message-group-container-style [sender]
  (case sender
    :incoming {:justify-content "flex-start"}
    :outgoing {:justify-content "flex-end"}))

(defn message-group-style [type]
  (merge
   {:flex-direction "row"
    :align-items    "flex-end"}
   (case type
     :incoming {:justify-content "flex-start"
                :margin-right    (- 52 8)}
     :outgoing {:justify-content "flex-end"
                :margin-left     (- 96 8)})))

(defn message-bulb-style [sender type]
  (merge
   {:border-top-left-radius     16
    :border-top-right-radius    16
    :border-bottom-right-radius 16
    :border-bottom-left-radius  16
    :margin-bottom              4
    :flex-shrink                1}

   (case sender
     :incoming {:border-bottom-left-radius 4}
     :outgoing {:border-bottom-right-radius 4
                ;; Not sure if margin is always that way,
                ;; design does not specify all cases
                :margin-top                 16})

   (condp match [sender type]
     [#{:incoming} #{"text"}] {:background-color secondary}
     [#{:outgoing} #{"text"}] {:background-color primary}

     [#{:outgoing :incoming} #{"sticker" "emoji" "transaction"}]
     {:border-color light-grey
      :border-width base-border-width
      :border-style "solid"})))

(defn messages-column-style [sender]
  (merge
   {:flex-direction "column"
    :flex           1}
   (case sender
     :incoming {:align-items "flex-start"}
     :outgoing {:align-items "flex-end"})))

(defn message-group [{:keys [user messages]}]
  (let [sender (if (= (:id user)
                      (:id curren-user))
                 :outgoing
                 :incoming)]
    [react/view {:style (message-group-container-style sender)}
     [react/view {:style (message-group-style sender)}
      (when (= sender :incoming)
        [avatar {:image (:avatar user)}])
      [react/view {:style (messages-column-style sender)}
       (when (= sender :incoming)
         [username user])
       (for [{:keys [id type reply] :as message} messages]
         ^{:key id}
         [:<>
          [react/view {:style (message-bulb-style sender type)}
           (when reply
             [reply-text sender reply])
           (case type
             "text"        [text-message sender message]
             "emoji"       [emoji-message sender message]
             "sticker"     [sticker-message sender message]
             "transaction" [transaction-message sender message]
             nil)]
          (when (:just-sent message)
            [status-text])])]]]))

;; Date text component

(def date-text-container-style
  {:justify-content  "center"
   :align-items      "center"
   :padding-vertical 8})

(def date-text-style
  {:font-size    14
   :line-height 20
   :color       dark-grey})

(defn date-text [date]
  [react/view {:style date-text-container-style}
   [react/text {:style date-text-style}
    date]])


;; NOTE: SectionList needs a better wrapper
(defn section-header [item]
  (r/as-element
   [date-text (oget item "section" "day")]))

(defn render-item [item]
  (let [message (js->clj (oget item "item") :keywordize-keys true)]
   (r/as-element
    [message-group message])))

(defn key-extractor [item]
  (oget item "id"))

(defn chat-screen []
  [react/view {:style chat-screen-style}
   [react/section-list {:sections              (clj->js messages)
                        :inverted              true
                        :key-extractor         key-extractor
                        :render-section-footer section-header
                        :render-item           render-item}]])
