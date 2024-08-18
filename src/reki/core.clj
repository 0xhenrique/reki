(ns reki.core
  (:gen-class))
(use '[clojure.java.shell :only [sh]])
(require '[clojure.string :as str])

(defn os-name []
  (let [osname (:out (sh "uname" "-n"))]
    (clojure.string/trim osname)))

(defn get-shell-name []
  (let [bash-name (str/trim (:out (sh "bash" "-c" "echo ${0##*/}")))]
    (str "\u001b[1mShell:\u001b[0m " bash-name)))

(defn get-os-figlet []
  (let [osname (os-name)
        osfiglet (:out (sh "figlet" osname))]
    (str osfiglet)))

(defn get-cpu-temp []
  (let [cputemp (str/trim (:out (sh "cat" "/sys/devices/platform/thinkpad_hwmon/hwmon/hwmon6/temp1_input")))]
    (str "\u001b[1mCPU Temperature:\u001b[0m " (* (Integer/parseInt cputemp) 0.001) "°C")))

(defn get-uptime []
  (let [uptime-output (str/trim (:out (sh "uptime")))]
    (str "\u001b[1mUptime:\u001b[0m " uptime-output)))

(defn get-memory-usage []
  (let [free-output (:out (sh "free" "-h"))
        memory-line (second (clojure.string/split-lines free-output))]
    (str "\u001b[1mMemory Usage:\u001b[0m " memory-line)))

(defn get-disk-usage []
  (let [df-output (:out (sh "df" "-h" "/"))
        disk-line (second (clojure.string/split-lines df-output))]
    (str "\u001b[1mDisk Usage:\u001b[0m " disk-line)))

(defn -main
  [& args]
  (println (get-os-figlet))
  (println (get-uptime))
  (println (get-memory-usage))
  (println (get-disk-usage))
  (println (get-shell-name))
  (println (get-cpu-temp)))
