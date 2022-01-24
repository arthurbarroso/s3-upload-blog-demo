(ns blog.client.form
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [blog.client.subs :as subs]
            [blog.client.events :as events]))

(defn handle-image-data [file file-key]
  {:file-type (.-type file)
   :file-size (-> file
                  (.-size)
                  (/ 1024)
                  .toFixed)
   :file-key (str file-key (.-name file))
   :file-name (.-name file)})

(defn get-image-data [input-id]
  (let [el (.getElementById js/document input-id)
        file (aget (.-files el) 0)
        form-data (js/FormData.)
        _ (.append form-data "file" file)]
    {:form-data form-data
     :file file}))

(defn generate-file-key [{:keys [title username]}]
  (str title "-" username "-"))

(defn submit-image
  [data input-id]
  (let [{:keys [form-data file]} (get-image-data input-id)
        file-key (generate-file-key data)]
    (assoc
     (handle-image-data file file-key)
     :file form-data)))

(defn image-selector
  [input-id]
  (let [UPLOADED-IMAGE (r/atom nil)]
    (fn []
      [:div
       [:input {:type "file"
                :id input-id
                :value @UPLOADED-IMAGE
                :on-change #(reset! UPLOADED-IMAGE (-> % .-target .-value))
                :accept "image/*"}]
       [:label {:htmlFor input-id}
        (if @UPLOADED-IMAGE
          "File added 🥳"
          "Upload image")]])))

(defn file-creation-handler [data input-id]
  (let [image-data (submit-image data input-id)
        submit-data (merge data image-data)]
    (re-frame/dispatch
     [::events/create-file submit-data])))

(defn file-form []
  (let [form-values (re-frame/subscribe [::subs/file-form-values])
        image-picker-input-id "woooo"]
    (fn []
      [:form {:onSubmit (fn [e]
                          (do (.preventDefault e)
                              (file-creation-handler @form-values image-picker-input-id)))}
       [:label {:htmlFor "title-input"}
        "Title"]
       [:input {:type "text"
                :id "title-input"
                :value (:title @form-values)
                :on-change #(re-frame/dispatch [::events/set-file-form-field-value
                                                :title (-> % .-target .-value)])}]
       [:label {:htmlFor "username-input"}
        "Username"]
       [:input {:type "text"
                :id "username-input"
                :value (:username @form-values)
                :on-change #(re-frame/dispatch [::events/set-file-form-field-value
                                                :username (-> % .-target .-value)])}]
       [image-selector image-picker-input-id]
       [:button {:type "submit"
                 :on-click #()}
        "Submit"]])))
