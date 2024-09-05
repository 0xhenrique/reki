(ns reki.core
  (:gen-class))
(use '[clojure.java.shell :only [sh]])
(require '[clojure.string :as str])

(defn os-name []
  (let [osname (:out (sh "sh" "-c" "cat /etc/*-release | grep 'PRETTY_NAME' | cut -d '=' -f2 | tr -d '\"'"))]
    (str/trim osname)))

(defn get-os-name []
  (let [getosname (os-name)]
    (str "\u001b[1mOS:\u001b[0m " getosname)))

(defn get-font-family []
  (let [getfontfamily (:out (sh "fc-match"))
        font-name (second (str/split getfontfamily #":\s*" 2))
        font-name (first (str/split font-name #"\s+"))]
    (str "\u001b[1mFont Family:\u001b[0m " (str/replace font-name #"\"" ""))))

(defn get-shell-name []
  (let [bash-name (str/trim (:out (sh "bash" "-c" "echo ${0##*/}")))]
    (str "\u001b[1mShell:\u001b[0m " bash-name)))

(defn get-os-figlet []
  (let [osname (os-name)
        osfiglet (:out (sh "figlet" osname))]
    (str osfiglet)))

;;; This needs to be reworked since the way I'm calling cputem right now only works in my machine
;; (defn get-cpu-temp []
;;   (let [cputemp (str/trim (:out (sh "cat" "/sys/devices/platform/thinkpad_hwmon/hwmon/hwmon6/temp1_input")))]
;;     (str "\u001b[1mCPU Temperature:\u001b[0m " (* (Integer/parseInt cputemp) 0.001) "°C")))

(defn parse-uptime [uptime-str]
  (let [match (re-find #"up\s+((\d+)\s+days?,\s+)?(\d+):(\d+)" uptime-str)
        days (if (match 2) (Integer. (match 2)) 0)
        hours (Integer. (match 3))
        minutes (Integer. (match 4))]
    (format "%dd %dh %dm" days hours minutes)))

(defn get-uptime []
  (let [uptime-output (str/trim (:out (sh "uptime")))
        uptime-short (parse-uptime uptime-output)]
    (str "\u001b[1mUptime:\u001b[0m " uptime-short)))

(defn get-memory-usage []
  (let [free-output (:out (sh "free" "-h"))
        lines (str/split-lines free-output)
        memory-line (second lines)
        [_ total used & _] (str/split memory-line #"\s+")]
    (str "\u001b[1mMemory:\u001b[0m " used " / " total)))

(defn get-disk-usage []
  (let [df-output (:out (sh "df" "-h" "/"))
        disk-line (second (str/split-lines df-output))
        [_ total used _ _ _] (str/split disk-line #"\s+")]
    (str "\u001b[1mDisk Usage:\u001b[0m " used " / " total)))

(defn -main
  [& args]
  (println (get-os-figlet))
  (println (get-os-name))
  (println (get-font-family))
  ;; (println (get-uptime))
  (println (get-memory-usage))
  (println (get-disk-usage))
  (println (get-shell-name)))
