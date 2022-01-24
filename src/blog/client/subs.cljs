(ns blog.client.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::file-form-values
 (fn [db]
   (get-in db [:forms :file-form])))
