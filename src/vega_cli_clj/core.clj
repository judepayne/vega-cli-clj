(ns vega-cli-clj.core
  (:require [clojure.java.shell    :as shell]
            [clojure.java.io       :as io]
            [clojure.data.json     :as json]))


(defn- vega-cli-installed? [mode]
  (case mode
    :vega-lite (= 0 (:exit (shell/sh "vl2svg" "--help")))
    :vega      (= 0 (:exit (shell/sh "vg2svg" "--help")))))


(def ^{:private true} cli-installed? (memoize vega-cli-installed?))


(defn- bytes->file
  "Writes a byte array to file, f."
  [f ba]
  (with-open [out (io/output-stream (io/file f))]
    (.write out ba)))


(def test-dat
  {:vega-doc
   {:data
    {:values
     [{:a "A", :b 28}
      {:a "B", :b 55}
      {:a "C", :b 43}
      {:a "D", :b 91}
      {:a "E", :b 81}
      {:a "F", :b 53}
      {:a "G", :b 19}
      {:a "H", :b 87}
      {:a "I", :b 52}]},
    :mark "bar",
    :encoding
    {:x {:field "a", :type "ordinal", :axis {:labelAngle 0}},
     :y {:field "b", :type "quantitative"}}},
   :mode :vega-lite,
   :fmt :png,
   :output-filename "out.png"
   })


(defn vega-cli
   "Takes vega-doc and mode (either :vega or :vega-lite) and uses the vega/vega-lite cli tools
  to output in the specifed binary formats (:png, :pdf) or text formats (:vega, :svg). If
  output-filename is supplied, the output is written to that file."
  ([{:keys [vega-doc mode fmt output-filename]
     :or {fmt :svg mode :vega-lite}}]
   {:pre [(#{:vega-lite :vega} mode)
          (#{:png :pdf :svg :vega} fmt)]}
   (if (cli-installed? mode)
     (let [short-mode (case (keyword mode) :vega-lite "vl" :vega "vg")
           ext (name (if (= fmt :vega) :vg fmt))
           command (str short-mode 2 ext)
           js (json/write-str vega-doc)
           {:keys [out err]}
           (cond
             (#{:pdf :png} fmt) (shell/sh command :in js :out-enc :bytes)
             :else (shell/sh command :in js))]
       (if (empty? err)
         (if output-filename
           (if (bytes? out)
             (bytes->file output-filename out)
             (spit output-filename out))
           out)
         (throw (RuntimeException. err))))
     (throw (RuntimeException.
             (str "Vega CLI not installed! Please run `npm install -g vega vega-lite vega-cli`. "
                  "If you have an error: `TypeError: Cannot read proprerty 'getContext' of null`, "
                  "then run `npm install -g canvas`."))))))
