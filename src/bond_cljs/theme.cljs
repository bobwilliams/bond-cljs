(ns bond-cljs.theme
  (:require [cljs.nodejs :as node]))

(def fs (node/require "fs"))
(def lame (node/require "lame"))
(def speaker (node/require "speaker"))
(def theme-file "bond-theme.mp3")

(def audio-options (js-obj "channels" 2
                           "bitDepth" 16
                           "sampleRate" 44100))

(defn play-theme! []
  (let [decoder (.Decoder lame)
        speaker (speaker. audio-options)
        input-stream (.createReadStream fs theme-file)]
    (-> input-stream (.pipe decoder) (.pipe speaker))))