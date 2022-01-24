(ns blog.client.core
  (:require [re-frame.core :as re-frame]
            [reagent.dom :as rdom]
            [blog.client.events :as events]
            [blog.client.form :as form]))

(def debug?
  ^boolean goog.DEBUG)

(defn dev-setup []
  (when debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [:div
                  [form/file-form]] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
