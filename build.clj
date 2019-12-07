(require '[cljs.build.api :as api]
         '[clojure.string :as cstr])

(def cljsbuild-config
  {:dev
   {:android
    {:source-paths ["react_native/src" "src" "env/dev/env/android"]
     :compiler     {:output-to     "target/android/app.js"
                    :main          'env.android.main
                    :output-dir    "target/android"
                    :npm-deps      true
                    :target        :nodejs
                    :optimizations :none}}
    :ios
    {:source-paths ["react_native/src" "src" "env/dev/env/ios"]
     :compiler     {:output-to     "target/ios/app.js"
                    :main          'env.ios.main
                    :output-dir    "target/ios"
                    :npm-deps      true
                    :target        :nodejs
                    :optimizations :none}}}
   :advanced
   {:android
    {:source-paths ["react_native/src" "src" "env/prod/env/android"]
     :compiler     {:output-to          "index.android.js"
                    :source-map         "index.android.js.map"
                    :main               'env.android.main
                    :output-dir         "target/android-prod"
                    :elide-asserts      true
                    :static-fns         true
                    :optimize-constants true
                    :npm-deps           true
                    :target             :nodejs
                    :optimizations      :advanced
                    :closure-defines    {"goog.DEBUG" false}}}
    :ios
    {:source-paths ["react_native/src" "src" "env/prod/env/ios"]
     :compiler     {:output-to          "index.ios.js"
                    :source-map         "index.ios.js.map"
                    :main               'env.ios.main
                    :output-dir         "target/ios-prod"
                    :elide-asserts      true
                    :static-fns         true
                    :optimize-constants true
                    :npm-deps           true
                    :target             :nodejs
                    :optimizations      :advanced
                    :closure-defines    {"goog.DEBUG" false}}}}})

(def cli-tasks-info
  {:compile
   {:desc  "Compile ClojureScript"
    :usage ["Usage: clj -C:build build.clj compile [env] [build-id] [type]"
            ""
            "[env] (required): Pre-defined build environment. Allowed values: \"dev\", \"advanced\", \"test\""
            "[build-id] (optional): Build ID. When omitted, this task will compile all builds from the specified [env]."
            "[type] (optional): Build type - value could be \"once\" or \"watch\". Default: \"once\"."]}
   :figwheel
   {:desc  "Start figwheel + CLJS REPL / nREPL"
    :usage ["Usage: clj -R:repl build.clj figwheel [options]"
            ""
            "[-h|--help] to see all available options"]}
   :test
   {:desc  "Run tests"
    :usage ["Usage: clj -R:test build.clj test [build-id] [type]"
            ""
            "[build-id] (required): Value could be \"unit\". It will compile then run the tests."
            "[type] (optional): Build type - value could be \"once\" or \"watch\". Default: \"once\"."]}
   :env
   {:desc  "Generate runtime environment files from .env configs, overwrites existing runtime environment files"
    :usage ["Usage: clj -C:build build.clj env [build-id] [deployment]"
            ""
            "[build-id] (required): Build ID, for which this task will refresh env configs according to system env vars."
            "[deployment] (required): Target deployment environment, allowed values: \"dev\", \"stage\", \"prod\", \"test\""]}
   :help
   {:desc "Show this help"}})

(def reset-color "\u001b[0m")
(def red-color "\u001b[31m")
(def green-color "\u001b[32m")
(def yellow-color "\u001b[33m")

(defn- colorizer [c]
  (fn [& args]
    (str c (apply str args) reset-color)))

(defn- println-colorized [message color]
  (println ((colorizer color) message)))

(defn- elapsed [started-at]
  (let [elapsed-us (- (System/currentTimeMillis) started-at)]
    (with-precision 2
      (str (/ (double elapsed-us) 1000) " seconds"))))

(defn- try-require [ns-sym]
  (try
    (require ns-sym)
    true
    (catch Exception e
      false)))

(defmacro with-namespaces [namespaces & body]
  (if (every? try-require namespaces)
    `(do ~@body)
    `(do (println-colorized "Task not available - required dependencies not found" red-color)
         (System/exit 1))))

(defn rn-build? [build-id]
  (contains? #{:ios :android} build-id))

(defn web-build? [build-id]
  (contains? #{:web} build-id))

(defn devcards-build? [build-id]
  (contains? #{:devcards} build-id))

(defn- get-cljsbuild-config [name-env & [name-build-id]]
  (try
    (let [env (keyword name-env)]
      (when-not (contains? cljsbuild-config env)
        (throw (Exception. (str "ENV " (pr-str name-env) " does not exist"))))
      (let [env-config (get cljsbuild-config env)]
        (if name-build-id
          (let [build-id (keyword name-build-id)]
            (when-not (contains? env-config build-id)
              (throw (Exception. (str "Build ID " (pr-str name-build-id) " does not exist"))))
            (get env-config build-id))
          env-config)))
    (catch Exception e
      (println-colorized (.getMessage e) red-color)
      (System/exit 1))))

(defn- get-output-files [compiler-options]
  (if-let [output-file (:output-to compiler-options)]
    [output-file]
    (into [] (map :output-to (->> compiler-options :modules vals)))))

(defn- compile-cljs-with-build-config [build-config build-fn env build-id]
  (let [{:keys [source-paths compiler]} build-config
        output-files                    (get-output-files compiler)
        started-at                      (System/currentTimeMillis)]
    (println (str "Compiling " (pr-str build-id) " for " (pr-str env) "..."))
    (flush)
    (build-fn (apply api/inputs source-paths) compiler)
    (println-colorized (str "Successfully compiled " (pr-str output-files)
                            " in " (elapsed started-at) ".")
                       green-color)))

(defn- compile-cljs [env & [build-id watch? watch-fn]]
  (let [build-fn         (if watch? api/watch api/build)
        get-build-config #(if (and watch? watch-fn)
                            (assoc-in (get-cljsbuild-config %1 %2) [:compiler :watch-fn] watch-fn)
                            (get-cljsbuild-config %1 %2))]
    (if build-id
      (compile-cljs-with-build-config (get-build-config env build-id) build-fn env build-id)
      (doseq [[build-id build-config] (get-cljsbuild-config env)]
        (compile-cljs-with-build-config (get-cljsbuild-config env build-id) build-fn env build-id)))))

(defn- show-help []
  (doseq [[task {:keys [desc usage]}] cli-tasks-info]
    (println (format (str yellow-color "%-12s" reset-color green-color "%s" reset-color)
                     (name task) desc))
    (when usage
      (println)
      (->> usage
           (map #(str "  " %))
           (cstr/join "\n")
           println)
      (println))))

(defmulti task first)

(defmethod task :default [args]
  (println (format "Unknown or missing task. Choose one of: %s\n"
                   (->> cli-tasks-info
                        keys
                        (map name)
                        (interpose ", ")
                        (apply str))))
  (show-help)
  (System/exit 1))

;;; Compiling task

(defmethod task "compile" [[_ env build-id type]]
  (case type
    (nil "once") (compile-cljs env build-id)
    "watch"      (compile-cljs env build-id true)
    (do (println "Unknown argument to compile task:" type)
        (System/exit 1))))

;;; Figwheeling task

(def figwheel-cli-opts
  [["-p" "--platform BUILD-IDS" "CLJS Build IDs for platforms <web|android|ios|devcards>"
    :id       :build-ids
    :default  [:web]
    :parse-fn #(->> (.split % ",")
                    (map (comp keyword cstr/lower-case cstr/trim))
                    vec)
    :validate [(fn [build-ids] (every? #(some? (#{:web :landing :android :ios :devcards} %)) build-ids))
               "Allowed \"web\", \"landing\", \"devcards\", \"android\", and/or \"ios\""]]
   ["-n" "--nrepl-port PORT" "nREPL Port"
    :id       :port
    :parse-fn #(if (string? %) (Integer/parseInt %) %)
    :validate [#(or (true? %) (< 0 % 0x10000))
               "Must be a number between 0 and 65536"]]
   ["-a" "--android-device TYPE" "Android Device Type <avd|genymotion|real>"
    :id       :android-device
    :parse-fn #(keyword (cstr/lower-case %))
    :validate [#(some? (#{:avd :genymotion :real} %))
               "Must be \"avd\", \"genymotion\", or \"real\""]]
   ["-i" "--ios-device TYPE" "iOS Device Type <simulator|real>"
    :id       :ios-device
    :parse-fn #(keyword (cstr/lower-case %))
    :validate [#(some? (#{:simulator :real} %)) "Must be \"simulator\", or \"real\""]]
   ["-h" "--help"]])

(defn print-and-exit [msg]
  (println msg)
  (System/exit 1))

(defn parse-figwheel-cli-opts [args]
  (with-namespaces [[clojure.tools.cli :as cli]]
    (let [{:keys [options errors summary]} (cli/parse-opts args figwheel-cli-opts)]
      (cond
        (:help options)     (print-and-exit summary)
        (not (nil? errors)) (print-and-exit errors)
        :else               options))))

(defmethod task "figwheel" [[_ & args]]
  (with-namespaces [[figwheel-sidecar.repl-api :as ra]
                    [re-frisk-sidecar.core :as rfs]
                    [clj-rn.core
                     :refer [get-main-config]
                     :rename {get-main-config get-cljrn-config}
                     :as clj-rn]]
    (let [{:keys [build-ids port android-device ios-device]} (parse-figwheel-cli-opts args)
          {:keys [js-modules resource-dirs figwheel-bridge]
           :as   cljrn-config}                               (get-cljrn-config)

          hosts-map {:android (clj-rn/resolve-dev-host :android android-device)
                     :ios     (clj-rn/resolve-dev-host :ios ios-device)
                     :web     {:port 7888}}]
      (when (some rn-build? build-ids)
        (println-colorized "Init React Native build" yellow-color)
        (clj-rn/enable-source-maps)
        (clj-rn/write-env-dev hosts-map)
        (doseq [build-id build-ids
                :let     [host-ip                 (get hosts-map build-id)
                          platform-name           (condp = build-id
                                                    :ios     "iOS"
                                                    :android "Android")]]
          (clj-rn/rebuild-index-js build-id {:app-name        (get cljrn-config :name)
                                             :host-ip         host-ip
                                             :js-modules      js-modules
                                             :resource-dirs   resource-dirs
                                             :figwheel-bridge figwheel-bridge})
          (when (= build-id :ios)
            (clj-rn/update-ios-rct-web-socket-executor host-ip)
            (println-colorized "Host in RCTWebSocketExecutor.m was updated" green-color))
          (println-colorized (format "Dev server host for %s: %s" platform-name host-ip) green-color)))

      (ra/start-figwheel!
       {:figwheel-options (cond-> {:builds-to-start build-ids}
                            port (merge {:nrepl-port       port
                                         :nrepl-middleware ["cider.nrepl/cider-middleware"
                                                            "cider.piggieback/wrap-cljs-repl"
                                                            "refactor-nrepl.middleware/wrap-refactor"]})
                            (some web-build? build-ids)
                            (merge {:http-server-root "web"
                                    ;; :ring-handler     "static.index/catch-all-handler"
                                    :css-dirs         ["resources/web/css"]})

                            (some devcards-build? build-ids)
                            (merge {:http-server-root "devcards"}))
        :all-builds (into []
                          (for [[build-id {:keys [source-paths compiler warning-handlers figwheel]}]
                                (get-cljsbuild-config :dev)]
                            {:id           build-id
                             :source-paths (concat source-paths
                                                   ["env/dev/env/common"
                                                    "env/dev/env/config.cljs"])
                             :compiler     compiler
                             :figwheel     (if figwheel figwheel true)}))})
      (rfs/-main)
      (if-not port
        (ra/cljs-repl)
        (spit ".nrepl-port" port)))))


(defn clojure-file? [file-name]
  (or (cstr/ends-with? file-name ".clj")
      (cstr/ends-with? file-name ".cls")
      (cstr/ends-with? file-name ".clc")
      (cstr/ends-with? file-name ".cljx")))

(defmethod task "lint" [& args]
  (with-namespaces [[clj-kondo.core :as clj-kondo]]
    (let [default-paths ["src" "test"]
          paths         (if (seq args)
                          (filter #(and (clojure-file? %)
                                        (or (cstr/starts-with? % "src")
                                            (cstr/starts-with? % "test")))
                                  args)
                          default-paths)
          kondo-run     (clj-kondo/run! {:lint      default-paths
                                         :cache     true
                                         :cache-dir ".cache/clj-kondo"})]
      (clj-kondo/print! kondo-run)
      (if-not (zero? (get-in kondo-run [:summary :error]))
        (System/exit 1)
        (System/exit 0)))))

;; Help
(defmethod task "help" [_]
  (show-help)
  (System/exit 1))

;;; Build script entrypoint.
(task *command-line-args*)
