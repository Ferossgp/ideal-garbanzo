(ns status-chat.views.chat.messages)

(def messages
  [{:day  "Today"
    :data [{:id        (random-uuid)
            :user      {:id        1
                        :username  "@lilblockchainz"
                        :anonymous false
                        :avatar    "https://source.unsplash.com/random/400x400"}
            :timestamp "2019-11-30T14:34:30.018Z"
            :messages  [{:type      :text
                         :id        (random-uuid)
                         :timestamp "12:00 AM"
                         :text      "By some time"}]}
           {:id        (random-uuid)
            :user      {:id        (random-uuid)
                        :username  "@lilblockchainz"
                        :anonymous false
                        :avatar    "https://source.unsplash.com/random/400x400"}
            :timestamp "2019-11-30T14:34:30.018Z"
            :messages  [{:type      :text
                         :id        (random-uuid)
                         :timestamp "12:00 AM"
                         :text      "By some time in the mid-’90s, I really came to understand what it is that I do. The crafting of dialogue and physical activity so that they can work in concert with each other. I wasn’t aware of that for my first two or three feature films, the actors had to tell me. I wasn’t aware of that for my first two or three feature films, the actors had to tell me. I wasn’t aware of that for my first two or three fea  I wasn’t aware of that for my first two or three feature films, the actors had to tell me. I wasn’t aware of that for my first two or three feature films, the actors had to tell me."}]}
           {:id        (random-uuid)
            :user      {:id        (random-uuid)
                        :username  "Teenage Mutant Ninja"
                        :anonymous true
                        :avatar    "https://source.unsplash.com/random/400x400"}
            :timestamp "2019-11-30T14:34:30.018Z"
            :messages  [{:type      :emoji
                         :id        (random-uuid)
                         :timestamp "12:00 AM"
                         :emoji     "😄"}]}
           {:id        (random-uuid)
            :user      {:id        1
                        :username  "Username"
                        :anonymous false
                        :avatar    "https://source.unsplash.com/random/400x400"}
            :timestamp "2019-11-30T14:34:30.018Z"
            :messages  [{:type      :text
                         :id        (random-uuid)
                         :just-sent true
                         :timestamp "12:00 AM"
                         :text      "I wasn’t aware of that for my first two or three fea "}]}
           {:id        (random-uuid)
            :user      {:id        (random-uuid)
                        :username  "Cyberpunk Hackerz"
                        :anonymous false
                        :avatar    "https://source.unsplash.com/random/400x400"}
            :timestamp "2019-11-30T14:34:30.018Z"
            :messages  [{:type      :sticker
                         :id        (random-uuid)
                         :timestamp "12:00 AM"
                         :uri       "https://source.unsplash.com/random/200x200"}]}
           {:id        (random-uuid)
            :user      {:id        (random-uuid)
                        :username  "@lilblockchainz"
                        :anonymous false
                        :avatar    "https://source.unsplash.com/random/400x400"}
            :timestamp "2019-11-30T14:34:30.018Z"
            :messages  [{:type         :transaction
                         :id           (random-uuid)
                         :currency     {:logo  "https://source.unsplash.com/random/40x40"
                                        :value "100"
                                        :code  "SMT"}
                         :alt-currency {:value 1
                                        :code  "USD"}
                         :state        :request
                         :timestamp    "11:00 PM"}]}]}
   {:day  "Not Today"
    :data [{:id        (random-uuid)
            :user      {:id        1
                        :username  "Cyberpunk Hackerz"
                        :anonymous false
                        :avatar    "https://source.unsplash.com/random/400x400"}
            :timestamp "2019-11-30T14:34:30.018Z"
            :messages  [{:type         "transaction"
                         :id           (random-uuid)
                         :currency     {:logo  "https://source.unsplash.com/random/40x40"
                                        :value "100"
                                        :code  "SMT"}
                         :alt-currency {:value 1
                                        :code  "USD"}
                         :state        :confirmed
                         :timestamp    "13:00 AM"}]}
           {:id        (random-uuid)
            :user      {:id        (random-uuid)
                        :username  "@lilblockchainz"
                        :anonymous false
                        :avatar    "https://source.unsplash.com/random/400x400"}
            :timestamp "2019-11-30T14:34:30.018Z"
            :messages  [{:reply     {:name "@cyberpunkhackerzz"
                                     :text "Oh hi, Lil!"}
                         :type      :text
                         :id        (random-uuid)
                         :timestamp "12:00 AM"
                         :text      "By some time"}
                        {:type      :text
                         :id        (random-uuid)
                         :timestamp "12:00 AM"
                         :text      "By some time in the mid-’90s"}
                        {:type      :text
                         :id        (random-uuid)
                         :timestamp "12:00 AM"
                         :text      "By some time in the mid-’90s"}]}]}])
