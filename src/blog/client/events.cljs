(ns blog.client.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [clojure.string :as string]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   {:forms {:file-form {:title nil
                        :username nil
                        :file-url nil}}}))

(re-frame/reg-event-db
 ::set-file-form-field-value
 (fn [db [_ field-path new-value]]
   (assoc-in db [:forms :file-form field-path] new-value)))

(re-frame/reg-event-fx
 ::create-file
 (fn [{:keys [db]} [_ form-data]]
   (let [{:keys [file-type file-key file-size]} form-data]
     {:db (assoc db :loading true)
      :http-xhrio {:method :post
                   :uri "http://localhost:4000/v1/s3/generate"
                   :format (ajax/json-request-format)
                   :timeout 8000
                   :params {:file-type file-type
                            :file-key file-key
                            :file-size (js/parseFloat file-size)}
                   :with-credentials true
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success [::s3-url-success form-data]
                   :on-failure [::s3-url-failure]}})))

(re-frame/reg-event-fx
 ::s3-url-failure
 (fn [{:keys [db]} [_ _response]]
   {:db (assoc db :loading false)}))

#_(defn get-file-extension
    "A function to get your file's extension in case you want it :)"
    [file-name]
    (subs file-name
          (+ 1 (.lastIndexOf file-name "."))
          (count file-name)))

(re-frame/reg-event-fx
 ::s3-url-success
 (fn [{:keys [db]} [_ form-data response]]
   (let [s3-url (:s3/url response)
         params-index (string/index-of s3-url "?")
         file-url (subs s3-url 0 params-index)]
     {:db (assoc db :loading false)
      :dispatch [::upload-file (merge response form-data {:file-url file-url})]})))

(re-frame/reg-event-fx
 ::upload-file
 (fn [_ [_ data]]
   {:http-xhrio {:method :put
                 :uri (:s3/url data)
                 :timeout 8000
                 :body (.get (:file data) "file")
                 :headers {"Content-Type" "image/*"}
                 :response-format (ajax/raw-response-format)
                 :on-success [::upload-image-success data]
                 :on-failure [::s3-url-failure]}}))

(re-frame/reg-event-fx
 ::upload-image-success
 (fn [_ [_ data _response]]
   {:dispatch [::send-server-uploaded-file data]}))

(re-frame/reg-event-fx
 ::send-server-uploaded-file
 (fn [{:keys [db]} [_ {:keys [file-url title username]}]]
   {:db (assoc db :loading true)
    :http-xhrio {:method :post
                 :uri "http://localhost:4000/v1/s3/store"
                 :format (ajax/json-request-format)
                 :timeout 8000
                 :params {:file-url file-url
                          :title title
                          :username username}
                 :with-credentials true
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::store-success]
                 :on-failure [::s3-url-failure]}}))

(re-frame/reg-event-fx
 ::store-success
 (fn [{:keys [db]} [_ response]]
   (cljs.pprint/pprint response)
   {:db (assoc db :loading false)}))
