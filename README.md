
initially extracted from [oz](https://github.com/metasoarous/oz).

### installation

[![Clojars Project](https://img.shields.io/clojars/v/vega-cli-clj.svg)](https://clojars.org/vega-cli-clj)

### Usage

There is only one public function exposed, vega-cli which converts a vega or vega-lite specification (in Clojure form - i.e. nested maps and vectors - rather than json) and converts it to a pdf/svg/png or vega json (in the case of vega-lite). The output of this function is not written to disk unless an `output-file` is specified.

    (require '[vega-cli-clj :as vg])
    
    (defn play-data [& names]
      (for [n names i (range 20)]
    {:time i :item n :quantity (+ (Math/pow (* i (count n)) 0.8) (rand-int (count n)))}))
    
    (def line-plot
      ;; line is vega-lite
      {:data {:values (play-data "monkey" "slipper" "broom")}
       :encoding {:x {:field "time" :type "quantitative"}
                  :y {:field "quantity" :type "quantitative"}
                  :color {:field "item" :type "nominal"}}
       :mark "line"})
       
    (vg/vega-cli {:vega-doc line-plot
                  :mode :vega-lite    ;; always specify the format of the input
                  :fmt :svg}          ;; the format of the ouput, can be :svg, :vega, :png or :pdf
                  
=> Will return svg as a string.

When `:pdf` or `:png` is specified for the `:fmt` key, a byte array is returned.

When `output-filename` is added to the map of arguments, the output is written to that file.

**Before running** vega-cli-clj, you run to have the vega command line tools properly installed.
*In each directory* where you'll be running this code, you need to run:

    npm install -g vega vega-lite vega-cli
    
and

    npm install -g canvas


### License 

Eclipse Public License 2.0
